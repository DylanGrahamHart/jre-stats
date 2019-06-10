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

    @Cacheable("allVideos")
    public List<Map<String, String>> getAllVideos() {
        List<Map<String, Object>> allPlaylistItems = new ArrayList<>();

        Map<String, Object> playlistItems = apiService.get("playlistItems",
                "playlistId", "UUzQUP1qoWDoEbmsQxvdjxgQ",
                "part", "snippet",
                "maxResults", "50"
        );
        allPlaylistItems.add(playlistItems);

        int totalResults = DataUtil.getInteger("pageInfo.totalResults", playlistItems);
        String nextPageToken = DataUtil.getString("nextPageToken", playlistItems);

        for (int i = 0; i < totalResults / 1000; i++) {
            playlistItems = apiService.get("playlistItems",
                    "playlistId", "UUzQUP1qoWDoEbmsQxvdjxgQ",
                    "part", "snippet",
                    "maxResults", "50",
                    "pageToken", nextPageToken
            );

            nextPageToken = DataUtil.getString("nextPageToken", playlistItems);
            allPlaylistItems.add(playlistItems);
        }

        List<Map<String, String>> allVideos = new ArrayList<>();
        for (Map<String, Object> playlistItem : allPlaylistItems) {
            List<String> videoIdsChunk = new ArrayList<>();

            for (Map<String, Object> video : DataUtil.getList("items", playlistItem)) {
                videoIdsChunk.add(
                        DataUtil.getString("snippet.resourceId.videoId", video)
                );
            }

            Map<String, Object> videos = apiService.get("videos",
                    "id", String.join(",", videoIdsChunk),
                    "part", "snippet,statistics",
                    "maxResults", "50"
            );

            for (Map<String, Object> video : DataUtil.getList("items", videos)) {
                Map<String, String> simpleVideo = new HashMap<>();

                String id = DataUtil.getString("id", video);
                String imgSrc = DataUtil.getString("snippet.thumbnails.high.url", video);
                String title = DataUtil.getString("snippet.title", video);
                String likeCount = DataUtil.getString("statistics.likeCount", video);
                String dislikeCount = DataUtil.getString("statistics.dislikeCount", video);
                String viewCount = DataUtil.getString("statistics.viewCount", video);
                String publishedAt = DataUtil.getString("snippet.publishedAt", video);

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
