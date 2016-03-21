package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseOccupationSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
public interface NurseOccuationSkillRepository extends JpaRepository<NurseOccupationSkillEntity, Integer>{

    List<NurseOccupationSkillEntity> findSkillRelationByUserId(long userId);

}
