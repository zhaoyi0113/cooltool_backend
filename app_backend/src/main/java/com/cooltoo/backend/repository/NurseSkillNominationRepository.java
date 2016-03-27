package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSkillNominationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
public interface NurseSkillNominationRepository extends JpaRepository<NurseSkillNominationEntity, Long>{

    List<NurseSkillNominationEntity> findByUserId(long userId);

    long countByUserIdAndSkillId(long userId, int skillId);

    List<NurseSkillNominationEntity> findByUserIdAndSkillIdAndNominatedId(long userId, int skillId, long nominatedId);

    long countByuserId(long userId);
}
