package com.cooltoo.go2nurse.service;

import com.cooltoo.go2nurse.beans.DoctorAppointmentBean;
import com.cooltoo.go2nurse.beans.DoctorClinicHoursBean;
import com.cooltoo.go2nurse.converter.DoctorAppointmentBeanConverter;
import com.cooltoo.go2nurse.repository.DoctorAppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hp on 2016/8/14.
 */
@Service("DoctorAppointmentService")
public class DoctorAppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorAppointmentService.class);

    @Autowired private DoctorAppointmentRepository repository;
    @Autowired private DoctorAppointmentBeanConverter beanConverter;
    @Autowired private DoctorClinicDateHoursService clinicDateHoursService;

    //=========================================================================
    //                    adding
    //=========================================================================
//    @Transactional
//    public DoctorAppointmentBean appointDoctor(long userId, long patientId, long clinicHoursId) {
//        DoctorClinicHoursBean clinicHoursBean = clinicDateHoursService.getClinicHourById(clinicHoursId);
//
//    }

}
