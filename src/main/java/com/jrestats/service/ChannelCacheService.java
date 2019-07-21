package com.jrestats.service;

import com.jrestats.util.JreUtil;
import com.jrestats.viewmodel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChannelCacheService {

    @Autowired
    YouTubeApiService apiService;

    @Cacheable("channelSnippet")
    public Map<String, Object> getChannelSnippet() {
        Map<String, Object> response = apiService.get("channels",
            "id", "UCzQUP1qoWDoEbmsQxvdjxgQ",
            "part", "snippet"
        );

        Map<String, Object> item = JreUtil.getList("items", response).get(0);
        Map<String, Object> snippet = JreUtil.getMap("snippet", item);
        return snippet;
    }

}
