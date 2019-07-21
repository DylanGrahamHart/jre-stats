package com.jrestats.viewmodel;

import com.jrestats.db.entity.ChannelStat;
import com.jrestats.util.JreUtil;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class Channel {

    public String subscriberCount;
    public Integer subscriberCountRaw;

    public String viewCount;
    public Integer viewCountRaw ;

    public String imgSrc;

    public Channel(Map<String, Object> snippet, ChannelStat channelStat) {
        setImgSrc(snippet);
        setSubscriberCount(channelStat.subscriberCount);
        setViewCount(channelStat.viewCount);
    }

    private void setImgSrc(Map<String, Object> snippet) {
        Map<String, Object> thumbnails = JreUtil.getMap("thumbnails", snippet);
        Map<String, Object> img = JreUtil.getMap("medium", thumbnails);
        imgSrc = (String) img.get("url");
    }

    private void setViewCount(Integer viewCount) {
        this.viewCountRaw = viewCount;
        this.viewCount = formatNumber(this.viewCountRaw);
    }

    private void setSubscriberCount(Integer subscriberCount) {
        this.subscriberCountRaw = subscriberCount;
        this.subscriberCount = formatNumber(this.subscriberCountRaw);
    }

    private String formatNumber(Integer numberToFormat) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return numberFormat.format(numberToFormat.longValue());
    }

}
