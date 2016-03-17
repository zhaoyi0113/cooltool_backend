package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSkillNorminationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
public interface NurseSkillNorminationRepository extends JpaRepository<NurseSkillNorminationEntity, Long>{

    long countByUserIdAndSkillId(long userId, int skillId);

    List<NurseSkillNorminationEntity> findByUserIdAndSkillIdAndNominatedId(long userId, int skillId, long nominatedId);

    long countByuserId(long userId);
}
