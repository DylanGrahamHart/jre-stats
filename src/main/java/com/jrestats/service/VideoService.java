package com.jrestats.service;

import com.jrestats.controller.MainController;
import com.jrestats.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoService {

    private Logger logger = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    YouTubeApiService apiService;

    @Value("${jrestats.pagesOfVideosToGet:50}")
    Integer pagesOfVideosToGet;

    private Map<String, Object> getPlaylistItems(String nextPageToken) {
        return apiService.get("playlistItems",
                "playlistId", "UUzQUP1qoWDoEbmsQxvdjxgQ",
                "part", "snippet",
                "maxResults", "50",
                "pageToken", nextPageToken
        );
    }

    private List<Map<String, Object>> getAllPlaylistItems() {
        List<Map<String, Object>> allPlaylistItems = new ArrayList<>();

        Map<String, Object> playlistItems = getPlaylistItems(null);
        allPlaylistItems.add(playlistItems);

        int totalResults = DataUtil.getInteger("pageInfo.totalResults", playlistItems);
        String nextPageToken = DataUtil.getString("nextPageToken", playlistItems);

        for (int i = 0; i < totalResults / pagesOfVideosToGet; i++) {
            playlistItems = getPlaylistItems(nextPageToken);
            nextPageToken = DataUtil.getString("nextPageToken", playlistItems);
            allPlaylistItems.add(playlistItems);
        }

        return allPlaylistItems;
    }

    @Cacheable("allVideos")
    public List<Map<String, String>> getAllVideos() {
        List<Map<String, String>> allVideos = new ArrayList<>();

        for (Map<String, Object> playlistItem : getAllPlaylistItems()) {
            List<String> videoIds = new ArrayList<>();

            for (Map<String, Object> playlistItemItem : DataUtil.getList("items", playlistItem)) {
                videoIds.add(DataUtil.getString("snippet.resourceId.videoId", playlistItemItem));
            }

            Map<String, Object> videos = apiService.get("videos",
                    "id", String.join(",", videoIds),
                    "part", "snippet,statistics",
                    "maxResults", "50"
            );

            for (Map<String, Object> videoItem : DataUtil.getList("items", videos)) {
                Map<String, String> video = new HashMap<>();

                video.put("id", DataUtil.getString("id", videoItem));
                video.put("imgSrc", DataUtil.getString("snippet.thumbnails.high.url", videoItem));
                video.put("title", DataUtil.getString("snippet.title", videoItem));

                video.put("likeCount", formatNumber(DataUtil.getString("statistics.likeCount", videoItem)));
                video.put("dislikeCount", formatNumber(DataUtil.getString("statistics.dislikeCount", videoItem)));
                video.put("viewCount", formatNumber(DataUtil.getString("statistics.viewCount", videoItem)));

                video.put("publishedAt", formatDate(DataUtil.getString("snippet.publishedAt", videoItem)));

                allVideos.add(video);
            }
        }

        return allVideos;
    }

    private String formatNumber(String numberToFormat) {
        return numberToFormat;
    }

    private String formatDate(String dateToFormat) {
        return dateToFormat;
    }
}

