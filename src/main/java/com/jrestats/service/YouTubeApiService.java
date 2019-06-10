package com.jrestats.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class YouTubeApiService {

    private RestTemplate restTemplate = new RestTemplate();

    private String API_KEY = "AIzaSyDEEwGOwUujh6rA6gWQnQRUw2-Uyfx1OOI";
    private String API_HOST = "https://www.googleapis.com/youtube/v3/";

    public Map<String, Object> get(String resource, String... params) {
        String url = API_HOST + resource + "?key=" + API_KEY + "&";

        for (int i = 0; i < params.length - 1; i += 2) {
            url += params[i] + "=" + params[i+1] + "&";
        }

        return restTemplate.getForObject(url, Map.class);
    }
}
