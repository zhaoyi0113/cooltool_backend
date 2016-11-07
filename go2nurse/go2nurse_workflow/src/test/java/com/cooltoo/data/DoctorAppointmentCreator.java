package com.cooltoo.data;

import com.cooltoo.Application;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.entities.*;
import com.cooltoo.go2nurse.repository.*;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.repository.HospitalRepository;
import com.cooltoo.util.JSONUtil;
import com.cooltoo.util.NumberUtil;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/8/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
@Ignore
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class DoctorAppointmentCreator {

    private static final Logger logger = LoggerFactory.getLogger(DoctorAppointmentCreator.class);

    @Autowired private HospitalDepartmentRepository departmentRepository;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private DoctorClinicDateRepository clinicDateRepository;
    @Autowired private DoctorClinicHoursRepository clinicHoursRepository;
    @Autowired private UserPatientRelationRepository userPatientRelationRepository;
    @Autowired private PatientRepository patientRepository;


    @Test
    public void createDoctorAppointment() {
        JSONUtil jsonUtil = JSONUtil.newInstance();
        Sort sort = new Sort(
                new Sort.Order(Sort.Direction.ASC, "id")
        );
        HospitalEntity hospital = hospitalRepository.findOne(1);
        List<HospitalDepartmentEntity> departments = departmentRepository.findByHospitalId(1, sort);
        List<DoctorEntity> doctors = doctorRepository.findByHospitalIdAndStatusIn(1, CommonStatus.getAll(), sort);
        List<DoctorClinicDateEntity> clinicDates = clinicDateRepository.findAll();
        List<DoctorClinicHoursEntity> clinicHours = clinicHoursRepository.findAll();
        List<UserPatientRelationEntity> userToPatient = userPatientRelationRepository.findAll();
        List<PatientEntity> allPatients = patientRepository.findAll();
        List<OrderStatus> orderStatuses = OrderStatus.getAll();

        List<DoctorAppointmentEntity> appointments = new ArrayList<>();
        long appointmentId = 101;

        HospitalDepartmentEntity department;
        DoctorClinicDateEntity clinicDate;
        DoctorEntity doctor;
        int patientIndex = 0;
        UserPatientRelationEntity userPatientRelation;
        PatientEntity patient;
        int counter = 0;
        int patientRelationCount = userToPatient.size();

        StringBuilder log = new StringBuilder();
        for (DoctorClinicHoursEntity clinicHour : clinicHours) {
            clinicDate = findClinicDate(clinicDates, clinicHour.getClinicDateId());
            doctor = findDoctor(doctors, clinicHour.getDoctorId());
            if (null==doctor || null==clinicDate) { continue; }
            if (doctor.getHospitalId()!=hospital.getId()) { continue; }

            department = findDepartment(departments, doctor.getDepartmentId());
            if (null==department) { continue; }

            log.append("INFO ----- ").append(clinicDate).append("\r\n");
            log.append("INFO ----- ").append(doctor).append("\r\n");
            log.append("INFO ----- ").append(department).append("\r\n");
            userPatientRelation = userToPatient.get(patientIndex%patientRelationCount);
            log.append("INFO ----- ").append(userPatientRelation).append("\r\n");
            patient = findPatient(allPatients, null==userPatientRelation ? 0 : userPatientRelation.getPatientId());
            log.append("INFO ----- ").append(patient).append("\r\n");
            log.append("INFO ----- ").append(patientIndex).append("\r\n");
            while (null==userPatientRelation || null==patient) {
                counter ++;
                if (counter>patientRelationCount) {
                    counter=0;
                    break;
                }
                patientIndex++;
                userPatientRelation = userToPatient.get(patientIndex%patientRelationCount);
                if (null==userPatientRelation) { continue; }
                patient = findPatient(allPatients, userPatientRelation.getPatientId());
            }
            if (userPatientRelation==null || patient==null) {
                continue;
            }

            DoctorAppointmentEntity newOne = new DoctorAppointmentEntity();
            newOne.setId(appointmentId);
            newOne.setTime(new Date(System.currentTimeMillis()));
            newOne.setStatus(CommonStatus.ENABLED);
            newOne.setOrderNo(NumberUtil.getUniqueString());
            newOne.setHospitalId(hospital.getId());
            newOne.setHospitalJson(jsonUtil.toJsonString(hospital));
            newOne.setDepartmentId(department.getId());
            newOne.setDepartmentJson(jsonUtil.toJsonString(department));
            newOne.setDoctorId(doctor.getId());
            newOne.setDoctorJson(jsonUtil.toJsonString(doctor));
            newOne.setClinicDateId(clinicDate.getId());
            newOne.setClinicDate(clinicDate.getClinicDate());
            newOne.setClinicHoursId(clinicHour.getId());
            newOne.setClinicHoursStart(clinicHour.getClinicHourStart());
            newOne.setClinicHoursEnd(clinicHour.getClinicHourEnd());
            newOne.setUserId(userPatientRelation.getUserId());
            newOne.setPatientId(patient.getId());
            newOne.setPatientJson(jsonUtil.toJsonString(patient));
            newOne.setOrderStatus(orderStatuses.get(patientIndex%orderStatuses.size()));
            appointments.add(newOne);

            appointmentId++;
            patientIndex++;
            log.append("\r\n").append("\r\n").append("\r\n");
        }
        writeTempFile(log.toString(), "fdsafdsafdsaf+++");
        String insertSql = insertSQL(appointments);
        boolean writeSuccess = writeTempFile(insertSql);
        logger.info("save doctor_appointment_duplicate sql file success={}", writeSuccess);
    }

    private DoctorClinicDateEntity findClinicDate(List<DoctorClinicDateEntity> dates, long dateId) {
        if(null==dates || dates.isEmpty()) {
            return null;
        }
        for (DoctorClinicDateEntity date : dates) {
            if (date.getId() == dateId) {
                return date;
            }
        }
        return null;
    }

    private DoctorEntity findDoctor(List<DoctorEntity> doctors, long doctorId) {
        if(null==doctors || doctors.isEmpty()) {
            return null;
        }
        for (DoctorEntity tmp : doctors) {
            if (tmp.getId() == doctorId) {
                return tmp;
            }
        }
        return null;
    }

    private HospitalDepartmentEntity findDepartment(List<HospitalDepartmentEntity> departments, int departmentId) {
        if(null==departments || departments.isEmpty()) {
            return null;
        }
        for (HospitalDepartmentEntity tmp : departments) {
            if (tmp.getId() == departmentId) {
                return tmp;
            }
        }
        return null;
    }

    private PatientEntity findPatient(List<PatientEntity> patients, long patientId) {
        if(null==patients || patients.isEmpty()) {
            return null;
        }
        for (PatientEntity tmp : patients) {
            if (tmp.getId() == patientId) {
                return tmp;
            }
        }
        return null;
    }

    private String insertSQL(List<DoctorAppointmentEntity> appointments) {
        StringBuilder insertSql = new StringBuilder();
        insertSql.append("INSERT INTO `go2nurse_doctor_appointment`(`id`,`time_created`,`status`,`order_no`,`hospital_id`,`hospital`,`department_id`,`department`,`doctor_id`,`doctor`,`clinic_date_id`,`clinic_date`,`clinic_hours_id`,`clinic_hours_start`,`clinic_hours_end`,`user_id`,`patient_id`,`patient`,`order_status`) VALUES").append("\r\n");
        for (DoctorAppointmentEntity tmp : appointments) {
            insertSql.append("    (");
            insertSql.append("'").append(tmp.getId()).append("',");
            insertSql.append("'").append(tmp.getTime()).append("',");
            insertSql.append("'").append(tmp.getStatus()).append("',");
            insertSql.append("'").append(tmp.getOrderNo()).append("',");
            insertSql.append("'").append(tmp.getHospitalId()).append("',");
            insertSql.append("'").append(tmp.getHospitalJson()).append("',");
            insertSql.append("'").append(tmp.getDepartmentId()).append("',");
            insertSql.append("'").append(tmp.getDepartmentJson()).append("',");
            insertSql.append("'").append(tmp.getDoctorId()).append("',");
            insertSql.append("'").append(tmp.getDoctorJson()).append("',");
            insertSql.append("'").append(tmp.getClinicDateId()).append("',");
            insertSql.append("'").append(tmp.getClinicDate()).append("',");
            insertSql.append("'").append(tmp.getClinicHoursId()).append("',");
            insertSql.append("'").append(tmp.getClinicHoursStart()).append("',");
            insertSql.append("'").append(tmp.getClinicHoursEnd()).append("',");
            insertSql.append("'").append(tmp.getUserId()).append("',");
            insertSql.append("'").append(tmp.getPatientId()).append("',");
            insertSql.append("'").append(tmp.getPatientJson()).append("',");
            insertSql.append("'").append(tmp.getOrderStatus()).append("'),\r\n");
        }
        int index = insertSql.lastIndexOf(",");
        insertSql.deleteCharAt(index);
        insertSql.append(";");
        return insertSql.toString();
    }

    private boolean writeTempFile(String content) {
        try {
            File file = File.createTempFile("doctor_appointment_duplicate_", ".sql");
            File parent = file.getParentFile();
            File dest = new File(parent, "doctor_appointment_duplicate.sql");
            FileOutputStream out = new FileOutputStream(file);
            out.write(content.getBytes("UTF-8"));
            System.out.println(file.renameTo(dest));
            logger.info("doctor_appointment_duplicate file is {}", file.getAbsolutePath());
            out.flush();
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private boolean writeTempFile(String content, String fileName) {
        try {
            File file = File.createTempFile(fileName, ".log");
            FileOutputStream out = new FileOutputStream(file);
            out.write(content.getBytes("UTF-8"));
            out.flush();
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
