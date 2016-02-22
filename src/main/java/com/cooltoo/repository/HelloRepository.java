package com.cooltoo.repository;

import com.cooltoo.entities.HelloEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by yzzhao on 2/22/16.
 */
public interface HelloRepository extends CrudRepository<HelloEntity, Long> {
}
