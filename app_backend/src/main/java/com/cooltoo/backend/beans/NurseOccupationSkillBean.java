package com.cooltoo.backend.beans;

import com.cooltoo.constants.OccupationSkillType;

/**
 * Created by hp on 2016/4/10.
 */
public class NurseOccupationSkillBean {

    private int id;
    private long userId;
    private int skillId;
    private OccupationSkillType type = OccupationSkillType.SKILL;
    private int point;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public OccupationSkillType getType() {
        return type;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("userId=").append(userId).append(", ");
        msg.append("skillId=").append(skillId).append(", ");
        msg.append("point=").append(point);
        msg.append("]");
        return msg.toString();
    }
}
