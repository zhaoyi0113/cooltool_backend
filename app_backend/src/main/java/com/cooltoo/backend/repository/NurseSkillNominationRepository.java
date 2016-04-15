package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSkillNominationEntity;
import com.cooltoo.constants.SocialAbilityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
public interface NurseSkillNominationRepository extends JpaRepository<NurseSkillNominationEntity, Long>{

    List<NurseSkillNominationEntity> findByUserId(long userId);
    List<NurseSkillNominationEntity> findByUserIdAndSkillType(long userId, SocialAbilityType skillType);
    List<NurseSkillNominationEntity> findByUserIdAndSkillIdAndSkillType(long userId, int skillId, SocialAbilityType skillType);
    List<NurseSkillNominationEntity> findByUserIdAndSkillIdAndNominatedIdAndSkillType(long userId, int skillId, long nominatedId, SocialAbilityType skillType);
    List<NurseSkillNominationEntity> findByUserIdAndNominatedId(long userId, long nominatedId);
    List<NurseSkillNominationEntity> findBySkillTypeAndSkillIdIn(SocialAbilityType type, List<Integer> skillIds);
    long countByUserIdAndSkillIdAndSkillType(long userId, int skillId, SocialAbilityType type);
    long countByUserId(long userId);
    void deleteByUserIdOrNominatedIdIn(List<Long> userIds, List<Long> nominatedUserIds);
    void deleteBySkillIdIn(List<Integer> skillIds);
}
