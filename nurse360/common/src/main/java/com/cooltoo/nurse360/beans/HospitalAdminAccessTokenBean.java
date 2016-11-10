package com.cooltoo.nurse360.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by zhaolisong on 2016/11/9.
 */
public class HospitalAdminAccessTokenBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long adminId;
    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", adminId=").append(adminId);
        msg.append(", token=").append(token);
        msg.append("]");
        return msg.toString();
    }
}
