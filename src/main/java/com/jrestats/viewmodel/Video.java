package com.jrestats.viewmodel;

import com.jrestats.util.JreUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        Map<String, Object> snippet = JreUtil.getMap("snippet", item);
        Map<String, Object> statistics = JreUtil.getMap("statistics", item);

        title = (String) snippet.get("title");
        id = (String) item.get("id");

        setImgSrc(snippet);
        setPublishedAt(snippet);
        setLikeCount(statistics);
        setDislikeCount(statistics);
        setViewCount(statistics);
    }

    private void setViewCount(Map<String, Object> statistics) {
        dislikeCountRaw = Integer.parseInt((String) statistics.get("dislikeCount"));
        dislikeCount = formatNumber(dislikeCountRaw);
    }

    private void setDislikeCount(Map<String, Object> statistics) {
        viewCountRaw = Integer.parseInt((String) statistics.get("viewCount"));
        viewCount = formatNumber(viewCountRaw);
    }

    private void setLikeCount(Map<String, Object> statistics) {
        likeCountRaw = Integer.parseInt((String) statistics.get("likeCount"));
        likeCount = formatNumber(likeCountRaw);
    }

    private void setPublishedAt(Map<String, Object> snippet) {
        publishedAtRaw = (String) snippet.get("publishedAt");
        publishedAt = formatDate(publishedAtRaw);
    }

    private void setImgSrc(Map<String, Object> snippet) {
        Map<String, Object> thumbnails = JreUtil.getMap("thumbnails", snippet);
        Map<String, Object> high = JreUtil.getMap("high", thumbnails);
        imgSrc = (String) high.get("url");
    }

    private String formatNumber(int numberToFormat) {
        String formattedNumber = Integer.toString(numberToFormat);
        float number = (float) numberToFormat;

        if (formattedNumber.length() > 9) {
            formattedNumber = String.format("%.1f", (Math.floor(number / Math.pow(10, 9)))) + 'B';
        } else if (formattedNumber.length() > 6) {
            formattedNumber = String.format("%.1f", Math.floor((number / Math.pow(10, 6)))) + 'M';
        } else if (formattedNumber.length() > 3) {
            formattedNumber = String.format("%.0f", Math.floor((number / Math.pow(10, 3)))) + 'K';
        }

        return formattedNumber;
    }

    private String formatDate(String dateToFormat) {
        String formattedDate = dateToFormat;

        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = inputDateFormat.parse(dateToFormat);
            formattedDate = (new SimpleDateFormat("MMM, dd yyyy")).format(date).toString();
        } catch (ParseException e) {
            System.out.println("Date error: " + e.getMessage());
        }

        return formattedDate;
    }

}
