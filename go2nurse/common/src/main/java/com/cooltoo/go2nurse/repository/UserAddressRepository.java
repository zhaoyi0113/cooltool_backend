package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hp on 2016/7/2.
 */
public interface UserAddressRepository extends JpaRepository<UserAddressEntity, Long> {
}
