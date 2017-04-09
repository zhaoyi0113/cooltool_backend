package com.cooltoo.beans;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public class HospitalDepartmentBean {

    private int id;
    private int hospitalId;
    private String name;
    private String description;
    private int enable;
    private long imageId;
    private String imageUrl;
    private long disableImageId;
    private String disableImageUrl;
    private int parentId;
    private boolean parentValid;
    private String uniqueId;
    private List<HospitalDepartmentBean> subDepartment;
    private String phoneNumber;
    private Double longitude;
    private Double latitude;
    private long addressImageId;
    private String addressImageUrl;
    private String addressLink;
    private String address;// 科室地址
    private String outpatientAddress;//门诊地址
    private String transportation;//乘车方式
    private long   logo;
    private String logoUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEnable() {
        return this.enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public long getImageId() {
        return this.imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getDisableImageId() {
        return this.disableImageId;
    }

    public void setDisableImageId(long disableImageId) {
        this.disableImageId = disableImageId;
    }

    public String getDisableImageUrl() {
        return this.disableImageUrl;
    }

    public void setDisableImageUrl(String disableImageUrl) {
        this.disableImageUrl = disableImageUrl;
    }

    public int getParentId() {
        return this.parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public boolean getParentValid() {
        return this.parentValid;
    }

    public void setParentValid(boolean parentValid) {
        this.parentValid = parentValid;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<HospitalDepartmentBean> getSubDepartment() {
        return this.subDepartment;
    }

    public void setSubDepartment(List<HospitalDepartmentBean> subDepartment) {
        this.subDepartment = subDepartment;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public long getAddressImageId() {
        return addressImageId;
    }

    public void setAddressImageId(long addressImageId) {
        this.addressImageId = addressImageId;
    }

    public String getAddressImageUrl() {
        return addressImageUrl;
    }

    public void setAddressImageUrl(String addressImageUrl) {
        this.addressImageUrl = addressImageUrl;
    }

    public String getAddressLink() {
        return addressLink;
    }

    public void setAddressLink(String addressLink) {
        this.addressLink = addressLink;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOutpatientAddress() {
        return outpatientAddress;
    }

    public void setOutpatientAddress(String outpatientAddress) {
        this.outpatientAddress = outpatientAddress;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    public long getLogo() {
        return logo;
    }

    public void setLogo(long logo) {
        this.logo = logo;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", name=").append(name);
        msg.append(", description=").append(description);
        msg.append(", enable=").append(enable);
        msg.append(", imageId=").append(imageId);
        msg.append(", imageUrl=").append(imageUrl);
        msg.append(", disableImageId=").append(disableImageId);
        msg.append(", disableImageUrl=").append(disableImageUrl);
        msg.append(", parentId=").append(parentId);
        msg.append(", uniqueId=").append(uniqueId);
        msg.append(", phoneNumber=").append(phoneNumber);
        msg.append(", longitude=").append(longitude);
        msg.append(", latitude=").append(latitude);
        msg.append(", addressImageId=").append(addressImageId);
        msg.append(", addressImageUrl=").append(addressImageUrl);
        msg.append(", addressLink=").append(addressLink);
        msg.append(", address=").append(address);
        msg.append(", outpatientAddress=").append(outpatientAddress);
        msg.append(", transportation=").append(transportation);
        msg.append(", logo=").append(logo);
        msg.append(", logoUrl=").append(logoUrl);
        msg.append("]");
        return msg.toString();
    }
}
