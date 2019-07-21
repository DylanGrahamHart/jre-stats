package com.jrestats.db.repo;

import com.jrestats.db.entity.ChannelStatEntity;
import org.springframework.data.repository.CrudRepository;

public interface ChannelStatRepo extends CrudRepository<ChannelStatEntity, Long> {

    ChannelStatEntity findTopByOrderByCreatedAtDesc();

}
