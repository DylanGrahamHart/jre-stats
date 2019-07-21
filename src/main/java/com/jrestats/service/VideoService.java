package com.jrestats.service;

import com.jrestats.viewmodel.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VideoService {

    private Logger logger = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    VideoCacheService videoCacheService;

    public Map<String, Object> getVideoListViewModel(String page, String sortKey) {
        Map<String, Object> videoList = new HashMap<>();

        List<Video> allVideos = videoCacheService.getAllVideos();
        videoList.put("list", getSortedSubList(allVideos, page, sortKey));
        videoList.put("controls", getControls(allVideos.size(), page, sortKey));
        return videoList;
    }

    public List<Video> getSortedSubList(List<Video> allVideos, String page, String sortKey) {
        int p = Integer.parseInt(page);
        int to = p * 50;
        int from = p * 50 + 50;

        if (!sortKey.isEmpty()) {
            allVideos.sort((video1, video2) -> sortVideos(sortKey, video1, video2));
        }

        return allVideos.subList(to, from);
    }

    private int sortVideos(String sort, Video video1, Video video2) {
        int compareFlag = 0;
        long compareValue1 = 0;
        long compareValue2 = 0;

        if (sort.contains("publishedAt")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            try {
                compareValue1 = dateFormat.parse(video1.publishedAtRaw).getTime();
                compareValue2 = dateFormat.parse(video2.publishedAtRaw).getTime();
            } catch (ParseException e) {
                logger.error("Date parsing problem: " + e.getMessage());
            }
        }
        if (sort.contains("viewCount")) {
            compareValue1 = video1.viewCountRaw;
            compareValue2 = video2.viewCountRaw;
        }
        if (sort.contains("likeCount")) {
            compareValue1 = video1.likeCountRaw;
            compareValue2 = video2.likeCountRaw;
        }
        if (sort.contains("dislikeCount")) {
            compareValue1 = video1.dislikeCountRaw;
            compareValue2 = video2.dislikeCountRaw;
        }
        if (sort.contains("likesPerView")) {
            compareValue1 = (int) ((double) video1.likeCountRaw / (double) video1.viewCountRaw * 1000);
            compareValue2 = (int) ((double) video2.likeCountRaw / (double) video2.viewCountRaw * 1000);
        }
        if (sort.contains("dislikesPerView")) {
            compareValue1 = (int) ((double) video1.dislikeCountRaw / (double) video1.viewCountRaw * 1000);
            compareValue2 = (int) ((double) video2.dislikeCountRaw / (double) video2.viewCountRaw * 1000);
        }

        if (compareValue1 > compareValue2) {
            compareFlag = -1;
        } else if (compareValue2 > compareValue1) {
            compareFlag = 1;
        }

        return compareFlag * (sort.contains("-") ? -1 : 1);
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

