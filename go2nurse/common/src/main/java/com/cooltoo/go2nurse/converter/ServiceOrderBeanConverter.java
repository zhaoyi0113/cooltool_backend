package com.cooltoo.go2nurse.converter;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.entities.ServiceOrderEntity;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import org.hibernate.type.SerializableToBlobType;
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

        bean.setServiceItemId(source.getServiceItemId());
        if (null!=source.getServiceItem()) {
            ServiceItemBean serviceItem = go2NurseUtility.parseJsonBean(source.getServiceItem(), ServiceItemBean.class);
            bean.setServiceItem(serviceItem);
        }

        bean.setVendorType(source.getVendorType());
        bean.setVendorId(source.getVendorId());
        if (null!=source.getVendor()) {
            if (ServiceVendorType.HOSPITAL.equals(bean.getVendorType())) {
                HospitalBean vendorHospital = go2NurseUtility.parseJsonBean(source.getVendor(), HospitalBean.class);
                bean.setVendorHospital(vendorHospital);
            }
            else if (ServiceVendorType.COMPANY.equals(bean.getVendorType())) {
                ServiceVendorBean vendor = go2NurseUtility.parseJsonBean(source.getVendor(), ServiceVendorBean.class);
                bean.setVendor(vendor);
            }
        }

        bean.setCategoryId(source.getCategoryId());
        if(null!=source.getCategory()) {
            ServiceCategoryBean serviceCategory = go2NurseUtility.parseJsonBean(source.getCategory(), ServiceCategoryBean.class);
            bean.setCategory(serviceCategory);
        }

        bean.setTopCategoryId(source.getTopCategoryId());
        if (null!=source.getTopCategory()) {
            ServiceCategoryBean serviceTopCategory = go2NurseUtility.parseJsonBean(source.getTopCategory(), ServiceCategoryBean.class);
            bean.setTopCategory(serviceTopCategory);
        }

        bean.setUserId(source.getUserId());

        bean.setPatientId(source.getPatientId());
        if (null!=source.getPatient()) {
            PatientBean patient = go2NurseUtility.parseJsonBean(source.getPatient(), PatientBean.class);
            bean.setPatient(patient);
        }

        bean.setAddressId(source.getAddressId());
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
        bean.setLeaveAMessage(source.getLeaveAMessage());

        return bean;
    }
}
