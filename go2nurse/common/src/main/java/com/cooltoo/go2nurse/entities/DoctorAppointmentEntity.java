package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.OrderStatus;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

/**
 * Created by hp on 2016/8/8.
 */
@Entity
@Table(name="go2nurse_doctor_appointment")
public class DoctorAppointmentEntity {

    private long id;
    private java.util.Date time;
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
    private long userId;
    private long patientId;
    private String patientJson;
    private OrderStatus orderStatus;

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
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "order_no")
    public String getOrderNo() {
        return orderNo;
    }

    @Column(name = "hospital_id")
    public int getHospitalId() {
        return hospitalId;
    }

    @Column(name = "hospital")
    public String getHospitalJson() {
        return hospitalJson;
    }

    @Column(name = "department_id")
    public int getDepartmentId() {
        return departmentId;
    }

    @Column(name = "department")
    public String getDepartmentJson() {
        return departmentJson;
    }

    @Column(name = "doctor_id")
    public long getDoctorId() {
        return doctorId;
    }

    @Column(name = "doctor")
    public String getDoctorJson() {
        return doctorJson;
    }

    @Column(name = "clinic_date_id")
    public long getClinicDateId() {
        return clinicDateId;
    }

    @Column(name = "clinic_date")
    public Date getClinicDate() {
        return clinicDate;
    }

    @Column(name = "clinic_hours_id")
    public long getClinicHoursId() {
        return clinicHoursId;
    }

    @Column(name = "clinic_hours_start")
    public Time getClinicHoursStart() {
        return clinicHoursStart;
    }

    @Column(name = "clinic_hours_end")
    public Time getClinicHoursEnd() {
        return clinicHoursEnd;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    @Column(name = "patient_id")
    public long getPatientId() {
        return patientId;
    }

    @Column(name = "patient")
    public String getPatientJson() {
        return patientJson;
    }

    @Column(name = "order_status")
    public OrderStatus getOrderStatus() {
        return orderStatus;
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

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setHospitalJson(String hospitalJson) {
        this.hospitalJson = hospitalJson;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public void setDepartmentJson(String departmentJson) {
        this.departmentJson = departmentJson;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctorJson(String doctorJson) {
        this.doctorJson = doctorJson;
    }

    public void setClinicDateId(long clinicDateId) {
        this.clinicDateId = clinicDateId;
    }

    public void setClinicDate(Date clinicDate) {
        this.clinicDate = clinicDate;
    }

    public void setClinicHoursId(long clinicHoursId) {
        this.clinicHoursId = clinicHoursId;
    }

    public void setClinicHoursStart(Time clinicHoursStart) {
        this.clinicHoursStart = clinicHoursStart;
    }

    public void setClinicHoursEnd(Time clinicHoursEnd) {
        this.clinicHoursEnd = clinicHoursEnd;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setPatientJson(String patientJson) {
        this.patientJson = patientJson;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", orderNo=").append(orderNo);
        msg.append(", orderStatus=").append(orderStatus);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", doctorId=").append(doctorId);
        msg.append(", clinicDate=").append(clinicDate);
        msg.append(", clinicHoursStart=").append(clinicHoursStart);
        msg.append(", clinicHoursEnd=").append(clinicHoursEnd);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
