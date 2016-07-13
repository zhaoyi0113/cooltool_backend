package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.entities.ServiceOrderEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/13.
 */
@Component
public class ServiceOrderBeanConverter implements Converter<ServiceOrderEntity, ServiceOrderBean> {
    @Override
    public ServiceOrderBean convert(ServiceOrderEntity source) {
        ServiceOrderBean bean = new ServiceOrderBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setServiceItemId(source.getServiceItemId());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        bean.setAddressId(source.getAddressId());
        bean.setServiceStartTime(source.getServiceStartTime());
        bean.setServiceTimeDuration(source.getServiceTimeDuration());
        bean.setServiceTimeUnit(source.getServiceTimeUnit());
        bean.setTotalConsumption(source.getTotalConsumption());
        bean.setOrderStatus(source.getOrderStatus());
        bean.setPayTime(source.getPayTime());
        bean.setPaymentAmount(source.getPaymentAmount());
        return bean;
    }
}
