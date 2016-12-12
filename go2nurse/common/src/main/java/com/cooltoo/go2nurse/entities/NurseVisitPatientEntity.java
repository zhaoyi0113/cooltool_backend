package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;

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
    private ServiceVendorType vendorType;
    private long vendorId;
    private long vendorDepartId;
    private long nurseSign;
    private String address;
    private String patientRecordNo;
    private String note;

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

    @Column(name = "vendor_type")
    public ServiceVendorType getVendorType() {
        return vendorType;
    }

    @Column(name = "vendor_id")
    public long getVendorId() {
        return vendorId;
    }

    @Column(name = "vendor_depart_id")
    public long getVendorDepartId() {
        return vendorDepartId;
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

    public void setVendorType(ServiceVendorType vendorType) {
        this.vendorType = vendorType;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public void setVendorDepartId(long vendorDepartId) {
        this.vendorDepartId = vendorDepartId;
    }

    @Column(name = "nurse_sign")
    public long getNurseSign() {
        return nurseSign;
    }
    public void setNurseSign(long nurseSign) {
        this.nurseSign = nurseSign;
    }

    @Column(name = "address")
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "patient_record_no")
    public String getPatientRecordNo() {
        return patientRecordNo;
    }
    public void setPatientRecordNo(String patientRecordNo) {
        this.patientRecordNo = patientRecordNo;
    }

    @Column(name = "note")
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
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
        msg.append(", vendorType=").append(vendorType);
        msg.append(", vendorId=").append(vendorId);
        msg.append(", vendorDepartId=").append(vendorDepartId);
        msg.append(", nurseSign=").append(nurseSign);
        msg.append(", address=").append(address);
        msg.append(", patientRecordNo=").append(patientRecordNo);
        msg.append(", note=").append(note);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
