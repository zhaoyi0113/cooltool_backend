package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseAbilityNominationEntity;
import com.cooltoo.constants.SocialAbilityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
public interface NurseAbilityNominationRepository extends JpaRepository<NurseAbilityNominationEntity, Long>{

    List<NurseAbilityNominationEntity> findByUserId(long userId);
    List<NurseAbilityNominationEntity> findByUserIdAndAbilityType(long userId, SocialAbilityType skillType);
    List<NurseAbilityNominationEntity> findByUserIdAndAbilityIdAndAbilityType(long userId, int skillId, SocialAbilityType skillType);
    List<NurseAbilityNominationEntity> findByUserIdAndAbilityIdAndNominatedIdAndAbilityType(long userId, int skillId, long nominatedId, SocialAbilityType skillType);
    List<NurseAbilityNominationEntity> findByUserIdAndNominatedId(long userId, long nominatedId);
    List<NurseAbilityNominationEntity> findByAbilityTypeAndAbilityIdIn(SocialAbilityType type, List<Integer> skillIds);
    long countByUserIdAndAbilityIdAndAbilityType(long userId, int skillId, SocialAbilityType type);
    long countByUserId(long userId);
    void deleteByUserIdOrNominatedIdIn(List<Long> userIds, List<Long> nominatedUserIds);
    void deleteByAbilityIdIn(List<Integer> skillIds);
}
