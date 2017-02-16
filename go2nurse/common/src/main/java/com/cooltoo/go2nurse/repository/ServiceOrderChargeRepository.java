package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.constants.AppType;
import com.cooltoo.go2nurse.constants.ChargeType;
import com.cooltoo.go2nurse.entities.ServiceOrderChargeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/7/15.
 */
public interface ServiceOrderChargeRepository extends JpaRepository<ServiceOrderChargeEntity, Long>{
    long countByChargeId(String chargeId);

    List<ServiceOrderChargeEntity> findByAppTypeAndOrderId(AppType appType, long orderId, Sort sort);
    List<ServiceOrderChargeEntity> findByAppTypeAndOrderIdIn(AppType appType, List<Long> orderIds, Sort sort);


    List<ServiceOrderChargeEntity> findByAppTypeAndChargeTypeAndOrderId(AppType appType, ChargeType chargeType, long orderId, Sort sort);

    List<ServiceOrderChargeEntity> findByChargeId(String chargeId);
}
