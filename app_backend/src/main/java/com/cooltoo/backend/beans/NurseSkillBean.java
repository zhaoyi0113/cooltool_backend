package com.cooltoo.backend.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SocialAbilityType;

import java.util.Date;

/**
 * Created by hp on 2016/4/10.
 */
public class NurseSkillBean {

    private int id;
    private long userId;
    private int skillId;
    private SocialAbilityType type = SocialAbilityType.SKILL;
    private int point;
    private Date time;
    private CommonStatus status;

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

    public SocialAbilityType getType() {
        return type;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
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
        msg.append("id=").append(id).append(", ");
        msg.append(", userId=").append(userId).append(", ");
        msg.append(", skillId=").append(skillId).append(", ");
        msg.append(", point=").append(point);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", type=").append(type);
        msg.append("]");
        return msg.toString();
    }
}
