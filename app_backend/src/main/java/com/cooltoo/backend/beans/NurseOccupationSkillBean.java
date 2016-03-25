package com.cooltoo.backend.beans;

/**
 * Created by zhaolisong on 16/3/25.
 */
public class NurseOccupationSkillBean {
    private int id;
    private long userId;
    private int skillId;
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
