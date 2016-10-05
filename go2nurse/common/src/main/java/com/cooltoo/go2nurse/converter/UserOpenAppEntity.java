package com.cooltoo.go2nurse.converter;

import com.cooltoo.constants.AppChannel;
import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;

/**
 * Created by yzzhao on 8/14/16.
 */
@Entity
@Table(name = "go2nurse_user_openapp_desc")
public class UserOpenAppEntity {

    private long id;
    private String openid;
    private String unionid;
    private String data;
    private AppChannel channel;
    private long userId;
    private CommonStatus status;
    private long createdAt;
    private String appId;

    @Id
    @Column(name = "id")
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "openid")
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Column(name = "unionid")
    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    @Column(name = "data")
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Column(name = "channel")
    public AppChannel getChannel() {
        return channel;
    }

    public void setChannel(AppChannel channel) {
        this.channel = channel;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Column(name = "status")
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "created_at")
    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "appid")
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}

