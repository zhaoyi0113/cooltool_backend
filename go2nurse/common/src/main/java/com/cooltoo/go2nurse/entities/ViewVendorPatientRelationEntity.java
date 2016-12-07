package com.cooltoo.go2nurse.entities;

import com.cooltoo.go2nurse.constants.ServiceVendorType;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/11/24.
 */
@Entity
@Table(name = "go2nurse_view_vendor_patient_relation")
public class ViewVendorPatientRelationEntity {

    private String id;
    private Date time;
    private long userId;
    private String userName;
    private long patientId;
    private String patientName;
    private ServiceVendorType vendorType;
    private long vendorId;
    private long vendorDepartId;
    private String recordFrom;
    private long recordId;


    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Column(name = "patient_id")
    public long getPatientId() {
        return patientId;
    }
    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    @Column(name = "vendor_type")
    public ServiceVendorType getVendorType() {
        return vendorType;
    }
    public void setVendorType(ServiceVendorType vendorType) {
        this.vendorType = vendorType;
    }

    @Column(name = "vendor_id")
    public long getVendorId() {
        return vendorId;
    }
    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    @Column(name = "vendor_depart_id")
    public long getVendorDepartId() {
        return vendorDepartId;
    }
    public void setVendorDepartId(long vendorDepartId) {
        this.vendorDepartId = vendorDepartId;
    }

    @Column(name = "record_from")
    public String getRecordFrom() {
        return recordFrom;
    }
    public void setRecordFrom(String recordFrom) {
        this.recordFrom = recordFrom;
    }

    @Column(name = "record_id")
    public long getRecordId() {
        return recordId;
    }
    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    @Column(name = "user_name")
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "patient_name")
    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append(", time=").append(time);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", vendorType=").append(vendorType);
        msg.append(", vendorId=").append(vendorId);
        msg.append(", vendorDepartId=").append(vendorDepartId);
        msg.append(", recordFrom=").append(recordFrom);
        msg.append(", recordId=").append(recordId);
        msg.append("]");
        return msg.toString();
    }
}
