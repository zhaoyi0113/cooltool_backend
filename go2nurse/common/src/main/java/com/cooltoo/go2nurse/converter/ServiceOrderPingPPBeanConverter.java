package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.ServiceOrderPingPPBean;
import com.cooltoo.go2nurse.entities.ServiceOrderPingPPEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/15.
 */
@Component
public class ServiceOrderPingPPBeanConverter implements Converter<ServiceOrderPingPPEntity, ServiceOrderPingPPBean> {
    @Override
    public ServiceOrderPingPPBean convert(ServiceOrderPingPPEntity source) {
        ServiceOrderPingPPBean bean = new ServiceOrderPingPPBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setAppType(source.getAppType());
        bean.setOrderId(source.getOrderId());
        bean.setPingPPType(source.getPingPPType());
        bean.setPingPPId(source.getPingPPId());
        bean.setPingPPJson(source.getPingPPJson());
        return bean;
    }
}
