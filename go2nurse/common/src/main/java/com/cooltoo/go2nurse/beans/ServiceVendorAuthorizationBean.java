package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;

import java.util.Date;

/**
 * Created by zhaolisong on 2016/12/1.
 */
public class ServiceVendorAuthorizationBean {

    private long id;
    private Date time;
    private CommonStatus status;/* ENABLED means been forbidden */
    private boolean forbidden;
    private long userId;
    private ServiceVendorType vendorType;
    private long vendorId;
    private long departId;

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

    public boolean isForbidden() {
        return forbidden;
    }

    public void setForbidden(boolean forbidden) {
        this.forbidden = forbidden;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public ServiceVendorType getVendorType() {
        return vendorType;
    }

    public void setVendorType(ServiceVendorType vendorType) {
        this.vendorType = vendorType;
    }

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public long getDepartId() {
        return departId;
    }

    public void setDepartId(long departId) {
        this.departId = departId;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", userId=").append(userId);
        msg.append(", vendorType=").append(vendorType);
        msg.append(", vendorId=").append(vendorId);
        msg.append(", departId=").append(departId);
        msg.append("]");
        return msg.toString();
    }
}
