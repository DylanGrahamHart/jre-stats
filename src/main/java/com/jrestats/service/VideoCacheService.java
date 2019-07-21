package com.jrestats.service;

import com.jrestats.util.JreUtil;
import com.jrestats.viewmodel.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VideoCacheService {

    @Value("${jrestats.pagesOfVideosToGet:50}")
    Integer pagesOfVideosToGet;

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

        Map<String, Object> pageInfo = JreUtil.getMap("pageInfo", playlistItems);
        int totalResults = (Integer) pageInfo.get("totalResults");
        String nextPageToken = (String) playlistItems.get("nextPageToken");

        for (int i = 0; i < totalResults / pagesOfVideosToGet; i++) {
            playlistItems = getPlaylistItems(nextPageToken);
            nextPageToken = (String) playlistItems.get("nextPageToken");
            allPlaylistItems.add(playlistItems);
        }

        return allPlaylistItems;
    }

    @Cacheable("allVideos")
    public List<Video> getAllVideos() {
        List<Video> allVideos = new ArrayList<>();

        for (Map<String, Object> playlistItem : getAllPlaylistItems()) {
            List<String> videoIds = new ArrayList<>();

            for (Map<String, Object> playlistItemItem : JreUtil.getList("items", playlistItem)) {
                Map<String, Object> snippet = JreUtil.getMap("snippet", playlistItemItem);
                Map<String, Object> resourceId = JreUtil.getMap("resourceId", snippet);
                String videoId = (String) resourceId.get("videoId");

                videoIds.add(videoId);
            }

            Map<String, Object> videos = apiService.get("videos",
                    "id", String.join(",", videoIds),
                    "part", "snippet,statistics",
                    "maxResults", "50"
            );

            for (Map<String, Object> videoItem : JreUtil.getList("items", videos)) {
                allVideos.add(new Video(videoItem));
            }
        }

        return allVideos;
    }

}
