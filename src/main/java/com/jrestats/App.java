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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

        mav.addObject("prevPage", pageOffset != 0 ? pageOffset - 1 : null);
        mav.addObject("nextPage", pageOffset + 1);

        mav.addObject("channel", apiService.getChannel());
        mav.addObject("videos", apiService.getAllVideos().get(pageOffset));

        return mav;
	}
}

@Service
class YouTubeApiService {

    private RestTemplate restTemplate = new RestTemplate();

    private String JRE_ID = "UCzQUP1qoWDoEbmsQxvdjxgQ";
    private String API_KEY = "AIzaSyDEEwGOwUujh6rA6gWQnQRUw2-Uyfx1OOI";
    private String API_HOST = "https://www.googleapis.com/youtube/v3/";

    public Map<String, Object> get(String resource, String... params) {
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

        return allVideos;
    }

}
