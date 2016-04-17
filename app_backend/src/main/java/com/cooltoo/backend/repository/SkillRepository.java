package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.SkillEntity;
import com.cooltoo.constants.OccupationSkillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
public interface SkillRepository extends JpaRepository<SkillEntity, Integer>{
    List<SkillEntity> findByName(String name);
    List<SkillEntity> findByStatus(OccupationSkillStatus status);
    Page<SkillEntity> findByStatus(OccupationSkillStatus status, Pageable page);
    long countByStatus(OccupationSkillStatus status);
}
