package com.jrestats.viewmodel;

import com.jrestats.util.DataUtil;
import com.jrestats.util.FormatUtil;

import java.util.Map;

public class Channel {

    public String subscriberCount;
    public String viewCount;

    public Channel(Map<String, Object> channelsResponse) {
        Map<String, Object> channelItem = DataUtil.getList("items", channelsResponse).get(0);
        this.subscriberCount = FormatUtil.formatNumber(DataUtil.getInteger("statistics.subscriberCount", channelItem));
        this.viewCount = FormatUtil.formatNumber(DataUtil.getInteger("statistics.viewCount", channelItem));
    }

}
