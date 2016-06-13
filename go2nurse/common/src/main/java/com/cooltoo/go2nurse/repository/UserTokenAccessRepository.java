package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.UserType;
import com.cooltoo.go2nurse.entities.UserTokenAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/2/16.
 */
public interface UserTokenAccessRepository extends JpaRepository<UserTokenAccessEntity, Long> {

    List<UserTokenAccessEntity> findTokenAccessByUserIdAndUserType(long userId, UserType userType);
    List<UserTokenAccessEntity> findTokenAccessByToken(String token);

}
