package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.ServiceOrderChargePingPPBean;
import com.cooltoo.go2nurse.entities.ServiceOrderChargePingPPEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/15.
 */
@Component
public class ServiceOrderPingPPBeanConverter implements Converter<ServiceOrderChargePingPPEntity, ServiceOrderChargePingPPBean> {
    @Override
    public ServiceOrderChargePingPPBean convert(ServiceOrderChargePingPPEntity source) {
        ServiceOrderChargePingPPBean bean = new ServiceOrderChargePingPPBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setOrderId(source.getOrderId());
        bean.setAppType(source.getAppType());
        bean.setChargeType(source.getChargeType());
        bean.setChargeId(source.getChargeId());
        bean.setChargeJson(source.getChargeJson());
        bean.setWebhooksEventId(source.getWebhooksEventId());
        bean.setWebhooksEventJson(source.getWebhooksEventJson());
        bean.setChargeStatus(source.getChargeStatus());
        return bean;
    }
}
