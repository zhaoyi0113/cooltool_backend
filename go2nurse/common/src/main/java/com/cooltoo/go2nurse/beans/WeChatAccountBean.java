package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by zhls on 10/8/16.
 */
public class WeChatAccountBean {

    private int id;
    private String appId;
    private String appSecret;
    private Date timeCreated;
    private String mchId;
    private CommonStatus status;
    private String name;
    private int hospitalId;
    private int departmentId;
    private HospitalBean hospital;
    private HospitalDepartmentBean department;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", timeCreated=").append(timeCreated);
        msg.append(", status=").append(status);
        msg.append(", appId=").append(appId);
        msg.append(", appSecret=").append(appSecret);
        msg.append(", mchId=").append(mchId);
        msg.append(", name=").append(name);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", departmentId=").append(departmentId);
        msg.append("]");
        return msg.toString();
    }
}
