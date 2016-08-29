package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Date;

/**
 * Created by hp on 2016/8/4.
 */
@Entity
@Table(name = "go2nurse_doctor_clinic_hours")
public class DoctorClinicHoursEntity {

    private long id;
    private java.util.Date time;
    private CommonStatus status;
    private long doctorId;
    private long clinicDateId;
    private Time clinicHourStart;
    private Time clinicHourEnd;
    private int numberCount;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "time_created")
    public java.util.Date getTime() {
        return time;
    }

    @Column(name = "status")
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "doctor_id")
    public long getDoctorId() {
        return doctorId;
    }

    @Column(name = "doctor_clinic_date_id")
    public long getClinicDateId() {
        return clinicDateId;
    }

    @Column(name = "clinic_hour_start")
    public Time getClinicHourStart() {
        return clinicHourStart;
    }

    @Column(name = "clinic_hour_end")
    public Time getClinicHourEnd() {
        return clinicHourEnd;
    }

    @Column(name = "number_count")
    public int getNumberCount() {
        return numberCount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(java.util.Date time) {
        this.time = time;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public void setClinicDateId(long clinicDateId) {
        this.clinicDateId = clinicDateId;
    }

    public void setClinicHourStart(Time clinicHourStart) {
        this.clinicHourStart = clinicHourStart;
    }

    public void setClinicHourEnd(Time clinicHourEnd) {
        this.clinicHourEnd = clinicHourEnd;
    }

    public void setNumberCount(int numberCount) {
        this.numberCount = numberCount;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", doctorId=").append(doctorId);
        msg.append(", clinicDateId=").append(clinicDateId);
        msg.append(", clinicHourStart=").append(clinicHourStart);
        msg.append(", clinicHourEnd=").append(clinicHourEnd);
        msg.append(", numberCount=").append(numberCount);
        msg.append("]");
        return msg.toString();
    }
}
