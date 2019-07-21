package com.jrestats.service;

import com.jrestats.db.entity.ChannelStat;
import com.jrestats.db.repo.ChannelStatRepo;
import com.jrestats.util.JreUtil;
import com.jrestats.viewmodel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChannelService {

    @Autowired
    YouTubeApiService apiService;

    @Autowired
    ChannelCacheService channelCacheService;

    @Autowired
    ChannelStatRepo channelStatRepo;

    public Channel getChannel() {
        Map<String, Object> snippet = channelCacheService.getChannelSnippet();
        ChannelStat newestChannelStat = channelStatRepo.findTopByOrderByCreatedAtDesc();
        return new Channel(snippet, newestChannelStat);
    }

    public ChannelStat createChannelStat() {
        Map<String, Object> response = apiService.get("channels",
            "id", "UCzQUP1qoWDoEbmsQxvdjxgQ",
            "part", "statistics"
        );

        Map<String, Object> item = JreUtil.getList("items", response).get(0);
        Map<String, Object> statistics = JreUtil.getMap("statistics", item);

        Integer subscriberCount = Integer.parseInt((String) statistics.get("subscriberCount"));
        Integer viewCount = Integer.parseInt((String) statistics.get("viewCount"));

        return channelStatRepo.save(new ChannelStat(subscriberCount, viewCount));
    }

}
