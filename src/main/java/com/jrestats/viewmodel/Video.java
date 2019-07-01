package com.jrestats.viewmodel;

import com.jrestats.util.DataUtil;
import com.jrestats.util.FormatUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Video {

    public String id;
    public String imgSrc;
    public String title;
    public String likeCount;
    public String dislikeCount;
    public String viewCount;
    public String publishedAt;

    public Video(Map<String, Object> videoItem) {
        this.id = DataUtil.getString("id", videoItem);
        this.imgSrc = DataUtil.getString("snippet.thumbnails.high.url", videoItem);
        this.title = DataUtil.getString("snippet.title", videoItem);

        this.likeCount = FormatUtil.formatNumberAbbr(DataUtil.getString("statistics.likeCount", videoItem));
        this.dislikeCount = FormatUtil.formatNumberAbbr(DataUtil.getString("statistics.dislikeCount", videoItem));
        this.viewCount = FormatUtil.formatNumberAbbr(DataUtil.getString("statistics.viewCount", videoItem));

        this.publishedAt = FormatUtil.formatDate(DataUtil.getString("snippet.publishedAt", videoItem));
    }



}
