package com.cooltoo.beans;

import com.cooltoo.constants.AdminUserType;

import java.util.Date;

/**
 * Created by zhaolisong on 16/3/22.
 */
public class AdminUserBean {

    private long id;
    private AdminUserType userType;
    private String userName;
    private String password;
    private String phoneNumber;
    private String email;
    private Date timeCreated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AdminUserType getUserType() {
        return userType;
    }

    public void setUserType(AdminUserType userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append("userType=").append(userType).append(" ,");
        msg.append("userName=").append(userName).append(" ,");
        msg.append("password=").append(password).append(" ,");
        msg.append("phoneNumber=").append(phoneNumber).append(" ,");
        msg.append("email=").append(email).append(" ,");
        msg.append("timeCreated=").append(timeCreated);
        msg.append("]");
        return msg.toString();
    }
}
