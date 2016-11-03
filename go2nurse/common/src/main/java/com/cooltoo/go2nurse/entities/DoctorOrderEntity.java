package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/9/6.
 */
@Entity
@Table(name = "go2nurse_doctor_order")
public class DoctorOrderEntity {
    private long id;
    private Date time;
    private CommonStatus status;
    private int hospitalId;
    private int hospitalOrder;
    private int departmentId;
    private int departmentOrder;
    private long doctorId;
    private int doctorOrder;


    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "hospital_id")
    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    @Column(name = "hospital_order")
    public int getHospitalOrder() {
        return hospitalOrder;
    }

    public void setHospitalOrder(int hospitalOrder) {
        this.hospitalOrder = hospitalOrder;
    }

    @Column(name = "department_id")
    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    @Column(name = "department_order")
    public int getDepartmentOrder() {
        return departmentOrder;
    }

    public void setDepartmentOrder(int departmentOrder) {
        this.departmentOrder = departmentOrder;
    }

    @Column(name = "doctor_id")
    public long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    @Column(name = "dortor_order")
    public int getDoctorOrder() {
        return doctorOrder;
    }

    public void setDoctorOrder(int doctorOrder) {
        this.doctorOrder = doctorOrder;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", doctorId=").append(doctorId);
        msg.append(", doctorOrder").append(doctorOrder);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", hospitalOrder").append(hospitalOrder);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", departmentOrder=").append(departmentOrder);
        msg.append("]");
        return msg.toString();
    }
}
