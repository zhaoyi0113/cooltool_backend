package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.*;
import com.cooltoo.go2nurse.constants.UserHospitalizedStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/13.
 */
@Entity
@Table(name = "go2nurse_user")
public class UserEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private GenderType gender;
    private Date birthday;
    private String mobile;
    private String password;
    private long profilePhoto;
    private UserAuthority authority;
    private UserType type;
    private String uniqueId;
    private String address;
    private UserHospitalizedStatus hasDecide;
    private AppChannel channel;

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

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "gender")
    @Enumerated
    public GenderType getGender() {
        return gender;
    }

    @Column(name = "birthday")
    public Date getBirthday() {
        return birthday;
    }

    @Column(name = "mobile")
    public String getMobile() {
        return mobile;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    @Column(name = "profile_photo_id")
    public long getProfilePhoto() {
        return profilePhoto;
    }

    @Column(name = "authority")
    @Enumerated
    public UserAuthority getAuthority() {
        return authority;
    }

    @Column(name = "type")
    public UserType getType() {
        return type;
    }

    @Column(name = "unique_id")
    public String getUniqueId() {
        return uniqueId;
    }

    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    @Column(name = "has_decide")
    @Enumerated
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

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setHasDecide(UserHospitalizedStatus hasDecide) {
        this.hasDecide = hasDecide;
    }

    @Column(name = "channel")
    @Enumerated
    public AppChannel getChannel() {
        return channel;
    }

    public void setChannel(AppChannel channel) {
        this.channel = channel;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", gender=").append(gender);
        msg.append(", birthday=").append(birthday);
        msg.append(", profile=").append(profilePhoto);
        msg.append(", mobile=").append(mobile);
        msg.append(", password=").append(password);
        msg.append(", authority=").append(authority);
        msg.append(", type=").append(type);
        msg.append(", uniqueId=").append(uniqueId);
        msg.append(", address=").append(address);
        msg.append(", hasDecide=").append(hasDecide);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
