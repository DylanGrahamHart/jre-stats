package com.jrestats.service;

import com.jrestats.db.entity.VideoEntity;
import com.jrestats.db.repo.VideoRepo;
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

    @Autowired
    VideoRepo videoRepo;

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

    public List<List<String>> getAllVideoIdsFromApi() {
        List<List<String>> videoIds = new ArrayList<>();
        List<VideoEntity> allVideoEntities = new ArrayList<>();

        for (Map<String, Object> playlistItem : getAllPlaylistItems()) {
            List<String> videoIdsChunk = new ArrayList<>();

            for (Map<String, Object> playlistItemItem : JreUtil.getList("items", playlistItem)) {
                Map<String, Object> snippet = JreUtil.getMap("snippet", playlistItemItem);
                Map<String, Object> resourceId = JreUtil.getMap("resourceId", snippet);
                String videoId = (String) resourceId.get("videoId");

                videoIdsChunk.add(videoId);

                VideoEntity videoEntity = videoRepo.findTopByVideoId(videoId);
                if (videoEntity == null) {
                    allVideoEntities.add(new VideoEntity(videoId));
                }
            }

            videoIds.add(videoIdsChunk);
        }

        videoRepo.save(allVideoEntities);
        return videoIds;
    }

    public List<List<String>> getAllVideoIdsFromEntities() {
        List<List<String>> videoIds = new ArrayList<>();
        List<String> videoIdsChunk = new ArrayList<>();

        Integer chunkCount = 0;
        for (VideoEntity video : videoRepo.findAll()) {
            videoIdsChunk.add(video.videoId);

            if (++chunkCount == 50) {
                videoIds.add(videoIdsChunk);
                videoIdsChunk = new ArrayList<>();
                chunkCount = 0;
            }
        }

        return videoIds;
    }

    public List<List<String>> getAllVideoIds() {
        List<List<String>> videoIds = new ArrayList<>();

        if (videoRepo.findAll().size() > 0) {
            videoIds = getAllVideoIdsFromEntities();
        } else {
            videoIds = getAllVideoIdsFromApi();
        }

        return videoIds;
    }

    @Cacheable("allVideos")
    public List<Video> getAllVideos() {
        List<Video> allVideos = new ArrayList<>();

        for (List<String> videoIdsChunk : getAllVideoIds()) {
            Map<String, Object> videos = apiService.get("videos",
                "id", String.join(",", videoIdsChunk),
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
