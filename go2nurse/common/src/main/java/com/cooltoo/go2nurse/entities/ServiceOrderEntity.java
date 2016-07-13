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
    private long serviceItemId;
    private long userId;
    private long patientId;
    private long addressId;
    private Date serviceStartTime;
    private int serviceTimeDuration;
    private TimeUnit serviceTimeUnit;
    private BigDecimal totalConsumption;
    private OrderStatus orderStatus;
    private Date payTime;
    private BigDecimal paymentAmount;

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

    @Column(name = "service_item_id")
    public long getServiceItemId() {
        return serviceItemId;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    @Column(name = "patient_id")
    public long getPatientId() {
        return patientId;
    }

    @Column(name = "address_id")
    public long getAddressId() {
        return addressId;
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

    @Column(name = "total_consumption")
    public BigDecimal getTotalConsumption() {
        return totalConsumption;
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

    @Column(name = "payment_amount")
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
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

    public void setServiceItemId(long serviceItemId) {
        this.serviceItemId = serviceItemId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
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

    public void setTotalConsumption(BigDecimal totalConsumption) {
        this.totalConsumption = totalConsumption;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", serviceItemId=").append(serviceItemId);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", addressId=").append(addressId);
        msg.append(", serviceStartTime=").append(serviceStartTime);
        msg.append(", serviceTimeDuration=").append(serviceTimeDuration);
        msg.append(", serviceTimeUnit=").append(serviceTimeUnit);
        msg.append(", totalConsumption=").append(totalConsumption);
        msg.append(", orderStatus=").append(orderStatus);
        msg.append(", payTime=").append(payTime);
        msg.append(", paymentAmount=").append(paymentAmount);
        msg.append("]");
        return msg.toString();
    }
}
