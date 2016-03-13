package com.cooltoo.repository;

import com.cooltoo.entities.OccupationSkillEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
public interface OccupationSkillRepository extends CrudRepository<OccupationSkillEntity, Integer>{

    List<OccupationSkillEntity> findByName(String name);


    @Query("select skill from OccupationSkillEntity  skill, NurseSkillRelationEntity  relation where relation.userId = :userId and relation.skillId = skill.id")
    List<OccupationSkillEntity> findSkillEntityByUserId(@Param("userId") long userId);

}
