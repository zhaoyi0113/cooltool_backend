package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.constants.AppType;
import com.cooltoo.go2nurse.constants.PingPPType;
import com.cooltoo.go2nurse.entities.ServiceOrderPingPPEntity;
import org.glassfish.jersey.jaxb.internal.XmlCollectionJaxbProvider;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/7/15.
 */
public interface ServiceOrderPingPPRepository extends JpaRepository<ServiceOrderPingPPEntity, Long>{

    List<ServiceOrderPingPPEntity> findByAppTypeAndOrderId(AppType appType, long orderId, Sort sort);

    List<ServiceOrderPingPPEntity> findByAppTypeAndOrderIdIn(AppType appType, List<Long> orderIds, Sort sort);

    ServiceOrderPingPPEntity findByAppTypeAndPingPPTypeAndPingPPId(AppType appType, PingPPType pingPPType, String pingPPId);
}
