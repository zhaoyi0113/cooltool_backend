package com.cooltoo.backend.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;

import java.util.Date;

/**
 * Created by hp on 2016/6/2.
 */
public class NurseSpeakTopicSubscriberBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long topicId;
    private long userId;
    private UserType userType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", topicId=").append(topicId);
        msg.append(", userId=").append(userId);
        msg.append(", userType=").append(userType);
        msg.append("]");
        return msg.toString();
    }

}
