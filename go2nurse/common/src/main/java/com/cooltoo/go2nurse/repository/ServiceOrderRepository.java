package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.ServiceOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hp on 2016/7/13.
 */
public interface ServiceOrderRepository extends JpaRepository<ServiceOrderEntity, Long> {
}
