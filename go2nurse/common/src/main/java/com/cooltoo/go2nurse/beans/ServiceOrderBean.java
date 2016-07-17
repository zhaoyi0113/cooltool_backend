package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.TimeUnit;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hp on 2016/7/13.
 */
public class ServiceOrderBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private ServiceItemBean serviceItem;
    private long userId;
    private PatientBean patient;
    private UserAddressBean address;
    private Date serviceStartTime;
    private int serviceTimeDuration;
    private TimeUnit serviceTimeUnit;
    private BigDecimal totalConsumption;
    private OrderStatus orderStatus;
    private Date payTime;
    private BigDecimal paymentAmount;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public ServiceItemBean getServiceItem() {
        return serviceItem;
    }

    public long getUserId() {
        return userId;
    }

    public PatientBean getPatient() {
        return patient;
    }

    public UserAddressBean getAddress() {
        return address;
    }

    public Date getServiceStartTime() {
        return serviceStartTime;
    }

    public int getServiceTimeDuration() {
        return serviceTimeDuration;
    }

    public TimeUnit getServiceTimeUnit() {
        return serviceTimeUnit;
    }

    public BigDecimal getTotalConsumption() {
        return totalConsumption;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Date getPayTime() {
        return payTime;
    }

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

    public void setServiceItem(ServiceItemBean serviceItem) {
        this.serviceItem = serviceItem;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public void setAddress(UserAddressBean address) {
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
        msg.append(", serviceItem=").append(serviceItem);
        msg.append(", userId=").append(userId);
        msg.append(", patient=").append(patient);
        msg.append(", address=").append(address);
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
