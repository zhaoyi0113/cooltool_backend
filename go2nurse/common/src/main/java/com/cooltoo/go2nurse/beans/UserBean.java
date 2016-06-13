package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.UserType;

import java.util.Date;

/**
 * Created by hp on 2016/6/13.
 */
public class UserBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private GenderType gender;
    private Date birthday;
    private String mobile;
    private String password;
    private long profilePhoto;
    private String profilePhotoUrl;
    private UserAuthority authority;
    private UserType type;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public GenderType getGender() {
        return gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getMobile() {
        return mobile;
    }

    public String getPassword() {
        return password;
    }

    public long getProfilePhoto() {
        return profilePhoto;
    }

    public UserAuthority getAuthority() {
        return authority;
    }

    public UserType getType() {
        return type;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfilePhoto(long profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setAuthority(UserAuthority authority) {
        this.authority = authority;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", gender=").append(gender);
        msg.append(", birthday=").append(birthday);
        msg.append(", profile=").append(profilePhoto);
        msg.append(", profileUrl=").append(profilePhotoUrl);
        msg.append(", mobile=").append(mobile);
        msg.append(", password=").append(password);
        msg.append(", authority=").append(authority);
        msg.append(", type=").append(type);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
