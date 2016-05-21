package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseIntegrationEntity;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SocialAbilityType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by yzzhao on 5/21/16.
 */
public interface NurseIntegrationRepository extends CrudRepository<NurseIntegrationEntity, Long> {

    List<NurseIntegrationEntity> findByUserIdAndAbilityTypeAndAbilityIdAndStatus(long userId, SocialAbilityType type, long abilityId, CommonStatus status);
    List<NurseIntegrationEntity> findByAbilityTypeAndReasonIdAndStatus(SocialAbilityType type, long reasonId, CommonStatus status);

}
