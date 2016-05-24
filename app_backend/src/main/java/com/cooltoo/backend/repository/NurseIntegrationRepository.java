package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseIntegrationEntity;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.constants.UserType;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by yzzhao on 5/21/16.
 */
public interface NurseIntegrationRepository extends CrudRepository<NurseIntegrationEntity, Long> {
    List<NurseIntegrationEntity> findByUserIdAndUserTypeAndAbilityTypeAndAbilityIdAndReasonIdIn(long userId, UserType userType, SocialAbilityType type, int abilityId, List<Long> reasonIds);
    NurseIntegrationEntity findByUserIdAndUserTypeAndAbilityTypeAndAbilityIdAndReasonId(long userId, UserType userType, SocialAbilityType type, int abilityId, long reasonId);
    List<NurseIntegrationEntity> findByUserIdAndAbilityTypeAndAbilityIdAndStatus(long userId, SocialAbilityType type, int abilityId, CommonStatus status);
    List<NurseIntegrationEntity> findByStatusAndAbilityTypeAndReasonId(CommonStatus status, SocialAbilityType type, long reasonId);
    long countByStatusAndUserTypeAndUserIdAndAbilityTypeAndReasonIdIn(CommonStatus status, UserType userType, long userId, SocialAbilityType type, List<Long> reasonIds);
    List<NurseIntegrationEntity> findByStatusAndUserTypeAndUserId(CommonStatus status, UserType userType, long userId, Sort sort);
}
