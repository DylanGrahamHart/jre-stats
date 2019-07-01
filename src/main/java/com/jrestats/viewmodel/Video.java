package com.jrestats.viewmodel;

import com.jrestats.util.DataUtil;
import com.jrestats.util.FormatUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Video {

    public Map<String, Object> item;

    public String id;
    public String imgSrc;
    public String title;
    public String likeCount;
    public String dislikeCount;
    public String viewCount;
    public String publishedAt;

    public Video(Map<String, Object> item) {
        this.item = item;

        this.id = DataUtil.getString("id", item);
        this.imgSrc = DataUtil.getString("snippet.thumbnails.high.url", item);
        this.title = DataUtil.getString("snippet.title", item);

        this.likeCount = FormatUtil.formatNumberAbbr(DataUtil.getString("statistics.likeCount", item));
        this.dislikeCount = FormatUtil.formatNumberAbbr(DataUtil.getString("statistics.dislikeCount", item));
        this.viewCount = FormatUtil.formatNumberAbbr(DataUtil.getString("statistics.viewCount", item));

        this.publishedAt = FormatUtil.formatDate(DataUtil.getString("snippet.publishedAt", item));
    }

    public int getLikeCount() {
        return DataUtil.getInteger("statistics.likeCount", item);
    }

    public int getDislikeCount() {
        return DataUtil.getInteger("statistics.dislikeCount", item);
    }

    public int getViewCount() {
        return DataUtil.getInteger("statistics.viewCount", item);
    }

}
