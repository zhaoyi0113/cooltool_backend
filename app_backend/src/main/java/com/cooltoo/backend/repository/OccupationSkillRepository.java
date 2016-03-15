package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.OccupationSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
public interface OccupationSkillRepository extends JpaRepository<OccupationSkillEntity, Integer>{

    List<OccupationSkillEntity> findByName(String name);


//    @Query("select skill from OccupationSkillEntity  skill, NurseSkillRelationEntity  relation where relation.userId = :userId and relation.skillId = skill.id")
//    Page<OccupationSkillEntity> findSkillNormation(@Param("userId") long userId, PageRequest request);


//    @Query("select skill from OccupationSkillEntity skill, NurseSkillRelationEntity relation where ")
//    Page<OccupationSkillEntity> findSkillNormination(@Param("userId") long userId);
}
