package com.jrestats.service;

import com.jrestats.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoService {

    @Autowired
    YouTubeApiService apiService;

    @Cacheable("allVideos")
    public List<Map<String, String>> getAllVideos() {
        List<Map<String, Object>> allPlaylistItems = new ArrayList<>();

        Map<String, Object> playlistItems = apiService.get("playlistItems",
                "playlistId", "UUzQUP1qoWDoEbmsQxvdjxgQ",
                "part", "snippet",
                "maxResults", "50"
        );
        allPlaylistItems.add(playlistItems);

        // int totalResults = ((Integer) Util.getStringFromMap("pageInfo.totalResults", playlistItems)).intValue();
        String nextPageToken = Util.getStringFromMap("nextPageToken", playlistItems);

        for (int i = 0; i < 2000 / 1000; i++) {
            playlistItems = apiService.get("playlistItems",
                    "playlistId", "UUzQUP1qoWDoEbmsQxvdjxgQ",
                    "part", "snippet",
                    "maxResults", "50",
                    "pageToken", nextPageToken
            );

            nextPageToken = Util.getStringFromMap("nextPageToken", playlistItems);
            allPlaylistItems.add(playlistItems);
        }

        List<Map<String, String>> allVideos = new ArrayList<>();
        for (Map<String, Object> playlistItem : allPlaylistItems) {
            List<String> videoIdsChunk = new ArrayList<>();

            for (Map<String, Object> video : Util.getList("items", playlistItem)) {
                videoIdsChunk.add(
                        Util.getStringFromMap("snippet.resourceId.videoId", video)
                );
            }

            Map<String, Object> videos = apiService.get("videos",
                    "id", String.join(",", videoIdsChunk),
                    "part", "snippet,statistics",
                    "maxResults", "50"
            );

            for (Map<String, Object> video : Util.getList("items", videos)) {
                Map<String, String> simpleVideo = new HashMap<>();

                String id = Util.getStringFromMap("id", video);
                String imgSrc = Util.getStringFromMap("snippet.thumbnails.high.url", video);
                String title = Util.getStringFromMap("snippet.title", video);
                String likeCount = Util.getStringFromMap("statistics.likeCount", video);
                String dislikeCount = Util.getStringFromMap("statistics.dislikeCount", video);
                String viewCount = Util.getStringFromMap("statistics.viewCount", video);
                String publishedAt = Util.getStringFromMap("snippet.publishedAt", video);

                simpleVideo.put("id", id);
                simpleVideo.put("imgSrc", imgSrc);
                simpleVideo.put("title", title);
                simpleVideo.put("likeCount", likeCount);
                simpleVideo.put("dislikeCount", dislikeCount);
                simpleVideo.put("viewCount", viewCount);
                simpleVideo.put("publishedAt", publishedAt);

                allVideos.add(simpleVideo);
            }
        }

        return allVideos;
    }
}
