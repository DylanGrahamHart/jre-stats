package com.jrestats.db.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ChannelStat {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    public Integer viewCount;
    public Integer subscriberCount;

    public ChannelStat() {

    }

    public ChannelStat(Integer subscriberCount, Integer viewCount) {
        this.createdAt = new Date();
        this.viewCount = viewCount;
        this.subscriberCount = subscriberCount;
    }
}