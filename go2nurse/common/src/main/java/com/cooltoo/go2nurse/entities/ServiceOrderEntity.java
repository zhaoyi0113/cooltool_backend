package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
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
    private String serviceItem;
    private ServiceVendorType vendorType;
    private long vendorId;
    private String vendor;
    private long categoryId;
    private String category;
    private long topCategoryId;
    private String topCategory;
    private long userId;
    private long patientId;
    private String patient;
    private long addressId;
    private String address;
    private Date serviceStartTime;
    private int serviceTimeDuration;
    private TimeUnit serviceTimeUnit;
    private int totalConsumptionCent;
    private int preferentialCent;
    private String orderNo;
    private OrderStatus orderStatus;
    private Date payTime;
    private int paymentAmountCent;
    private String leaveAMessage;
    private float score;
    private Date completedTime;

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

    @Column(name = "service_item")
    public String getServiceItem() {
        return serviceItem;
    }

    @Column(name = "item_vendor_type")
    public ServiceVendorType getVendorType() {
        return vendorType;
    }

    @Column(name = "item_vendor_id")
    public long getVendorId() {
        return vendorId;
    }

    @Column(name = "item_vendor")
    public String getVendor() {
        return vendor;
    }

    @Column(name = "item_category_id")
    public long getCategoryId() {
        return categoryId;
    }

    @Column(name = "item_category")
    public String getCategory() {
        return category;
    }

    @Column(name = "item_top_category_id")
    public long getTopCategoryId() {
        return topCategoryId;
    }

    @Column(name = "item_top_category")
    public String getTopCategory() {
        return topCategory;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    @Column(name = "patient_id")
    public long getPatientId() {
        return patientId;
    }

    @Column(name = "patient")
    public String getPatient() {
        return patient;
    }

    @Column(name = "address_id")
    public long getAddressId() {
        return addressId;
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

    @Column(name = "preferential_cent")
    public int getPreferentialCent() {
        return preferentialCent;
    }

    @Column(name = "order_no")
    public String getOrderNo() {
        return orderNo;
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

    @Column(name = "leave_a_message")
    public String getLeaveAMessage() {
        return leaveAMessage;
    }

    @Column(name = "score")
    public float getScore() {
        return score;
    }

    @Column(name = "completed_time")
    public Date getCompletedTime() {
        return completedTime;
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

    public void setServiceItem(String serviceItem) {
        this.serviceItem = serviceItem;
    }

    public void setVendorType(ServiceVendorType vendorType) {
        this.vendorType = vendorType;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTopCategoryId(long topCategoryId) {
        this.topCategoryId = topCategoryId;
    }

    public void setTopCategory(String topCategory) {
        this.topCategory = topCategory;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
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

    public void setPreferentialCent(int preferentialCent) {
        this.preferentialCent = preferentialCent;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public void setLeaveAMessage(String leaveAMessage) {
        this.leaveAMessage = leaveAMessage;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
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
        msg.append(", preferentialCent=").append(preferentialCent);
        msg.append(", orderNo=").append(orderNo);
        msg.append(", orderStatus=").append(orderStatus);
        msg.append(", payTime=").append(payTime);
        msg.append(", paymentAmountCent=").append(paymentAmountCent);
        msg.append(", leaveAMessage=").append(leaveAMessage);
        msg.append(", score=").append(score);
        msg.append(", completedTime=").append(completedTime);
        msg.append("]");
        return msg.toString();
    }
}
