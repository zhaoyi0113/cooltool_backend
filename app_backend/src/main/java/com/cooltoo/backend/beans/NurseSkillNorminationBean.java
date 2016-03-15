package com.cooltoo.backend.beans;

import java.util.Date;

/**
 * Created by yzzhao on 3/15/16.
 */
public class NurseSkillNorminationBean {

    private int skillId;
    private String skillName;
    private String skillImageUrl;
    private long skillNominateCount;

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
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

    public long getSkillNominateCount() {
        return skillNominateCount;
    }

    public void setSkillNominateCount(long skillNominateCount) {
        this.skillNominateCount = skillNominateCount;
    }
}
