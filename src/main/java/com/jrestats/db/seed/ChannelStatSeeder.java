package com.jrestats.db.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jrestats.App;
import com.jrestats.db.entity.ChannelStatEntity;
import com.jrestats.db.repo.ChannelStatRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class ChannelStatSeeder {

    private static Logger logger = LoggerFactory.getLogger(ChannelStatSeeder.class);

    @Autowired
    ChannelStatRepo channelStatRepo;

    public void seedChannelStats() {
        String channelStats = "[{\"createdAt\":1563757200000,\"viewCount\":1000,\"subscriberCount\":1000},{\"createdAt\":1563753600000,\"viewCount\":990,\"subscriberCount\":990},{\"createdAt\":1563670800000,\"viewCount\":950,\"subscriberCount\":950},{\"createdAt\":1563757200000,\"viewCount\":900,\"subscriberCount\":900},{\"createdAt\":1561165200000,\"viewCount\":500,\"subscriberCount\":500}]";

        List<Map<String, Object>> channelStatsData = null;

        try {
            channelStatsData = (new ObjectMapper()).readValue(channelStats, List.class);

            for (Map<String, Object> derp : channelStatsData) {
                Long createdAt = (Long) derp.get("createdAt");
                Integer subscriberCount = (Integer) derp.get("subscriberCount");
                Integer viewCount = (Integer) derp.get("viewCount");

                channelStatRepo.save(new ChannelStatEntity(createdAt, subscriberCount, viewCount));
            }
        } catch (IOException e) {
            logger.error("Parsing seed data: " + e.getMessage());
        }

        System.out.println(channelStatsData);
    }

}
