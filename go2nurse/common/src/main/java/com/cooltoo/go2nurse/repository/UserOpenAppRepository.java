package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.converter.UserOpenAppEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 8/14/16.
 */
public interface UserOpenAppRepository extends JpaRepository<UserOpenAppEntity, Long>{

    List<UserOpenAppEntity> findByUnionidAndStatus(String unionId, CommonStatus status);
    List<UserOpenAppEntity> findByOpenidAndStatus(String openId, CommonStatus status);
    List<UserOpenAppEntity> findByUnionid(String unionId);
    List<UserOpenAppEntity> findByOpenid(String openId);
    List<UserOpenAppEntity> findByUserId(long userId);
    UserOpenAppEntity findFirstByUserId(long userId);
}
