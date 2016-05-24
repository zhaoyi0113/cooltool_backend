package com.cooltoo.backend.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.constants.UserType;

import java.util.Date;

/**
 * Created by hp on 2016/5/21.
 */
public class NurseIntegrationBean {
    private long id;
    private long userId;
    private UserType userType;
    private long reasonId;
    private int abilityId;
    private SocialAbilityType abilityType;
    private long point;
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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public long getReasonId() {
        return reasonId;
    }

    public void setReasonId(long reasonId) {
        this.reasonId = reasonId;
    }

    public int getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(int abilityId) {
        this.abilityId = abilityId;
    }

    public SocialAbilityType getAbilityType() {
        return abilityType;
    }

    public void setAbilityType(SocialAbilityType abilityType) {
        this.abilityType = abilityType;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
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

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", userType=").append(userType);
        msg.append(", reasonId=").append(reasonId);
        msg.append(", abilityId=").append(abilityId);
        msg.append(", abilityType=").append(abilityType);
        msg.append(", point=").append(point);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
