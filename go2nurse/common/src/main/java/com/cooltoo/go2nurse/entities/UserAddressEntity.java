package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hp on 2016/7/2.
 */
@Entity
@Table(name = "go2nurse_user_address")
public class UserAddressEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private int provinceId;
    private int cityId;
    private String address;
    private int grade;
    private YesNoEnum isDefault;

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

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    @Column(name = "province_id")
    public int getProvinceId() {
        return provinceId;
    }

    @Column(name = "city_id")
    public int getCityId() {
        return cityId;
    }

    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    @Column(name = "grade")
    public int getGrade() {
        return grade;
    }

    @Column(name = "is_default")
    @Enumerated
    public YesNoEnum getIsDefault() { return isDefault; }

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
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
