package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSkillRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
public interface NurseSkillRelationRepository extends JpaRepository<NurseSkillRelationEntity, Integer>{

    List<NurseSkillRelationEntity> findSkillRelationByUserId(long userId);

}
