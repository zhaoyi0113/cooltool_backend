package com.cooltoo.beans;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.RelationshipType;

import java.util.Date;

/**
 * Created by hp on 2016/5/30.
 */
public class NurseRelationshipBean {

    private long id;
    private long userId;
    private NurseBean user;
    private long relativeUserId;
    private NurseBean relativeUser;
    private RelationshipType relationType;
    private Date time;
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

    public long getRelativeUserId() {
        return relativeUserId;
    }

    public void setRelativeUserId(long relativeUserId) {
        this.relativeUserId = relativeUserId;
    }

    public RelationshipType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationshipType relationType) {
        this.relationType = relationType;
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

    public NurseBean getUser() {
        return user;
    }

    public void setUser(NurseBean user) {
        this.user = user;
    }

    public NurseBean getRelativeUser() {
        return relativeUser;
    }

    public void setRelativeUser(NurseBean relativeUser) {
        this.relativeUser = relativeUser;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", relativeUserId=").append(relativeUserId);
        msg.append(", relationType=").append(relationType);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", user=").append(user);
        msg.append(", relativeUser=").append(relativeUser);
        msg.append("]");
        return msg.toString();
    }
}