package com.jrestats.viewmodel;

import com.jrestats.util.JreUtil;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class Channel {

    public String subscriberCount;
    public String viewCount;

    public Channel(Map<String, Object> channelsResponse) {
        Map<String, Object> statistics = JreUtil.getMap(
                "statistics",
                JreUtil.getList("items", channelsResponse).get(0)
        );

        setSubscriberCount(statistics);
        setViewCount(statistics);
    }

    public void setSubscriberCount(Map<String, Object> statistics) {
        this.subscriberCount = formatNumber(
                Integer.parseInt((String) statistics.get("subscriberCount"))
        );
    }
    public void setViewCount(Map<String, Object> statistics) {
        this.viewCount = formatNumber(
                Integer.parseInt((String) statistics.get("viewCount"))
        );
    }

    private String formatNumber(Integer numberToFormat) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return numberFormat.format(numberToFormat.longValue());
    }

}
