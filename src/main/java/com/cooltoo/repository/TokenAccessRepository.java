package com.cooltoo.repository;

import com.cooltoo.entities.TokenAccessEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/2/16.
 */
public interface TokenAccessRepository extends CrudRepository<TokenAccessEntity, Long> {

    List<TokenAccessEntity> findTokenAccessByUserId(long userId);

    List<TokenAccessEntity> findTokenAccessByToken(String token);
}
