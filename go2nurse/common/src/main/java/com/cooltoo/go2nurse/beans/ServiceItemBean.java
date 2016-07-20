package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceClass;
import com.cooltoo.go2nurse.constants.TimeUnit;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hp on 2016/7/13.
 */
public class ServiceItemBean {
    private long id;
    private Date time;
    private CommonStatus status;
    private long vendorId;
    private ServiceVendorBean vendor;
    private long categoryId;
    private String name;
    private ServiceClass clazz;
    private String description;
    private long imageId;
    private String imageUrl;
    private BigDecimal servicePrice;
    private int serviceTimeDuration;
    private TimeUnit serviceTimeUnit;
    private int grade;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getVendorId() {
        return vendorId;
    }

    public ServiceVendorBean getVendor() {
        return vendor;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public ServiceClass getClazz() {
        return clazz;
    }

    public String getDescription() {
        return description;
    }

    public long getImageId() {
        return imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BigDecimal getServicePrice() {
        return servicePrice;
    }

    public int getServiceTimeDuration() {
        return serviceTimeDuration;
    }

    public TimeUnit getServiceTimeUnit() {
        return serviceTimeUnit;
    }

    public int getGrade() {
        return grade;
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

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public void setVendor(ServiceVendorBean vendor) {
        this.vendor = vendor;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClazz(ServiceClass clazz) {
        this.clazz = clazz;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setServicePrice(BigDecimal servicePrice) {
        this.servicePrice = servicePrice;
    }

    public void setServiceTimeDuration(int serviceTimeDuration) {
        this.serviceTimeDuration = serviceTimeDuration;
    }

    public void setServiceTimeUnit(TimeUnit serviceTimeUnit) {
        this.serviceTimeUnit = serviceTimeUnit;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", vendorId=").append(vendorId);
        msg.append(", vendor=").append(vendor);
        msg.append(", categoryId=").append(categoryId);
        msg.append(", name=").append(name);
        msg.append(", clazz=").append(clazz);
        msg.append(", description=").append(description);
        msg.append(", imageId=").append(imageId);
        msg.append(", servicePrice=").append(servicePrice);
        msg.append(", serviceTimeDuration=").append(serviceTimeDuration);
        msg.append(", serviceTimeUnit=").append(serviceTimeUnit);
        msg.append(", grade=").append(grade);
        msg.append("]");
        return msg.toString();
    }
}
