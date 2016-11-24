package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;

import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/06.
 */
public class NurseVisitPatientBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long nurseId;
    private NurseBean nurse;
    private long userId;
    private UserBean user;
    private long patientId;
    private PatientBean patient;
    private String serviceItem;
    private List<NurseVisitPatientServiceItemBean> serviceItems;
    private String visitRecord;
    private List<String> recordImages;
    private long patientSign;
    private String patientSignUrl;
    private long orderId;
    private ServiceOrderBean order;
    private ServiceVendorType vendorType;
    private long vendorId;
    private long vendorDepartId;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public String getServiceItem() {
        return serviceItem;
    }

    public String getVisitRecord() {
        return visitRecord;
    }

    public long getPatientSign() {
        return patientSign;
    }

    public long getUserId() {
        return userId;
    }

    public long getPatientId() {
        return patientId;
    }

    public long getNurseId() {
        return nurseId;
    }

    public ServiceVendorType getVendorType() {
        return vendorType;
    }

    public long getVendorId() {
        return vendorId;
    }

    public long getVendorDepartId() {
        return vendorDepartId;
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

    public void setVisitRecord(String visitRecord) {
        this.visitRecord = visitRecord;
    }

    public void setPatientSign(long patientSign) {
        this.patientSign = patientSign;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public void setVendorType(ServiceVendorType vendorType) {
        this.vendorType = vendorType;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public void setVendorDepartId(long vendorDepartId) {
        this.vendorDepartId = vendorDepartId;
    }

    public NurseBean getNurse() {
        return nurse;
    }

    public void setNurse(NurseBean nurse) {
        this.nurse = nurse;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public PatientBean getPatient() {
        return patient;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public List<String> getRecordImages() {
        return recordImages;
    }

    public void setRecordImages(List<String> recordImages) {
        this.recordImages = recordImages;
    }

    public String getPatientSignUrl() {
        return patientSignUrl;
    }

    public void setPatientSignUrl(String patientSignUrl) {
        this.patientSignUrl = patientSignUrl;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public ServiceOrderBean getOrder() {
        return order;
    }

    public void setOrder(ServiceOrderBean order) {
        this.order = order;
    }

    public List<NurseVisitPatientServiceItemBean> getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(List<NurseVisitPatientServiceItemBean> serviceItems) {
        this.serviceItems = serviceItems;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", serviceItem=").append(serviceItem);
        msg.append(", visitRecord=").append(visitRecord);
        msg.append(", patientSign=").append(patientSign);
        msg.append(", vendorType=").append(vendorType);
        msg.append(", vendorId=").append(vendorId);
        msg.append(", vendorDepartId=").append(vendorDepartId);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
