package com.jrestats.service;

import com.jrestats.db.entity.ChannelStatEntity;
import com.jrestats.db.repo.ChannelStatRepo;
import com.jrestats.util.JreUtil;
import com.jrestats.viewmodel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChannelService {

    @Autowired
    YouTubeApiService apiService;

    @Autowired
    ChannelService channelService;

    @Autowired
    ChannelCacheService channelCacheService;

    @Autowired
    ChannelStatRepo channelStatRepo;

    public Channel getChannel() {
        Map<String, Object> snippet = channelCacheService.getChannelSnippet();
        ChannelStatEntity newestChannelStat = channelStatRepo.findTopByOrderByCreatedAtDesc();

        if (newestChannelStat == null) {
            newestChannelStat = channelService.createChannelStat();
        }

        return new Channel(snippet, newestChannelStat);
    }

    public ChannelStatEntity createChannelStat() {
        Map<String, Object> response = apiService.get("channels",
            "id", "UCzQUP1qoWDoEbmsQxvdjxgQ",
            "part", "statistics"
        );

        Map<String, Object> item = JreUtil.getList("items", response).get(0);
        Map<String, Object> statistics = JreUtil.getMap("statistics", item);

        Integer subscriberCount = Integer.parseInt((String) statistics.get("subscriberCount"));
        Integer viewCount = Integer.parseInt((String) statistics.get("viewCount"));

        return channelStatRepo.save(new ChannelStatEntity(subscriberCount, viewCount));
    }

}
