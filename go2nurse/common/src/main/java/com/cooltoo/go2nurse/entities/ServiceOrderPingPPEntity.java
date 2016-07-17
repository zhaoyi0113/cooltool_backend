package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.AppType;
import com.cooltoo.go2nurse.constants.PingPPType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/7/15.
 */
@Entity
@Table(name = "go2nurse_service_order_pingpp")
public class ServiceOrderPingPPEntity {
    private long id;
    private Date time;
    private CommonStatus status;
    private AppType appType;
    private String pingPPId;
    private String pingPPJson;
    private PingPPType pingPPType;
    private long orderId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "app_type")
    @Enumerated
    public AppType getAppType() {
        return appType;
    }

    @Column(name = "pingpp_id")
    public String getPingPPId() {
        return pingPPId;
    }

    @Column(name = "pingpp_json")
    public String getPingPPJson() {
        return pingPPJson;
    }

    @Column(name = "pingpp_type")
    @Enumerated
    public PingPPType getPingPPType() {
        return pingPPType;
    }

    @Column(name = "order_id")
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
