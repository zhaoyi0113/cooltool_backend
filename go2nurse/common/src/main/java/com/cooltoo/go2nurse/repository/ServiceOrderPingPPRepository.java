package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.constants.AppType;
import com.cooltoo.go2nurse.constants.ChargeType;
import com.cooltoo.go2nurse.entities.ServiceOrderChargePingPPEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/7/15.
 */
public interface ServiceOrderPingPPRepository extends JpaRepository<ServiceOrderChargePingPPEntity, Long>{

    List<ServiceOrderChargePingPPEntity> findByAppTypeAndOrderId(AppType appType, long orderId, Sort sort);
    List<ServiceOrderChargePingPPEntity> findByAppTypeAndOrderIdIn(AppType appType, List<Long> orderIds, Sort sort);
    ServiceOrderChargePingPPEntity findByAppTypeAndPingPPTypeAndPingPPId(AppType appType, ChargeType chargeType, String pingPPId);

    long countByChargeId(String chargeId);
    ServiceOrderChargePingPPEntity findByChargeId(String chargeId);
}
