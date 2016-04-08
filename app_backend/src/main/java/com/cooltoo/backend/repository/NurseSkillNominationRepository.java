package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSkillNominationEntity;
import com.cooltoo.constants.OccupationSkillType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
public interface NurseSkillNominationRepository extends JpaRepository<NurseSkillNominationEntity, Long>{

    List<NurseSkillNominationEntity> findByUserId(long userId);

    List<NurseSkillNominationEntity> findByUserIdAndSkillType(long userId, OccupationSkillType skillType);

    long countByUserIdAndSkillIdAndSkillType(long userId, int skillId, OccupationSkillType type);

    List<NurseSkillNominationEntity> findByUserIdAndSkillIdAndNominatedIdAndSkillType(long userId, int skillId, long nominatedId, OccupationSkillType skillType);

    List<NurseSkillNominationEntity> findByUserIdAndNominatedId(long userId, long nominatedId);

    long countByUserId(long userId);
}
