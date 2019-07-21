package com.jrestats.db.repo;

import com.jrestats.db.entity.ChannelStatEntity;
import com.jrestats.db.entity.VideoEntity;
import org.springframework.data.repository.CrudRepository;

public interface VideoRepo extends CrudRepository<VideoEntity, Long> {

    VideoEntity findTopByVideoId(String videoId);

}