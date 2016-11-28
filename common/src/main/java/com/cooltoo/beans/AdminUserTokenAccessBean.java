package com.cooltoo.beans;

import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by zhaolisong on 16/3/22.
 */
public class AdminUserTokenAccessBean {

    private long id;
    private long userId;
    private AdminUserType userType;
    private Date timeCreated;
    private String token;
    private CommonStatus status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public AdminUserType getUserType() {
        return userType;
    }

    public void setUserType(AdminUserType userType) {
        this.userType = userType;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append("userId=").append(userId).append(" ,");
        msg.append("userType=").append(userType).append(" ,");
        msg.append("timeCreated=").append(timeCreated).append(" ,");
        msg.append("token=").append(token).append(" ,");
        msg.append("status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
