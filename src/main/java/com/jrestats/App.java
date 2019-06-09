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
    VideoService videoService;

    @Autowired
    ChannelService channelService;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@GetMapping("/")
	public ModelAndView home() {
	    ModelAndView mav = new ModelAndView("home");

        Map<String, Object> channel = channelService.getChannel();
        Map<String, Object> videos = videoService.getFirst50();

        logger.info("DERP" + channelService.getChannel());
        logger.info("MERP" + channelService.getChannel());

        mav.addObject("channel", channel);
        mav.addObject("videos", videos);
        return mav;
	}
}

class YouTubeApiService {

    private static RestTemplate restTemplate = new RestTemplate();

    private static String API_KEY = "AIzaSyDEEwGOwUujh6rA6gWQnQRUw2-Uyfx1OOI";
    private static String API_HOST = "https://www.googleapis.com/youtube/v3/";

    public static Map<String, Object> get(String resource, String... params) {
        String url = API_HOST + resource + "?key=" + API_KEY + "&";

        for (int i = 0; i < params.length - 1; i += 2) {
            url += params[i] + "=" + params[i+1] + "&";
        }

        return restTemplate.getForObject(url, Map.class);
    }

}

@Service
class ChannelService {

    private String JRE_ID = "UCzQUP1qoWDoEbmsQxvdjxgQ";

    @Cacheable("channel")
    public Map<String, Object> getChannel() {
        Map<String, Object> channel = YouTubeApiService.get("channels",
                "id", JRE_ID,
                "part", "snippet,statistics"
        );

        return channel;
    }
}

@Service
class VideoService {

    @Value("${app.useCache:true}")
    private String useCache;

    public Map<String, Object> getFirst50() {
        Map<String, Object> videos;

        if (Boolean.valueOf(Objects.toString(useCache))) {
            videos = getAll().get(0);
        } else {
            videos = YouTubeApiService.get("playlistItems",
                    "playlistId", "UUzQUP1qoWDoEbmsQxvdjxgQ",
                    "part", "snippet",
                    "maxResults", "50"
            );
        }

        return videos;
    }

    @Cacheable("allVideos")
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> allVideos = new ArrayList<>();

        Map<String, Object> videosChunk = YouTubeApiService.get("playlistItems",
                "playlistId", "UUzQUP1qoWDoEbmsQxvdjxgQ",
                "part", "snippet",
                "maxResults", "50"
        );
        allVideos.add(videosChunk);

        Map<String, Object> pageInfo = (Map<String, Object>) videosChunk.get("pageInfo");
        int totalResults = ((Integer) pageInfo.get("totalResults")).intValue();

        String nextPageToken = (String) videosChunk.get("nextPageToken");
        for (int i = 0; i < totalResults / 50; i++) {
            videosChunk = YouTubeApiService.get("playlistItems",
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
