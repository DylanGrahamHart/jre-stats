package com.jrestats;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}

@Controller
class MainController {

    private String JRE_ID = "UCzQUP1qoWDoEbmsQxvdjxgQ";

	@GetMapping("/")
	public ModelAndView home() {
	    ModelAndView mav = new ModelAndView("home");

        Map<String, Object> channel = YouTubeApiService.get("channels",
                "id", JRE_ID,
                "part", "snippet,statistics"
        );

        Map<String, Object> videos = YouTubeApiService.get("search",
                "channelId", JRE_ID,
                "part", "snippet",
                "order", "date",
                "maxResults", "50",
                "type", "video"
        );

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

        for (int i = 0; i < params.length - 1; i++) {
            url += params[i] + "=" + params[i+1] + "&";
        }

        return restTemplate.getForObject(url, Map.class);
    }

}


