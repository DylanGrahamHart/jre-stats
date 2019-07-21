package com.jrestats.db.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "videos")
public class VideoEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    public String videoId;

    public VideoEntity() {

    }

    public VideoEntity(String videoId) {
        this.videoId = videoId;
    }

}
