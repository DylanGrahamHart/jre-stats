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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
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

    private Logger logger = LoggerFactory.getLogger(MainController.class);

	@GetMapping("/")
	public ModelAndView home(
	        @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "publishedAt") String sort
    ) {
	    ModelAndView mav = new ModelAndView("home");

	    List<Map<String, Object>> allVideos = apiService.getAllVideos();

        allVideos.sort((videoOne, videoTwo) -> {
            return sortVideos(videoOne, videoTwo, sort);
        });

        mav.addObject("videos", allVideos.subList(page*50, page*50+50));
        mav.addObject("channel", apiService.getChannel());

        mav.addObject("prevPage", page > 1 ? page - 1 : null);
        mav.addObject("prevHomePage", page == 1 ? page - 1 : null);
        mav.addObject("nextPage", page < apiService.getAllVideos().size()-1 ? page + 1 : null);

        return mav;
	}

	private int sortVideos(Map<String, Object> videoOne, Map<String, Object> videoTwo, String sort) {
        int compareFlag = 0;

        int likeCount1 = Integer.valueOf((String) ((Map<String, Object>) videoOne.get("statistics")).get("likeCount")).intValue();
        int likeCount2 = Integer.valueOf((String) ((Map<String, Object>) videoTwo.get("statistics")).get("likeCount")).intValue();
        int dislikeCount1 = Integer.valueOf((String) ((Map<String, Object>) videoOne.get("statistics")).get("dislikeCount")).intValue();
        int dislikeCount2 = Integer.valueOf((String) ((Map<String, Object>) videoTwo.get("statistics")).get("dislikeCount")).intValue();
        int viewCount1 = Integer.valueOf((String) ((Map<String, Object>) videoOne.get("statistics")).get("viewCount")).intValue();
        int viewCount2 = Integer.valueOf((String) ((Map<String, Object>) videoTwo.get("statistics")).get("viewCount")).intValue();

        if ("publishedAt".equals(sort)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String dateStr1 = (String) ((Map<String, Object>) videoOne.get("snippet")).get("publishedAt");
            String dateStr2 = (String) ((Map<String, Object>) videoTwo.get("snippet")).get("publishedAt");

            try {
                long date1 = dateFormat.parse(dateStr1).getTime();
                long date2 = dateFormat.parse(dateStr2).getTime();

                if (date1 > date2) {
                    compareFlag = -1;
                } else if (date2 > date1) {
                    compareFlag = 1;
                }
            } catch (ParseException e) {
                logger.error("Date parsing problem: " + e.getMessage());
            }
        }

        if ("viewCount".equals(sort)) {
            if (viewCount1 > viewCount2) {
                compareFlag = -1;
            } else if (viewCount2 > viewCount1) {
                compareFlag = 1;
            }
        }

        if ("likeCount".equals(sort)) {
            if (likeCount1 > likeCount2) {
                compareFlag = -1;
            } else if (likeCount2 > likeCount1) {
                compareFlag = 1;
            }
        }

        if ("dislikeCount".equals(sort)) {
            if (dislikeCount1 > dislikeCount2) {
                compareFlag = -1;
            } else if (dislikeCount2 > dislikeCount1) {
                compareFlag = 1;
            }
        }

        if ("likesPerView".equals(sort)) {
            double likesPerView1 = (double) likeCount1 / (double) viewCount1;
            double likesPerView2 = (double) likeCount2 / (double) viewCount2;

            if (likesPerView1 > likesPerView2) {
                compareFlag = -1;
            } else if (likesPerView1 < likesPerView2) {
                compareFlag = 1;
            }
        }

        if ("dislikesPerView".equals(sort)) {
            double dislikesPerView1 = (double) dislikeCount1 / (double) viewCount1;
            double dislikesPerView2 = (double) dislikeCount2 / (double) viewCount2;

            if (dislikesPerView1 > dislikesPerView2) {
                compareFlag = -1;
            } else if (dislikesPerView1 < dislikesPerView2) {
                compareFlag = 1;
            }
        }

        return compareFlag;
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

            List<Map<String, Object>> realVideoItems = (List<Map<String, Object>>) realVideosChunk.get("items");
            for (Map<String, Object> video : realVideoItems) {
                allRealVideos.add(video);
            }
        }

        return allRealVideos;
    }

    private void getSimplifiedVideo(Map<String, Object> videosChunk) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) videosChunk.get("items");

        for (Map<String, Object> video : items) {
            Map<String, Object> snippet = (Map<String, Object>) video.get("snippet");
            Map<String, Object> statistics = (Map<String, Object>) video.get("statistics");
        }
    }

}
