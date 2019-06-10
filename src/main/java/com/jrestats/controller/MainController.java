package com.jrestats.controller;

import com.jrestats.service.ChannelService;
import com.jrestats.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    ChannelService channelService;

    @Autowired
    VideoService videoService;

    @GetMapping("/")
    public ModelAndView home(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "publishedAt") String sort
    ) {
        ModelAndView mav = new ModelAndView("home");

        List<Map<String, String>> videos = videoService.getAllVideos();
        videos.sort((video1, video2) -> sortVideos(sort, video1, video2));
        mav.addObject("videos", videos.subList(page * 50, page * 50 + 50));

        mav.addObject("channel", channelService.getChannel());
        mav.addObject("pagination", getPagination(page, videos.size()));

        return mav;
    }

    private Map<String, Integer> getPagination(int page, int videoSize) {
        Map<String, Integer> pagination = new HashMap<>();

        pagination.put("prev", page > 1 ? page - 1 : null);
        pagination.put("home", page == 1 ? page - 1 : null);
        pagination.put("next", page * 50 < videoSize - 50 ? page + 1 : null);

        return pagination;
    }

    private int sortVideos(String sort, Map<String, String> video1, Map<String, String> video2) {
        int compareFlag = 0;

        int likeCount1 = Integer.valueOf(video1.get("likeCount")).intValue();
        int likeCount2 = Integer.valueOf(video2.get("likeCount")).intValue();
        int dislikeCount1 = Integer.valueOf(video1.get("dislikeCount")).intValue();
        int dislikeCount2 = Integer.valueOf(video2.get("dislikeCount")).intValue();
        int viewCount1 = Integer.valueOf(video1.get("viewCount")).intValue();
        int viewCount2 = Integer.valueOf(video2.get("viewCount")).intValue();

        if ("publishedAt".equals(sort)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String dateStr1 = video1.get("publishedAt");
            String dateStr2 = video2.get("publishedAt");

            try {
                long date1 = dateFormat.parse(dateStr1).getTime();
                long date2 = dateFormat.parse(dateStr2).getTime();

                if (date1 > date2) {
                    compareFlag = -1;
                } else if (date2 > date1) {
                    compareFlag = 1;
                }
            } catch (ParseException e) {
                logger.error("Date parsing problem: " + e.getMessage());
            }
        }

        if ("viewCount".equals(sort)) {
            if (viewCount1 > viewCount2) {
                compareFlag = -1;
            } else if (viewCount2 > viewCount1) {
                compareFlag = 1;
            }
        }

        if ("likeCount".equals(sort)) {
            if (likeCount1 > likeCount2) {
                compareFlag = -1;
            } else if (likeCount2 > likeCount1) {
                compareFlag = 1;
            }
        }

        if ("dislikeCount".equals(sort)) {
            if (dislikeCount1 > dislikeCount2) {
                compareFlag = -1;
            } else if (dislikeCount2 > dislikeCount1) {
                compareFlag = 1;
            }
        }

        if ("likesPerView".equals(sort)) {
            double likesPerView1 = (double) likeCount1 / (double) viewCount1;
            double likesPerView2 = (double) likeCount2 / (double) viewCount2;

            if (likesPerView1 > likesPerView2) {
                compareFlag = -1;
            } else if (likesPerView1 < likesPerView2) {
                compareFlag = 1;
            }
        }

        if ("dislikesPerView".equals(sort)) {
            double dislikesPerView1 = (double) dislikeCount1 / (double) viewCount1;
            double dislikesPerView2 = (double) dislikeCount2 / (double) viewCount2;

            if (dislikesPerView1 > dislikesPerView2) {
                compareFlag = -1;
            } else if (dislikesPerView1 < dislikesPerView2) {
                compareFlag = 1;
            }
        }

        return compareFlag;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected String error() {
        return "Error";
    }
}
