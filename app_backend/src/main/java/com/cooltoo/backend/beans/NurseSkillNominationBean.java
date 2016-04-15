package com.cooltoo.backend.beans;

import com.cooltoo.constants.SocialAbilityType;

/**
 * Created by yzzhao on 3/15/16.
 */
public class NurseSkillNominationBean {

    private long userId;
    private int skillId;
    private SocialAbilityType skillType;
    private long skillNominateCount;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public SocialAbilityType getSkillType() {
        return skillType;
    }

    public void setSkillType(SocialAbilityType skillType) {
        this.skillType = skillType;
    }

    public long getSkillNominateCount() {
        return skillNominateCount;
    }

    public void setSkillNominateCount(long skillNominateCount) {
        this.skillNominateCount = skillNominateCount;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("userId=").append(userId).append(", ");
        msg.append("skillId=").append(skillId).append(", ");
        msg.append("skillType=").append(skillType).append(", ");
        msg.append("skillNominateCount=").append(skillNominateCount);
        return msg.toString();
    }
}
