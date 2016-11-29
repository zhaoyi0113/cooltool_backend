package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.DeviceType;
import com.cooltoo.go2nurse.entities.UserDeviceTokensEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/9/13.
 */
public interface UserDeviceTokensRepository extends JpaRepository<UserDeviceTokensEntity, Long> {

    List<UserDeviceTokensEntity> findByUserIdAndDeviceTypeAndDeviceTokenAndStatus(long userId, DeviceType type, String token, CommonStatus status);
    List<UserDeviceTokensEntity> findByUserIdAndDeviceTokenAndStatus(long userId, String token, CommonStatus status);
    List<UserDeviceTokensEntity> findByUserIdInAndStatus(List<Long> userId, CommonStatus status);
    List<UserDeviceTokensEntity> findByDeviceTypeAndDeviceToken(DeviceType type, String token);
}
