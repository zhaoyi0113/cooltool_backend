package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.OrderStatus;

import javax.persistence.criteria.Order;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2016/8/8.
 */
public class DoctorAppointmentBean {

    public static final String FLAG = "FLAG";

    private long id;
    private java.util.Date time;
    private CommonStatus status;
    private String orderNo;
    private int hospitalId;
    private HospitalBean hospital;
    private int departmentId;
    private HospitalDepartmentBean department;
    private long doctorId;
    private DoctorBean doctor;
    private long clinicDateId;
    private Date clinicDate;
    private long clinicHoursId;
    private Time clinicHoursStart;
    private Time clinicHoursEnd;
    private long userId;
    private long patientId;
    private PatientBean patient;
    private String orderStatus;
    private float score;
    private Map<String, Object> properties = new HashMap<>();

    public long getId() {
        return id;
    }

    public java.util.Date getTime() {
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

    public HospitalBean getHospital() {
        return hospital;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public HospitalDepartmentBean getDepartment() {
        return department;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public DoctorBean getDoctor() {
        return doctor;
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

    public PatientBean getPatient() {
        return patient;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public float getScore() {
        return score;
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

    public void setHospital(HospitalBean hospital) {
        this.hospital = hospital;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public void setDepartment(HospitalDepartmentBean department) {
        this.department = department;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctor(DoctorBean doctor) {
        this.doctor = doctor;
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

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Object getProperty(String key){
        return this.properties.get(key);
    }

    public void setProperty(String key, Object value){
        this.properties.put(key, value);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", orderNo=").append(orderNo);
        msg.append(", orderStatus=").append(orderStatus);
        msg.append(", score=").append(score);
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
