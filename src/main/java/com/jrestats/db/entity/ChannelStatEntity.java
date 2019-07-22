package com.jrestats.db.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "channel_stats")
public class ChannelStatEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    public Integer viewCount;
    public Integer subscriberCount;

    public ChannelStatEntity() {

    }

    public ChannelStatEntity(Integer subscriberCount, Integer viewCount) {
        this.createdAt = new Date();
        this.viewCount = viewCount;
        this.subscriberCount = subscriberCount;
    }

    public ChannelStatEntity(Long createdAt, Integer subscriberCount, Integer viewCount) {
        this.createdAt = new Date(createdAt);
        this.viewCount = viewCount;
        this.subscriberCount = subscriberCount;
    }
}