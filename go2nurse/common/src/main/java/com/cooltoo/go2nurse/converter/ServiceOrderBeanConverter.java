package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.beans.UserAddressBean;
import com.cooltoo.go2nurse.entities.ServiceOrderEntity;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/13.
 */
@Component
public class ServiceOrderBeanConverter implements Converter<ServiceOrderEntity, ServiceOrderBean> {

    @Autowired private Go2NurseUtility go2NurseUtility;

    @Override
    public ServiceOrderBean convert(ServiceOrderEntity source) {
        ServiceOrderBean bean = new ServiceOrderBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        if (null!=source.getServiceItem()) {
            ServiceItemBean serviceItem = go2NurseUtility.parseJsonBean(source.getServiceItem(), ServiceItemBean.class);
            bean.setServiceItem(serviceItem);
        }
        bean.setUserId(source.getUserId());
        if (null!=source.getPatient()) {
            PatientBean patient = go2NurseUtility.parseJsonBean(source.getPatient(), PatientBean.class);
            bean.setPatient(patient);
        }
        if (null!=source.getAddress()) {
            UserAddressBean address = go2NurseUtility.parseJsonBean(source.getAddress(), UserAddressBean.class);
            bean.setAddress(address);
        }
        bean.setServiceStartTime(source.getServiceStartTime());
        bean.setServiceTimeDuration(source.getServiceTimeDuration());
        bean.setServiceTimeUnit(source.getServiceTimeUnit());
        bean.setTotalConsumptionCent(source.getTotalConsumptionCent());
        bean.setOrderStatus(source.getOrderStatus());
        bean.setPayTime(source.getPayTime());
        bean.setPaymentAmountCent(source.getPaymentAmountCent());
        return bean;
    }
}
