package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ManagedBy;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.ServiceClass;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.TimeUnit;
import com.cooltoo.util.VerifyUtil;

import javax.persistence.Column;
import java.util.Date;

/**
 * Created by hp on 2016/7/13.
 */
public class ServiceItemBean {
    private long id;
    private Date time;
    private CommonStatus status;
    private long vendorId;
    private long vendorDepartId;
    private ServiceVendorType vendorType;
    private ServiceVendorBean vendor;
    private HospitalBean hospital;
    private HospitalDepartmentBean hospitalDepartment;
    private long topCategoryId;
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
    private int serviceDiscountCent;
    private String serviceDiscount;
    private int serverIncomeCent;
    private String serverIncome;
    private YesNoEnum needVisitPatientRecord;
    private YesNoEnum managerApproved;
    private ManagedBy managedBy;
    private YesNoEnum needSymptoms;
    private String symptomsItems;
    private long questionnaireId;
    private YesNoEnum needSymptomsDetail;

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

    public long getVendorDepartId() {
        return vendorDepartId;
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

    public HospitalDepartmentBean getHospitalDepartment() {
        return hospitalDepartment;
    }

    public long getTopCategoryId() {
        return topCategoryId;
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

    public void setVendorDepartId(long vendorDepartId) {
        this.vendorDepartId = vendorDepartId;
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

    public void setHospitalDepartment(HospitalDepartmentBean department) {
        this.hospitalDepartment = department;
    }

    public void setTopCategoryId(long topCategoryId) {
        this.topCategoryId = topCategoryId;
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

    public int getServiceDiscountCent() {
        return serviceDiscountCent;
    }
    public String getServiceDiscount() {
        return serviceDiscount;
    }
    public void setServiceDiscountCent(int serviceDiscountCent) {
        this.serviceDiscountCent = serviceDiscountCent;
        this.serviceDiscount = VerifyUtil.parsePrice(serviceDiscountCent);
    }

    public int getServerIncomeCent() {
        return serverIncomeCent;
    }
    public String getServerIncome() {
        return serverIncome;
    }
    public void setServerIncomeCent(int serverIncomeCent) {
        this.serverIncomeCent = serverIncomeCent;
        this.serverIncome = VerifyUtil.parsePrice(serverIncomeCent);
    }

    public YesNoEnum getNeedVisitPatientRecord() {
        return needVisitPatientRecord;
    }
    public void setNeedVisitPatientRecord(YesNoEnum needVisitPatientRecord) {
        this.needVisitPatientRecord = needVisitPatientRecord;
    }

    public YesNoEnum getManagerApproved() {
        return managerApproved;
    }
    public void setManagerApproved(YesNoEnum managerApproved) {
        this.managerApproved = managerApproved;
    }

    public ManagedBy getManagedBy() {
        return managedBy;
    }
    public void setManagedBy(ManagedBy managedBy) {
        this.managedBy = managedBy;
    }

    public YesNoEnum getNeedSymptoms() {
        return needSymptoms;
    }
    public void setNeedSymptoms(YesNoEnum needSymptoms) {
        this.needSymptoms = needSymptoms;
    }

    public String getSymptomsItems() {
        return symptomsItems;
    }
    public void setSymptomsItems(String symptomsItems) {
        this.symptomsItems = symptomsItems;
    }

    public long getQuestionnaireId() {
        return questionnaireId;
    }
    public void setQuestionnaireId(long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public YesNoEnum getNeedSymptomsDetail() {
        return needSymptomsDetail;
    }
    public void setNeedSymptomsDetail(YesNoEnum needSymptomsDetail) {
        this.needSymptomsDetail = needSymptomsDetail;
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
        msg.append(", topCategoryId=").append(topCategoryId);
        msg.append(", categoryId=").append(categoryId);
        msg.append(", name=").append(name);
        msg.append(", clazz=").append(clazz);
        msg.append(", description=").append(description);
        msg.append(", imageId=").append(imageId);
        msg.append(", detailImageId=").append(detailImageId);
        msg.append(", serviceTimeDuration=").append(serviceTimeDuration);
        msg.append(", serviceTimeUnit=").append(serviceTimeUnit);
        msg.append(", servicePrice=").append(servicePrice);
        msg.append(", servicePriceCent=").append(servicePriceCent);
        msg.append(", serviceDiscount=").append(serviceDiscount);
        msg.append(", serviceDiscountCent=").append(serviceDiscountCent);
        msg.append(", serverIncome=").append(serverIncome);
        msg.append(", serverIncomeCent=").append(serverIncomeCent);
        msg.append(", needVisitPatientRecord=").append(needVisitPatientRecord);
        msg.append(", managerApproved=").append(managerApproved);
        msg.append(", managedBy=").append(managedBy);
        msg.append(", grade=").append(grade);
        msg.append("]");
        return msg.toString();
    }
}
