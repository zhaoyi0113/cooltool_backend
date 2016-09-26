package com.cooltoo.go2nurse.converter;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.go2nurse.beans.DoctorAppointmentBean;
import com.cooltoo.go2nurse.beans.DoctorBean;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.entities.DoctorAppointmentEntity;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/8/8.
 */
@Component
public class DoctorAppointmentBeanConverter implements Converter<DoctorAppointmentEntity, DoctorAppointmentBean> {

    @Autowired private Go2NurseUtility utility;

    @Override
    public DoctorAppointmentBean convert(DoctorAppointmentEntity source) {
        DoctorAppointmentBean bean = new DoctorAppointmentBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        if (null!=source.getPatientJson()) {
            PatientBean patient = utility.parseJsonBean(source.getPatientJson(), PatientBean.class);
            bean.setPatient(patient);
        }
        bean.setOrderNo(source.getOrderNo());
        bean.setOrderStatus(source.getOrderStatus());
        bean.setHospitalId(source.getHospitalId());
        if (null!=source.getHospitalJson()) {
            HospitalBean hospital = utility.parseJsonBean(source.getHospitalJson(), HospitalBean.class);
            bean.setHospital(hospital);
        }
        bean.setDepartmentId(source.getDepartmentId());
        if (null!=source.getDepartmentJson()) {
            HospitalDepartmentBean department = utility.parseJsonBean(source.getDepartmentJson(), HospitalDepartmentBean.class);
            bean.setDepartment(department);
        }
        bean.setDoctorId(source.getDoctorId());
        if (null!=source.getDepartmentJson()) {
            DoctorBean doctor = utility.parseJsonBean(source.getDoctorJson(), DoctorBean.class);
            bean.setDoctor(doctor);
        }
        bean.setClinicDateId(source.getClinicDateId());
        bean.setClinicDate(source.getClinicDate());
        bean.setClinicHoursId(source.getClinicHoursId());
        bean.setClinicHoursStart(source.getClinicHoursStart());
        bean.setClinicHoursEnd(source.getClinicHoursEnd());
        bean.setScore(source.getScore());
        return bean;
    }
}
