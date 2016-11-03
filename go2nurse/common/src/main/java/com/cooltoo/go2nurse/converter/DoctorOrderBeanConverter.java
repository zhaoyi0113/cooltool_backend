package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.DoctorOrderBean;
import com.cooltoo.go2nurse.entities.DoctorOrderEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/10/14.
 */
@Component
public class DoctorOrderBeanConverter implements Converter<DoctorOrderEntity, DoctorOrderBean> {
    @Override
    public DoctorOrderBean convert(DoctorOrderEntity source) {
        DoctorOrderBean bean = new DoctorOrderBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setDoctorId(source.getDoctorId());
        bean.setDoctorOrder(source.getDoctorOrder());
        bean.setHospitalId(source.getHospitalId());
        bean.setHospitalOrder(source.getHospitalOrder());
        bean.setDepartmentId(source.getDepartmentId());
        bean.setDepartmentOrder(source.getDepartmentOrder());
        return bean;
    }
}
