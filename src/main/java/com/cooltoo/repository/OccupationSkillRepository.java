package com.cooltoo.repository;

import com.cooltoo.entities.OccupationSkillEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
public interface OccupationSkillRepository extends CrudRepository<OccupationSkillEntity, Integer>{

    List<OccupationSkillEntity> findByName(String name);
}
