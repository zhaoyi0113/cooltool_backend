package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.PaymentPlatform;
import com.cooltoo.go2nurse.constants.AppType;
import com.cooltoo.go2nurse.constants.ChargeStatus;
import com.cooltoo.go2nurse.constants.ChargeType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/7/15.
 */
@Entity
@Table(name = "go2nurse_service_order_charge_pingpp")
public class ServiceOrderChargeEntity {
    private long id;
    private Date time;
    private CommonStatus status;
    private long orderId;
    private String orderNo;
    private String channel;
    private AppType appType;
    private ChargeType chargeType;
    private String chargeId;
    private String chargeJson;
    private String webhooksEventId;
    private String webhooksEventJson;
    private ChargeStatus chargeStatus;
    private PaymentPlatform paymentPlatform;

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

    @Column(name = "order_id")
    public long getOrderId() {
        return orderId;
    }

    @Column(name = "order_no")
    public String getOrderNo() {
        return orderNo;
    }

    @Column(name = "channel")
    public String getChannel() {
        return channel;
    }

    @Column(name = "payment_platform")
    public PaymentPlatform getPaymentPlatform() {
        return paymentPlatform;
    }

    @Column(name = "app_type")
    @Enumerated
    public AppType getAppType() {
        return appType;
    }

    @Column(name = "charge_type")
    @Enumerated
    public ChargeType getChargeType() {
        return chargeType;
    }

    @Column(name = "charge_id")
    public String getChargeId() {
        return chargeId;
    }

    @Column(name = "charge_json")
    public String getChargeJson() {
        return chargeJson;
    }

    @Column(name = "webhooks_event_id")
    public String getWebhooksEventId() {
        return webhooksEventId;
    }

    @Column(name = "webhooks_event_json")
    public String getWebhooksEventJson() {
        return webhooksEventJson;
    }

    @Column(name = "charge_status")
    @Enumerated
    public ChargeStatus getChargeStatus() {
        return chargeStatus;
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

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setPaymentPlatform(PaymentPlatform paymentPlatform) {
        this.paymentPlatform = paymentPlatform;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setAppType(AppType appType) {
        this.appType = appType;
    }

    public void setChargeType(ChargeType chargeType) {
        this.chargeType = chargeType;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public void setChargeJson(String chargeJson) {
        this.chargeJson = chargeJson;
    }

    public void setWebhooksEventId(String webhooksEventId) {
        this.webhooksEventId = webhooksEventId;
    }

    public void setWebhooksEventJson(String webhooksEventJson) {
        this.webhooksEventJson = webhooksEventJson;
    }

    public void setChargeStatus(ChargeStatus chargeStatus) {
        this.chargeStatus = chargeStatus;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", orderId=").append(orderId);
        msg.append(", orderNo=").append(orderNo);
        msg.append(", paymentPlatform=").append(paymentPlatform);
        msg.append(", channel=").append(channel);
        msg.append(", appType=").append(appType);
        msg.append(", pingPPType=").append(chargeType);
        msg.append(", chargeId=").append(chargeId);
        msg.append(", chargeJson=").append(chargeJson);
        msg.append(", webhooksEventId=").append(webhooksEventId);
        msg.append(", webhooksEventJson=").append(webhooksEventJson);
        msg.append(", chargeStatus=").append(chargeStatus);
        msg.append("]");
        return msg.toString();
    }
}
