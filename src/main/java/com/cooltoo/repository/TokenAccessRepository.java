package com.cooltoo.repository;

import com.cooltoo.entities.TokenAccessEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by yzzhao on 3/2/16.
 */
public interface TokenAccessRepository extends CrudRepository<TokenAccessEntity, Long> {
}
