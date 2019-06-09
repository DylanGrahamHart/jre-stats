package com.jrestats;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.nio.channels.Channel;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableCaching
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}

@Controller
class MainController {

    @Autowired
    YouTubeApiService apiService;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@GetMapping("/")
	public ModelAndView home(@RequestParam(defaultValue = "0") int pageOffset) {
	    ModelAndView mav = new ModelAndView("home");

        mav.addObject("videos", apiService.getAllVideos().get(pageOffset));
        mav.addObject("channel", apiService.getChannel());

        mav.addObject("prevPage", pageOffset > 1 ? pageOffset - 1 : null);
        mav.addObject("prevHomePage", pageOffset == 1 ? pageOffset - 1 : null);
        mav.addObject("nextPage", pageOffset < apiService.getAllVideos().size()-1 ? pageOffset + 1 : null);

        return mav;
	}

    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected String error() {
        return "Error";
    }
}

@Service
class YouTubeApiService {

    private RestTemplate restTemplate = new RestTemplate();

    private String JRE_ID = "UCzQUP1qoWDoEbmsQxvdjxgQ";
    private String API_KEY = "AIzaSyDEEwGOwUujh6rA6gWQnQRUw2-Uyfx1OOI";
    private String API_HOST = "https://www.googleapis.com/youtube/v3/";

    private Map<String, Object> get(String resource, String... params) {
        String url = API_HOST + resource + "?key=" + API_KEY + "&";

        for (int i = 0; i < params.length - 1; i += 2) {
            url += params[i] + "=" + params[i+1] + "&";
        }

        return restTemplate.getForObject(url, Map.class);
    }

    @Cacheable("channel")
    public Map<String, Object> getChannel() {
        Map<String, Object> channel = get("channels",
                "id", JRE_ID,
                "part", "snippet,statistics"
        );

        return channel;
    }

    @Cacheable("allVideos")
    public List<Map<String, Object>> getAllVideos() {
        List<Map<String, Object>> allVideos = new ArrayList<>();

        Map<String, Object> videosChunk = get("playlistItems",
                "playlistId", "UUzQUP1qoWDoEbmsQxvdjxgQ",
                "part", "snippet",
                "maxResults", "50"
        );
        allVideos.add(videosChunk);

        Map<String, Object> pageInfo = (Map<String, Object>) videosChunk.get("pageInfo");
        int totalResults = ((Integer) pageInfo.get("totalResults")).intValue();

        String nextPageToken = (String) videosChunk.get("nextPageToken");
        for (int i = 0; i < totalResults / 1000; i++) {
            videosChunk = get("playlistItems",
                    "playlistId", "UUzQUP1qoWDoEbmsQxvdjxgQ",
                    "part", "snippet",
                    "maxResults", "50",
                    "pageToken", nextPageToken
            );

            nextPageToken = (String) videosChunk.get("nextPageToken");
            allVideos.add(videosChunk);
        }

        List<Map<String, Object>> allRealVideos = new ArrayList<>();
        for (Map<String, Object> videosChunkDerp : allVideos) {
            List<String> videoIdsChunk = new ArrayList<>();
            List<Map<String, Object>> items = (List<Map<String, Object>>) videosChunkDerp.get("items");

            for (Map<String, Object> video : items) {
                Map<String, Object> snippet = (Map<String, Object>) video.get("snippet");
                Map<String, Object> resourceId = (Map<String, Object>) snippet.get("resourceId");
                videoIdsChunk.add((String) resourceId.get("videoId"));
            }

            Map<String, Object> realVideosChunk = get("videos",
                    "id", String.join(",", videoIdsChunk),
                    "part", "snippet,statistics",
                    "maxResults", "50"
            );

            allRealVideos.add(realVideosChunk);
        }

        return allRealVideos;
    }

//    private void transformVideos(Map<String, Object> videosChunk) {
//        List<Map<String, Object>> items = (List<Map<String, Object>>) videosChunk.get("items");
//
//        for (Map<String, Object> video : items) {
//            Map<String, Object> snippet = (Map<String, Object>) video.get("snippet");
//            Map<String, Object> statistics = (Map<String, Object>) video.get("statistics");
//        }
//    }

}
