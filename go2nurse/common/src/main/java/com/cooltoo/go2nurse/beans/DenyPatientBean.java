package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.WhoDenyPatient;

import java.util.Date;

/**
 * Created by zhaolisong on 14/12/2016.
 */
public class DenyPatientBean {

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

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    public CommonStatus getStatus() {
        return status;
    }
    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public ServiceVendorType getVendorType() {
        return vendorType;
    }
    public void setVendorType(ServiceVendorType vendorType) {
        this.vendorType = vendorType;
    }

    public long getVendorId() {
        return vendorId;
    }
    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public long getDepartId() {
        return departId;
    }
    public void setDepartId(long departId) {
        this.departId = departId;
    }

    public long getNurseId() {
        return nurseId;
    }
    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public WhoDenyPatient getWhoDenyPatient() {
        return whoDenyPatient;
    }
    public void setWhoDenyPatient(WhoDenyPatient whoDenyPatient) {
        this.whoDenyPatient = whoDenyPatient;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

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
