package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/11/06.
 */
@Entity
@Table(name = "go2nurse_nurse_visit_patient")
public class NurseVisitPatientEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long nurseId;
    private long userId;
    private long patientId;
    private long orderId;
    private String serviceItem;
    private String visitRecord;
    private long patientSign;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "service_itme")
    public String getServiceItem() {
        return serviceItem;
    }

    @Column(name = "visit_record")
    public String getVisitRecord() {
        return visitRecord;
    }

    @Column(name = "patient_sign")
    public long getPatientSign() {
        return patientSign;
    }

    @Column(name = "order_id")
    public long getOrderId() {
        return orderId;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    @Column(name = "patient_id")
    public long getPatientId() {
        return patientId;
    }

    @Column(name = "nurse_id")
    public long getNurseId() {
        return nurseId;
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

    public void setServiceItem(String serviceItem) {
        this.serviceItem = serviceItem;
    }

    public void setVisitRecord(String visitRecord) {
        this.visitRecord = visitRecord;
    }

    public void setPatientSign(long patientSign) {
        this.patientSign = patientSign;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", serviceItem=").append(serviceItem);
        msg.append(", visitRecord=").append(visitRecord);
        msg.append(", patientSign=").append(patientSign);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
