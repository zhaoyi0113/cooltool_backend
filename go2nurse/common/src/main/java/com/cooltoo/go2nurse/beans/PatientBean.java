package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.YesNoEnum;

import java.util.Date;

/**
 * Created by yzzhao on 2/29/16.
 */
public class PatientBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private GenderType gender;
    private Date birthday;
    private long age;
    private String identityCard;
    private String mobile;
    private YesNoEnum isDefault;
    private long headImageId;
    private String headImageUrl;
    private YesNoEnum isSelf;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public GenderType getGender() {
        return gender;
    }
    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getIdentityCard() {
        return identityCard;
    }
    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public YesNoEnum getIsDefault() {
        return isDefault;
    }
    public void setIsDefault(YesNoEnum isDefault) {
        this.isDefault = isDefault;
    }

    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    public CommonStatus getStatus() {
        return status;
    }
    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public long getHeadImageId() {
        return headImageId;
    }
    public void setHeadImageId(long headImageId) {
        this.headImageId = headImageId;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }
    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public YesNoEnum getIsSelf() {
        return isSelf;
    }
    public void setIsSelf(YesNoEnum isSelf) {
        this.isSelf = isSelf;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", name=").append(name);
        msg.append(", gender=").append(gender);
        msg.append(", birthday=").append(birthday);
        msg.append(", identityCard=").append(identityCard);
        msg.append(", mobile=").append(mobile);
        msg.append(", isDefault=").append(isDefault);
        msg.append(", isSelf=").append(isSelf);
        msg.append("]");
        return msg.toString();
    }
}
