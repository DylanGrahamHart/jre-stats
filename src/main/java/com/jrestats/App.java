package com.jrestats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}

@Controller
class MainController {

	@GetMapping("/")
	@ResponseBody
	public String home() {
		return HttpUtil.get("http://derp.jre-stats.com/tits");
	}

    @GetMapping("/tits")
    @ResponseBody
    public String tits() {
        return "I stab you";
    }

}

class HttpUtil {

    private static RestTemplate restTemplate = new RestTemplate();

    public static String get(String url) {
        return restTemplate.getForObject(url, String.class);
	}

}