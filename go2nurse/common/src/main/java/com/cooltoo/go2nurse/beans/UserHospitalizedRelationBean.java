package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import java.util.Date;

/**
 * Created by hp on 2016/6/14.
 */
public class UserHospitalizedRelationBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private int hospitalId;
    private HospitalBean hospital;
    private int departmentId;
    private HospitalDepartmentBean department;
    private long groupId;
    private YesNoEnum hasLeave;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getUserId() {
        return userId;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public int getDepartmentId() {
        return departmentId;
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

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public HospitalBean getHospital() {
        return hospital;
    }

    public void setHospital(HospitalBean hospital) {
        this.hospital = hospital;
    }

    public HospitalDepartmentBean getDepartment() {
        return department;
    }

    public void setDepartment(HospitalDepartmentBean department) {
        this.department = department;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public YesNoEnum getHasLeave() {
        return hasLeave;
    }

    public void setHasLeave(YesNoEnum hasLeave) {
        this.hasLeave = hasLeave;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", groupId=").append(groupId);
        msg.append(", hasLeave=").append(hasLeave);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append(", hospital=").append(hospital);
        msg.append(", department=").append(department);
        msg.append("]");
        return msg.toString();
    }
}
