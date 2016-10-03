package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaoyi0113 on 10/3/16.
 */
@Entity
@Table(name = "go2nurse_user_wechat_token_access")
public class UserWeChatTokenAccessEntity {

    private long id;
    private String token;
    private int wechatAccountId;
    private Date timeCreated;
    private CommonStatus status;

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(name = "wechat_account_id")
    public int getWechatAccountId() {
        return wechatAccountId;
    }

    public void setWechatAccountId(int wechatAccountId) {
        this.wechatAccountId = wechatAccountId;
    }

    @Column(name = "time_created")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }
}
