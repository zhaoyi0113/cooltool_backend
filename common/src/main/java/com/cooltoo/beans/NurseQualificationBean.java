package com.cooltoo.beans;

import com.cooltoo.constants.VetStatus;

import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 16/3/23.
 */
public class NurseQualificationBean {

    private long id;
    private String name;
    private long userId;
    private VetStatus qualificationStatus;
    private String statusDescr;
    private Date timeCreated;
    private Date timeProcessed;
    private String userName;
    private String realName;
    private int hospitalId;
    private String hospitalName;
    private int parentDepartmentId;
    private String parentDepartmentName;
    private int departmentId;
    private String departmentName;
    private List<NurseQualificationFileBean> workfiles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VetStatus getStatus() {
        return qualificationStatus;
    }

    public void setStatus(VetStatus status) {
        this.qualificationStatus = status;
    }

    public String getStatusDescr() {
        return this.statusDescr;
    }

    public void setStatusDescr(String statusDescr) {
        this.statusDescr = statusDescr;
    }

    public Date getTimeProcessed() {
        return timeProcessed;
    }

    public void setTimeProcessed(Date timeProcessed) {
        this.timeProcessed = timeProcessed;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public List<NurseQualificationFileBean> getWorkfiles() {
        return this.workfiles;
    }

    public void setWorkfiles(List<NurseQualificationFileBean> workfiles) {
        this.workfiles = workfiles;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getParentDepartmentId() {
        return parentDepartmentId;
    }

    public void setParentDepartmentId(int parentDepartmentId) {
        this.parentDepartmentId = parentDepartmentId;
    }

    public String getParentDepartmentName() {
        return parentDepartmentName;
    }

    public void setParentDepartmentName(String parentDepartmentName) {
        this.parentDepartmentName = parentDepartmentName;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("name=").append(name).append(" , ");
        msg.append("userId=").append(userId).append(" , ");
        msg.append("status=").append(qualificationStatus).append(" , ");
        msg.append("statusDescr=").append(statusDescr).append(" , ");
        msg.append("workfiles=").append(workfiles).append(" , ");
        msg.append("]");
        return msg.toString();
    }
}
