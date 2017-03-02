package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.entities.ServiceItemEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/13.
 */
@Component
public class ServiceItemBeanConverter implements Converter<ServiceItemEntity, ServiceItemBean> {
    @Override
    public ServiceItemBean convert(ServiceItemEntity source) {
        ServiceItemBean bean = new ServiceItemBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setVendorId(source.getVendorId());
        bean.setVendorType(source.getVendorType());
        bean.setCategoryId(source.getCategoryId());
        bean.setName(source.getName());
        bean.setClazz(source.getClazz());
        bean.setDescription(source.getDescription());
        bean.setImageId(source.getImageId());
        bean.setDetailImageId(source.getDetailImageId());
        bean.setServicePriceCent(source.getServicePriceCent());
        bean.setServiceTimeDuration(source.getServiceTimeDuration());
        bean.setServiceTimeUnit(source.getServiceTimeUnit());
        bean.setGrade(source.getGrade());
        bean.setVendorDepartId(source.getVendorDepartId());
        bean.setServiceDiscountCent(source.getServiceDiscountCent());
        bean.setServerIncomeCent(source.getServerIncomeCent());
        bean.setNeedVisitPatientRecord(source.getNeedVisitPatientRecord());
        bean.setManagerApproved(source.getManagerApproved());
        bean.setManagedBy(source.getManagedBy());
        bean.setNeedSymptoms(source.getNeedSymptoms());
        bean.setSymptomsItems(source.getSymptomsItems());
        bean.setQuestionnaireId(source.getQuestionnaireId());
        return bean;
    }
}
