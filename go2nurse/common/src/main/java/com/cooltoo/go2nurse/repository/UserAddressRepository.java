package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.UserAddressEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/7/2.
 */
public interface UserAddressRepository extends JpaRepository<UserAddressEntity, Long> {
    List<UserAddressEntity> findByUserId(long userId, Sort sort);
    List<UserAddressEntity> findByIdIn(List<Long> userAddressesId, Sort sort);
}
