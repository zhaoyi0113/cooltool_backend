package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.AppType;
import com.cooltoo.go2nurse.constants.PingPPType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/7/15.
 */
public class ServiceOrderPingPPBean {
    private long id;
    private Date time;
    private CommonStatus status;
    private AppType appType;
    private String pingPPId;
    private String pingPPJson;
    private PingPPType pingPPType;
    private long orderId;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public AppType getAppType() {
        return appType;
    }

    public String getPingPPId() {
        return pingPPId;
    }

    public String getPingPPJson() {
        return pingPPJson;
    }

    public PingPPType getPingPPType() {
        return pingPPType;
    }

    public long getOrderId() {
        return orderId;
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

    public void setAppType(AppType appType) {
        this.appType = appType;
    }

    public void setPingPPId(String pingPPId) {
        this.pingPPId = pingPPId;
    }

    public void setPingPPJson(String pingPPJson) {
        this.pingPPJson = pingPPJson;
    }

    public void setPingPPType(PingPPType pingPPType) {
        this.pingPPType = pingPPType;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", appType=").append(appType);
        msg.append(", pingPPId=").append(pingPPId);
        msg.append(", pingPPJson=").append(pingPPJson);
        msg.append(", pingPPType=").append(pingPPType);
        msg.append(", orderId=").append(orderId);
        msg.append("]");
        return msg.toString();
    }
}
