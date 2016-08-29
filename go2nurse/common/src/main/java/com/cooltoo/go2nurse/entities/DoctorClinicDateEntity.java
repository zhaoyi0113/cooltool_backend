package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by hp on 2016/8/4.
 */
@Entity
@Table(name = "go2nurse_doctor_clinic_date")
public class DoctorClinicDateEntity {

    private long id;
    private java.util.Date time;
    private CommonStatus status;
    private long doctorId;
    private Date clinicDate;

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

    @Column(name = "clinic_date")
    public Date getClinicDate() {
        return clinicDate;
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

    public void setClinicDate(Date clinicDate) {
        this.clinicDate = clinicDate;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", doctorId=").append(doctorId);
        msg.append(", clinicDate=").append(clinicDate);
        msg.append("]");
        return msg.toString();
    }
}
