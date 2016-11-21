package com.cooltoo.go2nurse.converter;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.entities.ServiceOrderEntity;
import com.cooltoo.util.JSONUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by hp on 2016/7/13.
 */
@Component
public class ServiceOrderBeanConverter implements Converter<ServiceOrderEntity, ServiceOrderBean> {

    private JSONUtil jsonUtil = JSONUtil.newInstance();

    @Override
    public ServiceOrderBean convert(ServiceOrderEntity source) {
        ServiceOrderBean bean = new ServiceOrderBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());

        bean.setServiceItemId(source.getServiceItemId());
        if (null!=source.getServiceItem()) {
            ServiceItemBean serviceItem = jsonUtil.parseJsonBean(source.getServiceItem(), ServiceItemBean.class);
            bean.setServiceItem(serviceItem);
        }

        bean.setVendorType(source.getVendorType());
        bean.setVendorId(source.getVendorId());
        if (null!=source.getVendor()) {
            if (ServiceVendorType.HOSPITAL.equals(bean.getVendorType())) {
                HospitalBean vendorHospital = jsonUtil.parseJsonBean(source.getVendor(), HospitalBean.class);
                bean.setVendorHospital(vendorHospital);
            }
            else if (ServiceVendorType.COMPANY.equals(bean.getVendorType())) {
                ServiceVendorBean vendor = jsonUtil.parseJsonBean(source.getVendor(), ServiceVendorBean.class);
                bean.setVendor(vendor);
            }
        }
        bean.setVendorDepartId(source.getVendorDepartId());
        if (null!=source.getVendorDepart()) {
            if (ServiceVendorType.HOSPITAL.equals(bean.getVendorType())) {
                HospitalDepartmentBean vendorDepart = jsonUtil.parseJsonBean(source.getVendorDepart(), HospitalDepartmentBean.class);
                bean.setVendorHospitalDepart(vendorDepart);
            }
        }

        bean.setCategoryId(source.getCategoryId());
        if(null!=source.getCategory()) {
            ServiceCategoryBean serviceCategory = jsonUtil.parseJsonBean(source.getCategory(), ServiceCategoryBean.class);
            bean.setCategory(serviceCategory);
        }

        bean.setTopCategoryId(source.getTopCategoryId());
        if (null!=source.getTopCategory()) {
            ServiceCategoryBean serviceTopCategory = jsonUtil.parseJsonBean(source.getTopCategory(), ServiceCategoryBean.class);
            bean.setTopCategory(serviceTopCategory);
        }

        bean.setUserId(source.getUserId());

        bean.setPatientId(source.getPatientId());
        if (null!=source.getPatient()) {
            PatientBean patient = jsonUtil.parseJsonBean(source.getPatient(), PatientBean.class);
            bean.setPatient(patient);
        }

        bean.setAddressId(source.getAddressId());
        if (null!=source.getAddress()) {
            UserAddressBean address = jsonUtil.parseJsonBean(source.getAddress(), UserAddressBean.class);
            bean.setAddress(address);
        }

        bean.setServiceStartTime(source.getServiceStartTime());
        bean.setServiceTimeDuration(source.getServiceTimeDuration());
        bean.setServiceTimeUnit(source.getServiceTimeUnit());
        bean.setTotalConsumptionCent(source.getTotalConsumptionCent());
        bean.setPreferentialCent(source.getPreferentialCent());
        bean.setOrderNo(source.getOrderNo());
        bean.setOrderStatus(source.getOrderStatus());
        bean.setPayTime(source.getPayTime());
        bean.setPaymentAmountCent(source.getPaymentAmountCent());
        bean.setLeaveAMessage(source.getLeaveAMessage());

        int itemCount = 0;
        if (null!=bean.getServiceItem()) {
            int itemPrice = bean.getServiceItem().getServicePriceCent();
            if (itemPrice>0 && bean.getTotalConsumptionCent()>0) {
                itemCount = bean.getTotalConsumptionCent()/itemPrice;
            }
        }
        bean.setItemCount(itemCount);
        bean.setScore(source.getScore());
        if (null!=source.getCompletedTime()) {
            bean.setCompletedTime(source.getCompletedTime());
        }
        else {
            bean.setCompletedTime(new Date(0));
        }

        return bean;
    }
}
