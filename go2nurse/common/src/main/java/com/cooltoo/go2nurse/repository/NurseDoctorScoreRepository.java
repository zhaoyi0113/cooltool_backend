package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.UserType;
import com.cooltoo.go2nurse.constants.ReasonType;
import com.cooltoo.go2nurse.entities.NurseDoctorScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/9/27.
 */
public interface NurseDoctorScoreRepository extends JpaRepository<NurseDoctorScoreEntity, Long> {

    List<NurseDoctorScoreEntity> findByReceiverTypeAndReceiverIdAndUserIdAndReasonTypeAndReasonId(UserType receiverType, long receiverId, long userId, ReasonType reasonType, long reasonId);

    List<NurseDoctorScoreEntity> findByReceiverTypeAndReceiverIdIn(UserType receiverType, List<Long> receiverIds);
    List<NurseDoctorScoreEntity> findByReceiverTypeAndReceiverId(UserType receiverType, long receiverId);
}
