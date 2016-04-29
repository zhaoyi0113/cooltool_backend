package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseDeviceTokensEntity;
import com.cooltoo.constants.CommonStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 4/28/16.
 */
public interface NurseDeviceTokensRepository extends JpaRepository<NurseDeviceTokensEntity, Long>{

    List<NurseDeviceTokensEntity> findByUserIdAndStatus(long userId, CommonStatus status);
    List<NurseDeviceTokensEntity> findByUserId(long userId);
    List<NurseDeviceTokensEntity> findByUserIdAndDeviceTokenAndStatus(long userId, String token, CommonStatus status);
    List<NurseDeviceTokensEntity> findByDeviceToken(String token);
}
