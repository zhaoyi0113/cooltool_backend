package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseMessageEntity;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.constants.SuggestionStatus;
import com.cooltoo.constants.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/5/21.
 */
public interface NurseMessageRepository extends JpaRepository<NurseMessageEntity, Long> {
    long countByUserTypeAndUserIdAndStatusIn(UserType userType, long userId, List<SuggestionStatus> statuses);
    List<NurseMessageEntity> findByUserTypeAndUserIdAndStatusIn(UserType userType, long userId, List<SuggestionStatus> statuses, Sort sort);
    Page<NurseMessageEntity> findByUserTypeAndUserIdAndStatusIn(UserType userType, long userId, List<SuggestionStatus> statuses, Pageable page);
    NurseMessageEntity findByUserIdAndUserTypeAndAbilityTypeAndAbilityIdAndReasonId(long userId, UserType userType, SocialAbilityType type, int abilityId, long reasonId);
    List<NurseMessageEntity> findByUserIdAndAbilityTypeAndAbilityIdAndStatus(long userId, SocialAbilityType type, int abilityId, CommonStatus status);
    List<NurseMessageEntity> findByStatusAndAbilityTypeAndReasonId(CommonStatus status, SocialAbilityType type, long reasonId);
    List<NurseMessageEntity> findByStatusAndAbilityTypeAndReasonIdIn(CommonStatus status, SocialAbilityType type, List<Long> reasonIds);
    List<NurseMessageEntity> findByStatusAndUserTypeAndUserId(CommonStatus status, UserType userType, long userId, Sort sort);
}
