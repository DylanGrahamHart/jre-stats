package com.jrestats.db.repo;

import com.jrestats.db.entity.Derp;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DerpRepo extends CrudRepository<Derp, Long> {

    List<Derp> findAll();

}
