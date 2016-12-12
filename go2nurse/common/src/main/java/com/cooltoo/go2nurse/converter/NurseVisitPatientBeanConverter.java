package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.NurseVisitPatientBean;
import com.cooltoo.go2nurse.entities.NurseVisitPatientEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/6.
 */
@Component
public class NurseVisitPatientBeanConverter implements Converter<NurseVisitPatientEntity, NurseVisitPatientBean> {
    @Override
    public NurseVisitPatientBean convert(NurseVisitPatientEntity source) {
        NurseVisitPatientBean bean = new NurseVisitPatientBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setNurseId(source.getNurseId());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        bean.setServiceItem(source.getServiceItem());
        bean.setVisitRecord(source.getVisitRecord());
        bean.setPatientSign(source.getPatientSign());
        bean.setOrderId(source.getOrderId());
        bean.setVendorType(source.getVendorType());
        bean.setVendorId(source.getVendorId());
        bean.setVendorDepartId(source.getVendorDepartId());
        bean.setNurseSign(source.getNurseSign());
        bean.setPatientRecordNo(null==source.getPatientRecordNo() ? "" : source.getPatientRecordNo());
        bean.setAddress(null==source.getAddress() ? "" : source.getAddress());
        bean.setNote(null==source.getNote() ? "" : source.getNote());
        return bean;
    }
}
