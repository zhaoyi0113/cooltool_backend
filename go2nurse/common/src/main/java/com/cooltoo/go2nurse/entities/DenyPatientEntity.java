package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.WhoDenyPatient;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 14/12/2016.
 */
@Entity
@Table(name = "go2nurse_manager_nurse_deny_user_patient")
public class DenyPatientEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private ServiceVendorType vendorType;
    private long vendorId;
    private long departId;
    private long nurseId;
    private WhoDenyPatient whoDenyPatient;
    private long userId;
    private long patientId;

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
    public CommonStatus getStatus() {
        return status;
    }
    public void setStatus(CommonStatus status) {
        this.status = status;
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
    public long getDepartId() {
        return departId;
    }
    public void setDepartId(long departId) {
        this.departId = departId;
    }

    @Column(name = "nurse_id")
    public long getNurseId() {
        return nurseId;
    }
    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    @Column(name = "who_deny_user")
    public WhoDenyPatient getWhoDenyPatient() {
        return whoDenyPatient;
    }
    public void setWhoDenyPatient(WhoDenyPatient whoDenyPatient) {
        this.whoDenyPatient = whoDenyPatient;
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

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", vendorType=").append(vendorType);
        msg.append(", vendorId=").append(vendorId);
        msg.append(", departId=").append(departId);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", whoDenyPatient=").append(whoDenyPatient);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
