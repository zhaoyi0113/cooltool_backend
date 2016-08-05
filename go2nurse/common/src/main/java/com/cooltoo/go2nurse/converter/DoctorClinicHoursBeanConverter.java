package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.DoctorClinicHoursBean;
import com.cooltoo.go2nurse.entities.DoctorClinicHoursEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/8/4.
 */
@Component
public class DoctorClinicHoursBeanConverter implements Converter<DoctorClinicHoursEntity, DoctorClinicHoursBean> {
    @Override
    public DoctorClinicHoursBean convert(DoctorClinicHoursEntity source) {
        DoctorClinicHoursBean bean = new DoctorClinicHoursBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setDoctorId(source.getDoctorId());
        bean.setClinicDateId(source.getClinicDateId());
        bean.setClinicHourStart(source.getClinicHourStart());
        bean.setClinicHourEnd(source.getClinicHourEnd());
        bean.setNumberCount(source.getNumberCount());
        return bean;
    }
}
