package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakTopicSubscriberEntity;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hp on 2016/6/2.
 */
public interface NurseSpeakTopicSubscriberRepository extends JpaRepository<NurseSpeakTopicSubscriberEntity, Long> {

    long countByTopicIdAndStatus(long topicId, CommonStatus status);
    Page<NurseSpeakTopicSubscriberEntity> findByTopicIdAndStatus(long topicId, CommonStatus status, Pageable page);

    NurseSpeakTopicSubscriberEntity findByTopicIdAndUserIdAndUserType(long topicId, long userId, UserType userType);
}
