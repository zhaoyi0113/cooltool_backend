package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.ServiceOrderChargeBean;
import com.cooltoo.go2nurse.entities.ServiceOrderChargeEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/15.
 */
@Component
public class ServiceOrderChargeBeanConverter implements Converter<ServiceOrderChargeEntity, ServiceOrderChargeBean> {
    @Override
    public ServiceOrderChargeBean convert(ServiceOrderChargeEntity source) {
        ServiceOrderChargeBean bean = new ServiceOrderChargeBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setOrderId(source.getOrderId());
        bean.setOrderNo(source.getOrderNo());
        bean.setPaymentPlatform(source.getPaymentPlatform());
        bean.setChannel(source.getChannel());
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
