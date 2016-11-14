package com.cooltoo.nurse360.beans;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.CommonStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/9.
 */
public class HospitalAdminBean {

    public static final String ROLE = "role";

    private long id;
    private Date time;
    private CommonStatus status;
    private AdminUserType adminType;
    private String name;
    private String password;
    private String telephone ="";
    private String email="";
    private int hospitalId;
    private HospitalBean hospital;
    private int departmentId;
    private HospitalDepartmentBean department;
    private Map<String, Object> properties = new HashMap<>();

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

    public AdminUserType getAdminType() {
        return adminType;
    }

    public void setAdminType(AdminUserType adminType) {
        this.adminType = adminType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void setProperties(String key, Object value) {
        if (null==properties) {
            properties = new HashMap<>();
        }
        properties.put(key, value);
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", adminType=").append(adminType);
        msg.append(", name=").append(name);
        msg.append(", password=").append(password);
        msg.append(", telephone=").append(telephone);
        msg.append(", email=").append(email);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", hospital=").append(hospital);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", department=").append(department);
        msg.append("]");
        return msg.toString();
    }

}
