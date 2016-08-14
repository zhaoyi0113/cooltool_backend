package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.DoctorAppointmentBean;
import com.cooltoo.go2nurse.entities.DoctorAppointmentEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/8/8.
 */
@Component
public class DoctorAppointmentBeanConverter implements Converter<DoctorAppointmentEntity, DoctorAppointmentBean> {
    @Override
    public DoctorAppointmentBean convert(DoctorAppointmentEntity source) {
        DoctorAppointmentBean bean = new DoctorAppointmentBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        bean.setPatientJson(source.getPatientJson());
        bean.setOrderNo(source.getOrderNo());
        bean.setOrderStatus(source.getOrderStatus());
        bean.setHospitalId(source.getHospitalId());
        bean.setHospitalJson(source.getHospitalJson());
        bean.setDepartmentId(source.getDepartmentId());
        bean.setDepartmentJson(source.getDepartmentJson());
        bean.setDoctorId(source.getDoctorId());
        bean.setDoctorJson(source.getDoctorJson());
        bean.setClinicDateId(source.getClinicDateId());
        bean.setClinicDate(source.getClinicDate());
        bean.setClinicHoursId(source.getClinicHoursId());
        bean.setClinicHoursStart(source.getClinicHoursStart());
        bean.setClinicHoursEnd(source.getClinicHoursEnd());
        return bean;
    }
}
