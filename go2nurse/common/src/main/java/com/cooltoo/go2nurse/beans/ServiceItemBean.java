package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceClass;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.TimeUnit;
import com.cooltoo.util.VerifyUtil;

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
    private ServiceVendorType vendorType;
    private ServiceVendorBean vendor;
    private HospitalBean hospital;
    private long categoryId;
    private String name;
    private ServiceClass clazz;
    private String description;
    private long imageId;
    private String imageUrl;
    private long detailImageId;
    private String detailImageUrl;
    private String servicePrice;
    private int servicePriceCent;
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

    public ServiceVendorType getVendorType() {
        return vendorType;
    }

    public ServiceVendorBean getVendor() {
        return vendor;
    }

    public HospitalBean getHospital() {
        return hospital;
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

    public String getServicePrice() {
        return servicePrice;
    }

    public int getServicePriceCent() {
        return servicePriceCent;
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

    public void setVendorType(ServiceVendorType vendorType) {
        this.vendorType = vendorType;
    }

    public void setVendor(ServiceVendorBean vendor) {
        this.vendor = vendor;
    }

    public void setHospital(HospitalBean hospital) {
        this.hospital = hospital;
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

    public void setServicePriceCent(int servicePriceCent) {
        this.servicePriceCent = servicePriceCent;
        this.servicePrice = VerifyUtil.parsePrice(servicePriceCent);
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

    public long getDetailImageId() {
        return detailImageId;
    }

    public void setDetailImageId(long detailImageId) {
        this.detailImageId = detailImageId;
    }

    public String getDetailImageUrl() {
        return detailImageUrl;
    }

    public void setDetailImageUrl(String detailImageUrl) {
        this.detailImageUrl = detailImageUrl;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", vendorId=").append(vendorId);
        msg.append(", vendorType=").append(vendorType);
        msg.append(", vendor=").append(vendor);
        msg.append(", hospital=").append(hospital);
        msg.append(", categoryId=").append(categoryId);
        msg.append(", name=").append(name);
        msg.append(", clazz=").append(clazz);
        msg.append(", description=").append(description);
        msg.append(", imageId=").append(imageId);
        msg.append(", detailImageId=").append(detailImageId);
        msg.append(", servicePrice=").append(servicePrice);
        msg.append(", servicePriceCent=").append(servicePriceCent);
        msg.append(", serviceTimeDuration=").append(serviceTimeDuration);
        msg.append(", serviceTimeUnit=").append(serviceTimeUnit);
        msg.append(", grade=").append(grade);
        msg.append("]");
        return msg.toString();
    }
}
