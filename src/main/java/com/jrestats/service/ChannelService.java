package com.jrestats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChannelService {

    @Autowired
    YouTubeApiService apiService;

    @Cacheable("channel")
    public Map<String, Object> getChannel() {
        Map<String, Object> channel = apiService.get("channels",
                "id", "UCzQUP1qoWDoEbmsQxvdjxgQ",
                "part", "snippet,statistics"
        );

        return channel;
    }

}
