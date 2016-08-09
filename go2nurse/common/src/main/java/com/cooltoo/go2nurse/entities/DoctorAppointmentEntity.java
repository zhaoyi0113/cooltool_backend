package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by hp on 2016/8/8.
 */
//@Entity
//@Table(name="go2nurse_doctor_appointment")
public class DoctorAppointmentEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private String orderNo;
    private int hospitalId;
    private String hospitalJson;
    private int departmentId;
    private String departmentJson;
    private long doctorId;
    private String doctorJson;
    private long clinicDateId;
    private Date clinicDate;
    private long clinicHoursId;
    private Time clinicHoursStart;
    private Time clinicHoursEnd;
    private long patientId;
    private String patientJson;

//    @Id
//    @GeneratedValue
//    @Column(name = "id")
    public long getId() {
        return id;
    }

//    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

//    @Column(name = "status")
//    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public String getHospitalJson() {
        return hospitalJson;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentJson() {
        return departmentJson;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public String getDoctorJson() {
        return doctorJson;
    }

    public long getClinicDateId() {
        return clinicDateId;
    }

    public Date getClinicDate() {
        return clinicDate;
    }

    public long getClinicHoursId() {
        return clinicHoursId;
    }

    public Time getClinicHoursStart() {
        return clinicHoursStart;
    }

    public Time getClinicHoursEnd() {
        return clinicHoursEnd;
    }

    public long getPatientId() {
        return patientId;
    }

    public String getPatientJson() {
        return patientJson;
    }
}
