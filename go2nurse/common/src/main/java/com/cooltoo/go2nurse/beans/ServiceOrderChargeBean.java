package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.PaymentPlatform;
import com.cooltoo.go2nurse.constants.AppType;
import com.cooltoo.go2nurse.constants.ChargeStatus;
import com.cooltoo.go2nurse.constants.ChargeType;

import java.util.Date;

/**
 * Created by hp on 2016/7/15.
 */
public class ServiceOrderChargeBean {
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

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public PaymentPlatform getPaymentPlatform() {
        return paymentPlatform;
    }

    public String getChannel() {
        return channel;
    }

    public AppType getAppType() {
        return appType;
    }

    public ChargeType getChargeType() {
        return chargeType;
    }

    public String getChargeId() {
        return chargeId;
    }

    public String getChargeJson() {
        return chargeJson;
    }

    public String getWebhooksEventId() {
        return webhooksEventId;
    }

    public String getWebhooksEventJson() {
        return webhooksEventJson;
    }

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
