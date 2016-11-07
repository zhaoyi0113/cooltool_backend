package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.NurseVisitPatientServiceItemBean;
import com.cooltoo.go2nurse.entities.NurseVisitPatientServiceItemEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/6.
 */
@Component
public class NurseVisitPatientServiceItemBeanConverter implements Converter<NurseVisitPatientServiceItemEntity, NurseVisitPatientServiceItemBean> {
    @Override
    public NurseVisitPatientServiceItemBean convert(NurseVisitPatientServiceItemEntity source) {
        NurseVisitPatientServiceItemBean bean = new NurseVisitPatientServiceItemBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setHospitalId(source.getHospitalId());
        bean.setDepartmentId(source.getDepartmentId());
        bean.setItemName(source.getItemName());
        bean.setItemDescription(source.getItemDescription());
        return bean;
    }
}
