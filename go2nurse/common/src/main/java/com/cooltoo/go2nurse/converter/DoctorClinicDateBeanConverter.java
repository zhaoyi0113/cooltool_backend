package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.DoctorClinicDateBean;
import com.cooltoo.go2nurse.entities.DoctorClinicDateEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/8/4.
 */
@Component
public class DoctorClinicDateBeanConverter implements Converter<DoctorClinicDateEntity, DoctorClinicDateBean> {
    @Override
    public DoctorClinicDateBean convert(DoctorClinicDateEntity source) {
        DoctorClinicDateBean bean = new DoctorClinicDateBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setDoctorId(source.getDoctorId());
        bean.setClinicDate(source.getClinicDate());
        return bean;
    }
}
