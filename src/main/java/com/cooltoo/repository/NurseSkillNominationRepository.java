package com.cooltoo.repository;

import com.cooltoo.entities.NurseSkillNominationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by yzzhao on 3/13/16.
 */
public interface NurseSkillNominationRepository extends JpaRepository<NurseSkillNominationEntity, Long>{

    long countByUserIdAndSkillId(long userId, int skillId);
}
