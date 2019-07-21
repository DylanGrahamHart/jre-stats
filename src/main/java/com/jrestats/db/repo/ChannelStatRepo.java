package com.jrestats.db.repo;

import com.jrestats.db.entity.ChannelStat;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChannelStatRepo extends CrudRepository<ChannelStat, Long> {

    ChannelStat findTopByOrderByCreatedAtDesc();

}
