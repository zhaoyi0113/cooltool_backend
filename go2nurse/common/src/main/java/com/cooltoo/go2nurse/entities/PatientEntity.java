package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.YesNoEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yzzhao on 2/29/16.
 */
@Entity
@Table(name = "go2nurse_patient")
public class PatientEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private GenderType gender;
    private Date birthday;
    private String identityCard;
    private String mobile;
    private YesNoEnum isDefault;
    private long headImageId;
    private YesNoEnum isSelf;

    @Id
    @Column(name = "id")
    @GeneratedValue
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "gender")
    @Enumerated
    public GenderType getGender() {
        return gender;
    }
    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    @Column(name = "birthday")
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    @Column(name = "identity_card")
    public String getIdentityCard() {
        return identityCard;
    }
    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    @Column(name = "mobile")
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(name = "is_default")
    @Enumerated
    public YesNoEnum getIsDefault() {
        return isDefault;
    }
    public void setIsDefault(YesNoEnum isDefault) {
        this.isDefault = isDefault;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }
    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "head_image_id")
    public long getHeadImageId() {
        return headImageId;
    }
    public void setHeadImageId(long headImageId) {
        this.headImageId = headImageId;
    }

    @Column(name = "is_self")
    @Enumerated
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
