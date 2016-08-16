package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.converter.UserOpenAppEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 8/14/16.
 */
public interface UserOpenAppRepository extends JpaRepository<UserOpenAppEntity, Long>{

    List<UserOpenAppEntity> findByUnionid(String unionId);
}
