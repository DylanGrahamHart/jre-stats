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
    public int likeCountRaw;

    public String dislikeCount;
    public int dislikeCountRaw;

    public String viewCount;
    public int viewCountRaw;

    public String publishedAt;
    public String publishedAtRaw;

    public Video(Map<String, Object> item) {
        this.id = DataUtil.getString("id", item);
        this.imgSrc = DataUtil.getString("snippet.thumbnails.high.url", item);
        this.title = DataUtil.getString("snippet.title", item);

        this.likeCount = FormatUtil.formatNumberAbbr(DataUtil.getString("statistics.likeCount", item));
        this.likeCountRaw = DataUtil.getInteger("statistics.likeCount", item);

        this.dislikeCount = FormatUtil.formatNumberAbbr(DataUtil.getString("statistics.dislikeCount", item));
        this.dislikeCountRaw = DataUtil.getInteger("statistics.dislikeCount", item);

        this.viewCount = FormatUtil.formatNumberAbbr(DataUtil.getString("statistics.viewCount", item));
        this.viewCountRaw = DataUtil.getInteger("statistics.viewCount", item);

        this.publishedAt = FormatUtil.formatDate(DataUtil.getString("snippet.publishedAt", item));
        this.publishedAtRaw = DataUtil.getString("snippet.publishedAt", item);
    }

}
