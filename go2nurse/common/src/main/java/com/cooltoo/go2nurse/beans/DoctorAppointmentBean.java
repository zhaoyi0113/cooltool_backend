package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.OrderStatus;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by hp on 2016/8/8.
 */
public class DoctorAppointmentBean {

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
    private long userId;
    private long patientId;
    private String patientJson;
    private OrderStatus orderStatus;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

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

    public long getUserId() {
        return userId;
    }

    public long getPatientId() {
        return patientId;
    }

    public String getPatientJson() {
        return patientJson;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(Date time) {
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
