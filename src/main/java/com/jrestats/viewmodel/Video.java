package com.jrestats.viewmodel;

import com.jrestats.util.DataUtil;

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

        this.likeCount = formatNumber(DataUtil.getString("statistics.likeCount", videoItem));
        this.dislikeCount = formatNumber(DataUtil.getString("statistics.dislikeCount", videoItem));
        this.viewCount = formatNumber(DataUtil.getString("statistics.viewCount", videoItem));

        this.publishedAt = formatDate(DataUtil.getString("snippet.publishedAt", videoItem));
    }

    private String formatNumber(String numberToFormat) {
        String formattedNumber = numberToFormat;
        float number = Float.parseFloat(numberToFormat);

        if (numberToFormat.length() > 9) {
            formattedNumber = String.format("%.1f", (Math.floor(number / Math.pow(10, 9)))) + 'B';
        } else if (numberToFormat.length() > 6) {
            formattedNumber = String.format("%.1f", Math.floor((number / Math.pow(10, 6)))) + 'M';
        } else if (numberToFormat.length() > 3) {
            formattedNumber = String.format("%.0f", Math.floor((number / Math.pow(10, 3)))) + 'K';
        }

        return formattedNumber;
    }

    private String formatDate(String dateToFormat) {
        String formattedDate = dateToFormat;

        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = inputDateFormat.parse(dateToFormat);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-");

            formattedDate = inputDateFormat.parse(dateToFormat).toString();
        } catch (ParseException e) {}

        return formattedDate;
    }

}
