package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by hp on 2016/7/19.
 */
public class ServiceVendorBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private String description;
    private long logoId;
    private String logoUrl;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getLogoId() {
        return logoId;
    }

    public String getLogoUrl() {
        return logoUrl;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLogoId(long logoId) {
        this.logoId = logoId;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", description=").append(description);
        msg.append(", logoId=").append(logoId);
        msg.append(", logoUrl=").append(logoUrl);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
