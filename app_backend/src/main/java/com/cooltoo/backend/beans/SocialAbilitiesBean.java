package com.cooltoo.backend.beans;

import com.cooltoo.constants.SocialAbilityType;

/**
 * Created by zhaolisong on 16/3/25.
 */
public class SocialAbilitiesBean {
    private long userId;
    private int skillId;
    private String skillName;
    private SocialAbilityType skillType;
    private long factor;
    private long nominatedCount;
    private int point;
    private long imageId;
    private String imageUrl;
    private long disableImageId;
    private String disableImagePath;

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

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public SocialAbilityType getSkillType() {
        return skillType;
    }

    public void setSkillType(SocialAbilityType skillType) {
        this.skillType = skillType;
    }

    public long getFactor() {
        return factor;
    }

    public void setFactor(long factor) {
        this.factor = factor;
    }

    public long getNominatedCount() {
        return nominatedCount;
    }

    public void setNominatedCount(long nominatedCount) {
        this.nominatedCount = nominatedCount;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getDisableImageId() {
        return disableImageId;
    }

    public void setDisableImageId(long disableImageId) {
        this.disableImageId = disableImageId;
    }

    public String getDisableImagePath() {
        return disableImagePath;
    }

    public void setDisableImagePath(String disableImagePath) {
        this.disableImagePath = disableImagePath;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("userId=").append(userId).append(", ");
        msg.append("skillId=").append(skillId).append(", ");
        msg.append("skillName=").append(skillName).append(", ");
        msg.append("skillType=").append(skillType).append(", ");
        msg.append("factor=").append(factor).append(", ");
        msg.append("nominatedCount=").append(nominatedCount).append(", ");
        msg.append("point=").append(point).append(", ");
        msg.append("imageId=").append(imageId).append(", ");
        msg.append("imageUrl=").append(imageUrl).append(", ");
        msg.append("disableImageId=").append(disableImageId).append(", ");
        msg.append("disableImagePath=").append(disableImagePath).append("] ");
        return msg.toString();
    }
}
