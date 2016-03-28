package com.cooltoo.backend.beans;

import com.cooltoo.constants.OccupationSkillType;

import java.util.Date;

/**
 * Created by yzzhao on 3/15/16.
 */
public class NurseSkillNominationBean {

    private int skillId;
    private OccupationSkillType skillType;
    private String skillName;
    private String skillImageUrl;
    private String skillDisableImageUrl;
    private long skillNominateCount;

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public OccupationSkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(OccupationSkillType skillType) {
        this.skillType = skillType;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillImageUrl() {
        return skillImageUrl;
    }

    public void setSkillImageUrl(String skillImageUrl) {
        this.skillImageUrl = skillImageUrl;
    }

    public String getSkillDisableImageUrl() {
        return skillDisableImageUrl;
    }

    public void setSkillDisableImageUrl(String skillDisableImageUrl) {
        this.skillDisableImageUrl = skillDisableImageUrl;
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
        msg.append("skillId=").append(skillId).append(", ");
        msg.append("skillType=").append(skillType).append(", ");
        msg.append("skillName=").append(skillName).append(", ");
        msg.append("skillImageUrl=").append(skillImageUrl).append(", ");
        msg.append("skillDisableImageUrl=").append(skillDisableImageUrl).append(", ");
        msg.append("skillNominateCount=").append(skillNominateCount);
        return msg.toString();
    }
}
