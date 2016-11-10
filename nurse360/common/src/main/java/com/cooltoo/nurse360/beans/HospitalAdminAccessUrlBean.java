package com.cooltoo.nurse360.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by zhaolisong on 2016/11/10.
 */
public class HospitalAdminAccessUrlBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long adminId;
    private HospitalAdminBean admin;
    private long urlId;
    private HospitalManagementUrlBean url;

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

    public long getUrlId() {
        return urlId;
    }

    public void setUrlId(long urlId) {
        this.urlId = urlId;
    }

    public HospitalAdminBean getAdmin() {
        return admin;
    }

    public void setAdmin(HospitalAdminBean admin) {
        this.admin = admin;
    }

    public HospitalManagementUrlBean getUrl() {
        return url;
    }

    public void setUrl(HospitalManagementUrlBean url) {
        this.url = url;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", adminId=").append(adminId);
        msg.append(", urlId=").append(urlId);
        msg.append(", admin=").append(admin);
        msg.append(", url=").append(url);
        msg.append("]");
        return msg.toString();
    }
}
