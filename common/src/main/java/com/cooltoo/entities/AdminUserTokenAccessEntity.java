package com.cooltoo.entities;

import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 16/3/22.
 */
@Entity
@Table(name = "nursego_admin_user_token_access")
public class AdminUserTokenAccessEntity  {
    private long id;
    private long userId;
    private AdminUserType userType;
    private Date timeCreated;
    private String token;
    private CommonStatus status;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Column(name = "user_type")
    @Enumerated
    public AdminUserType getUserType() {
        return userType;
    }

    public void setUserType(AdminUserType userType) {
        this.userType = userType;
    }

    @Column(name = "time_created")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(name = "status")
    @Enumerated
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
