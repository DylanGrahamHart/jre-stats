package com.jrestats.db.repo;

import com.jrestats.db.entity.ChannelStatEntity;
import com.jrestats.db.entity.VideoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VideoRepo extends CrudRepository<VideoEntity, Long> {

    List<VideoEntity> findAll();
    VideoEntity findTopByVideoId(String videoId);

}