package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.RegionBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import java.util.Date;

/**
 * Created by hp on 2016/7/2.
 */
public class UserAddressBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private int provinceId;
    private RegionBean province;
    private int cityId;
    private RegionBean city;
    private String address;
    private int grade;
    private YesNoEnum isDefault;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getUserId() {
        return userId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public String getAddress() {
        return address;
    }

    public int getGrade() {
        return grade;
    }

    public YesNoEnum getIsDefault() {
        return isDefault;
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

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setIsDefault(YesNoEnum isDefault) {
        this.isDefault = isDefault;
    }

    public RegionBean getProvince() {
        return province;
    }

    public void setProvince(RegionBean province) {
        this.province = province;
    }

    public RegionBean getCity() {
        return city;
    }

    public void setCity(RegionBean city) {
        this.city = city;
    }

    public String toAddress() {
        StringBuffer address = new StringBuffer();
        if (null!=province) {
            address.append(province.getName());
        }
        if (null!=city) {
            address.append(" ").append(city.getName());
        }
        if ((getAddress() instanceof String) && getAddress().trim().length()>0) {
            address.append(" ").append(getAddress().trim());
        }
        return address.toString();
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", provinceId=").append(provinceId);
        msg.append(", cityId=").append(cityId);
        msg.append(", address=").append(address);
        msg.append(", grade=").append(grade);
        msg.append(", isDefault=").append(isDefault);
        msg.append(", province=").append(province);
        msg.append(", city=").append(city);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
