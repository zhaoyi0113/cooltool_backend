package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.TimeUnit;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hp on 2016/7/13.
 */
@Entity
@Table(name = "go2nurse_service_order")
public class ServiceOrderEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private String serviceItem;
    private long userId;
    private String patient;
    private String address;
    private Date serviceStartTime;
    private int serviceTimeDuration;
    private TimeUnit serviceTimeUnit;
    private int totalConsumptionCent;
    private OrderStatus orderStatus;
    private Date payTime;
    private int paymentAmountCent;

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

    @Column(name = "service_item")
    public String getServiceItem() {
        return serviceItem;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    @Column(name = "patient")
    public String getPatient() {
        return patient;
    }

    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    @Column(name = "service_start_time")
    public Date getServiceStartTime() {
        return serviceStartTime;
    }

    @Column(name = "service_time_duration")
    public int getServiceTimeDuration() {
        return serviceTimeDuration;
    }

    @Column(name = "service_time_unit")
    @Enumerated
    public TimeUnit getServiceTimeUnit() {
        return serviceTimeUnit;
    }

    @Column(name = "total_consumption_cent")
    public int getTotalConsumptionCent() {
        return totalConsumptionCent;
    }

    @Column(name = "order_status")
    @Enumerated
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    @Column(name = "pay_time")
    public Date getPayTime() {
        return payTime;
    }

    @Column(name = "payment_amount_cent")
    public int getPaymentAmountCent() {
        return paymentAmountCent;
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

    public void setServiceItem(String serviceItem) {
        this.serviceItem = serviceItem;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setServiceStartTime(Date serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public void setServiceTimeDuration(int serviceTimeDuration) {
        this.serviceTimeDuration = serviceTimeDuration;
    }

    public void setServiceTimeUnit(TimeUnit serviceTimeUnit) {
        this.serviceTimeUnit = serviceTimeUnit;
    }

    public void setTotalConsumptionCent(int totalConsumptionCent) {
        this.totalConsumptionCent = totalConsumptionCent;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public void setPaymentAmountCent(int paymentAmountCent) {
        this.paymentAmountCent = paymentAmountCent;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", serviceItem=").append(serviceItem);
        msg.append(", userId=").append(userId);
        msg.append(", patient=").append(patient);
        msg.append(", address=").append(address);
        msg.append(", serviceStartTime=").append(serviceStartTime);
        msg.append(", serviceTimeDuration=").append(serviceTimeDuration);
        msg.append(", serviceTimeUnit=").append(serviceTimeUnit);
        msg.append(", totalConsumptionCent=").append(totalConsumptionCent);
        msg.append(", orderStatus=").append(orderStatus);
        msg.append(", payTime=").append(payTime);
        msg.append(", paymentAmountCent=").append(paymentAmountCent);
        msg.append("]");
        return msg.toString();
    }
}
