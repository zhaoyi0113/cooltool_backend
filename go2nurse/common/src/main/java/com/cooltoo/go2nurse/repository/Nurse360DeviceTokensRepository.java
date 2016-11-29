package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.DeviceType;
import com.cooltoo.go2nurse.entities.Nurse360DeviceTokensEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 2016/11/29.
 */
public interface Nurse360DeviceTokensRepository extends JpaRepository<Nurse360DeviceTokensEntity, Long> {

    List<Nurse360DeviceTokensEntity> findByUserIdAndDeviceTypeAndDeviceTokenAndStatus(long userId, DeviceType type, String token, CommonStatus status);
    List<Nurse360DeviceTokensEntity> findByUserIdAndDeviceTokenAndStatus(long userId, String token, CommonStatus status);
    List<Nurse360DeviceTokensEntity> findByUserIdInAndStatus(List<Long> userId, CommonStatus status);
    List<Nurse360DeviceTokensEntity> findByDeviceTypeAndDeviceToken(DeviceType type, String token);
}
