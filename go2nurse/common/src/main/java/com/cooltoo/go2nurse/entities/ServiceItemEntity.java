package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceClass;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.TimeUnit;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hp on 2016/7/13.
 */
@Entity
@Table(name = "go2nurse_service_item")
public class ServiceItemEntity {
    private long id;
    private Date time;
    private CommonStatus status;
    private long vendorId;
    private ServiceVendorType vendorType;
    private long categoryId;
    private String name;
    private ServiceClass clazz;
    private String description;
    private long imageId;
    private long detailImageId;
    private int servicePriceCent;
    private int serviceTimeDuration;
    private TimeUnit serviceTimeUnit;
    private int grade;

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

    @Column(name = "vendor_id")
    public long getVendorId() {
        return vendorId;
    }

    @Column(name = "vendor_type")
    @Enumerated
    public ServiceVendorType getVendorType() {
        return vendorType;
    }

    @Column(name = "category_id")
    public long getCategoryId() {
        return categoryId;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "class")
    @Enumerated
    public ServiceClass getClazz() {
        return clazz;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Column(name = "image_id")
    public long getImageId() {
        return imageId;
    }

    @Column(name = "detail_image_id")
    public long getDetailImageId() {
        return detailImageId;
    }

    @Column(name = "service_price_cent")
    public int getServicePriceCent() {
        return servicePriceCent;
    }

    @Column(name = "service_time_duration")
    public int getServiceTimeDuration() {
        return serviceTimeDuration;
    }

    @Column(name = "service_time_unit")
    public TimeUnit getServiceTimeUnit() {
        return serviceTimeUnit;
    }

    @Column(name = "grade")
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

    public void setDetailImageId(long detailImageId) {
        this.detailImageId = detailImageId;
    }

    public void setServicePriceCent(int servicePriceCent) {
        this.servicePriceCent = servicePriceCent;
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
        msg.append(", vendorType=").append(vendorType);
        msg.append(", categoryId=").append(categoryId);
        msg.append(", name=").append(name);
        msg.append(", clazz=").append(clazz);
        msg.append(", description=").append(description);
        msg.append(", imageId=").append(imageId);
        msg.append(", detailImageId=").append(detailImageId);
        msg.append(", servicePriceCent=").append(servicePriceCent);
        msg.append(", serviceTimeDuration=").append(serviceTimeDuration);
        msg.append(", serviceTimeUnit=").append(serviceTimeUnit);
        msg.append(", grade=").append(grade);
        msg.append("]");
        return msg.toString();
    }
}
