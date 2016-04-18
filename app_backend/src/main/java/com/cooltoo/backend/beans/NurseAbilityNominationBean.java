package com.cooltoo.backend.beans;

import com.cooltoo.constants.SocialAbilityType;

/**
 * Created by yzzhao on 3/15/16.
 */
public class NurseAbilityNominationBean {

    private long userId;
    private int abilityId;
    private SocialAbilityType abilityType;
    private long abilityNominateCount;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public long getAbilityNominateCount() {
        return abilityNominateCount;
    }

    public void setAbilityNominateCount(long abilityNominateCount) {
        this.abilityNominateCount = abilityNominateCount;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("userId=").append(userId).append(", ");
        msg.append("abilityId=").append(abilityId).append(", ");
        msg.append("abilityType=").append(abilityType).append(", ");
        msg.append("abilityNominateCount=").append(abilityNominateCount);
        return msg.toString();
    }
}
