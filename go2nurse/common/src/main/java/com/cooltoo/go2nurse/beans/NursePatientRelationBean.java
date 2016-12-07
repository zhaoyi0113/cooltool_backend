package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by zhaolisong on 16/10/8.
 */
public class NursePatientRelationBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long nurseId;
    private long userId;
    private UserBean user;
    private long patientId;
    private PatientBean patient;
    private long followUpId;

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

    public long getNurseId() {
        return nurseId;
    }

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
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

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public PatientBean getPatient() {
        return patient;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public long getFollowUpId() {
        return followUpId;
    }

    public void setFollowUpId(long followUpId) {
        this.followUpId = followUpId;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", followUpId=").append(followUpId);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append("]");
        return msg.toString();
    }
}
