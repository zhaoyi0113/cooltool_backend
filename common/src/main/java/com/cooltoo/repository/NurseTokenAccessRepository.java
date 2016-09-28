package com.cooltoo.repository;

import com.cooltoo.entities.NurseTokenAccessEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/2/16.
 */
public interface NurseTokenAccessRepository extends CrudRepository<NurseTokenAccessEntity, Long> {

    List<NurseTokenAccessEntity> findTokenAccessByUserId(long userId);

    List<NurseTokenAccessEntity> findTokenAccessByToken(String token);

}
