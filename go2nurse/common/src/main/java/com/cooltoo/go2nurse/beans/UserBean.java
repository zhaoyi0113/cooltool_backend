package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.UserType;
import com.cooltoo.go2nurse.constants.UserHospitalizedStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2016/6/13.
 */
public class UserBean {

    public static final String FOLLOW_UP_ID = "follow-up_id";

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private GenderType gender;
    private Date birthday;
    private int age;
    private String mobile;
    private String password;
    private long profilePhoto;
    private String profilePhotoUrl;
    private UserAuthority authority;
    private UserType type;
    private String uniqueId;
    private String address;
    private UserHospitalizedStatus hasDecide;
    private int channel;
    private Map<String, Object> properties = new HashMap<>();

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

    public String getUniqueId() {
        return uniqueId;
    }

    public String getAddress() {
        return address;
    }

    public UserHospitalizedStatus getHasDecide() {
        return hasDecide;
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

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setHasDecide(UserHospitalizedStatus hasDecide) {
        this.hasDecide = hasDecide;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(String key, Object value) {
        if(null==properties) {
            properties = new HashMap<>();
        }
        properties.put(key, value);
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
        msg.append(", uniqueId=").append(uniqueId);
        msg.append(", address=").append(address);
        msg.append(", hasDecide=").append(hasDecide);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append(", channel=").append(channel);
        msg.append("]");
        return msg.toString();
    }
}
