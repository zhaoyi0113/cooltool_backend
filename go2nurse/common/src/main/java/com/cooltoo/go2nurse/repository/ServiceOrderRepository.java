package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.ServiceOrderEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/7/13.
 */
public interface ServiceOrderRepository extends JpaRepository<ServiceOrderEntity, Long> {

    List<ServiceOrderEntity> findByUserId(long userId, Sort sort);

}
