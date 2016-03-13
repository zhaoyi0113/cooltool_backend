package com.cooltoo.repository;

import com.cooltoo.entities.NurseSkillRelationEntity;
import com.cooltoo.entities.OccupationSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
public interface NurseSkillRelationRepository extends JpaRepository<NurseSkillRelationEntity, Integer>{

    List<NurseSkillRelationEntity> findSkillRelationByUserId(long userId);

}
