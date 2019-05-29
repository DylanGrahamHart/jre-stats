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

	@GetMapping("/")
	public ModelAndView home() {
	    ModelAndView mav = new ModelAndView("home");

        Map<String, Object> channel = YouTubeApiService.get("channels",
                "id", "UCzQUP1qoWDoEbmsQxvdjxgQ",
                "part", "snippet,statistics"
        );

        mav.addObject("channel", channel);
        return mav;
	}

}

class HttpUtil {

    private static RestTemplate restTemplate = new RestTemplate();

    public static Map<String, Object> get(String url) {
        return restTemplate.getForObject(url, Map.class);
    }

}

class YouTubeApiService {

    private static ObjectMapper mapper = new ObjectMapper();

    private static String API_KEY = "AIzaSyAa31jop5ZIsuF4eUNYvS1dRNFUYdNaOmw";
    private static String API_HOST = "https://www.googleapis.com/youtube/v3/";

    public static Map<String, Object> get(String resource, String... params) {
        String url = API_HOST + resource + "?key=" + API_KEY + "&";

        for (int i = 0; i < params.length - 1; i++) {
            url += params[i] + "=" + params[i+1] + "&";
        }

        return HttpUtil.get(url);
    }

}


