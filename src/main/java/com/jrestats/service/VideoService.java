package com.jrestats.service;

import com.jrestats.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    YouTubeApiService apiService;

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

        for (int i = 0; i < totalResults / 1000; i++) {
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
                Map<String, String> simpleVideo = new HashMap<>();

                simpleVideo.put("id", DataUtil.getString("id", videoItem));
                simpleVideo.put("imgSrc", DataUtil.getString("snippet.thumbnails.high.url", videoItem));
                simpleVideo.put("title", DataUtil.getString("snippet.title", videoItem));
                simpleVideo.put("likeCount", DataUtil.getString("statistics.likeCount", videoItem));
                simpleVideo.put("dislikeCount", DataUtil.getString("statistics.dislikeCount", videoItem));
                simpleVideo.put("viewCount", DataUtil.getString("statistics.viewCount", videoItem));
                simpleVideo.put("publishedAt", DataUtil.getString("snippet.publishedAt", videoItem));

                allVideos.add(simpleVideo);
            }
        }

        return allVideos;
    }
}
