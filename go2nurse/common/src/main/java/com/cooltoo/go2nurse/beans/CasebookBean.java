package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 2016/10/23.
 */
public class CasebookBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private String description;
    private long userId;
    private UserBean user;
    private long patientId;
    private PatientBean patient;
    private long nurseId;
    private NurseBean nurse;
    private List<CaseBean> cases;
    private long caseSize;
    private int hospitalId;
    private int departmentId;
    private Date recentRecordTime;
    private YesNoEnum hidden;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public long getUserId() {
        return userId;
    }

    public long getPatientId() {
        return patientId;
    }

    public UserBean getUser() {
        return user;
    }

    public PatientBean getPatient() {
        return patient;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public List<CaseBean> getCases() {
        return cases;
    }
    public void setCases(List<CaseBean> cases) {
        this.cases = cases;
    }

    public long getNurseId() {
        return nurseId;
    }
    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public NurseBean getNurse() {
        return nurse;
    }
    public void setNurse(NurseBean nurse) {
        this.nurse = nurse;
    }

    public long getCaseSize() {
        return caseSize;
    }
    public void setCaseSize(long caseSize) {
        this.caseSize = caseSize;
    }

    public int getHospitalId() {
        return hospitalId;
    }
    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public Date getRecentRecordTime() {
        return recentRecordTime;
    }
    public void setRecentRecordTime(Date recentRecordTime) {
        this.recentRecordTime = recentRecordTime;
    }

    public YesNoEnum getHidden() {
        return hidden;
    }
    public void setHidden(YesNoEnum hidden) {
        this.hidden = hidden;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", name=").append(name);
        msg.append(", description=").append(description);
        msg.append(", hidden=").append(hidden);
        msg.append(", caseSize=").append(caseSize);
        msg.append(", caseLastRecordTime=").append(recentRecordTime);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
