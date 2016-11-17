package com.cooltoo.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.DeviceType;

import java.util.Date;

/**
 * Created by yzzhao on 4/29/16.
 */
public class NurseDeviceTokensBean {

    private long id;
    private long userId;
    private String deviceToken;
    private CommonStatus status;
    private Date timeCreated;
    private DeviceType deviceType;

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

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}
