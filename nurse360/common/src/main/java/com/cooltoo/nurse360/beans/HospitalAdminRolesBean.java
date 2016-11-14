package com.cooltoo.nurse360.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.constants.AdminRole;

import java.util.Date;

/**
 * Created by zhaolisong on 2016/11/10.
 */
public class HospitalAdminRolesBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long adminId;
    private HospitalAdminBean admin;
    private AdminRole role;

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

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public HospitalAdminBean getAdmin() {
        return admin;
    }

    public void setAdmin(HospitalAdminBean admin) {
        this.admin = admin;
    }

    public AdminRole getRole() {
        return role;
    }

    public void setRole(AdminRole role) {
        this.role = role;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", adminId=").append(adminId);
        msg.append(", admin=").append(admin);
        msg.append(", role=").append(role);
        msg.append("]");
        return msg.toString();
    }
}
