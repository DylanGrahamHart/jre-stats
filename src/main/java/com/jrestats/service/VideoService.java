package com.jrestats.service;

import com.jrestats.controller.MainController;
import com.jrestats.util.DataUtil;
import com.jrestats.util.FormatUtil;
import com.jrestats.viewmodel.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public List<Video> getAllVideos() {
        List<Video> allVideos = new ArrayList<>();

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
                allVideos.add(new Video(videoItem));
            }
        }

        return allVideos;
    }

    public List<Video> getSortedSubList(List<Video> allVideos, String page, String sortKey) {
        int p = Integer.parseInt(page);
        int to = p * 50;
        int from = p * 50 + 50;

        if (!sortKey.isEmpty()) {
            System.out.println("I stab");
        }

        return allVideos.subList(to, from);
    }

    public Map<String, Object> getControls(int numberOfVideos, String page, String sortKey) {
        Map<String, Object> controls = new HashMap<>();
        int p = Integer.parseInt(page);
        int maxPages = numberOfVideos / 50;

        if (p == 1) {
            if (!"".equals(sortKey)) {
                controls.put("prev", "?sort=" + sortKey);
            } else {
                controls.put("prev", "/");
            }
        }
        if (p > 1) {
            String controlString = "?page=" + (p-1);

            if (!"".equals(sortKey)) {
                controlString += "&sort=" + sortKey;
            }

            controls.put("prev", controlString);
        }
        if (p < maxPages-1) {
            String controlString = "?page=" + (p+1);

            if (!"".equals(sortKey)) {
                controlString += "&sort=" + sortKey;
            }

            controls.put("next", controlString);
        }

        controls.put("sort", sortKey);
        return controls;
    }

}

