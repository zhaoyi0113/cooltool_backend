package com.cooltoo.backend.beans;

import com.cooltoo.constants.OccupationSkillType;

/**
 * Created by yzzhao on 3/10/16.
 */
public class OccupationSkillBean {
    private int id;
    private long imageId;
    private long disableImageId;
    private String name;
    private OccupationSkillType skillType;

    private int factor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public long getDisableImageId() {
        return disableImageId;
    }

    public void setDisableImageId(long disableImageId) {
        this.disableImageId = disableImageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OccupationSkillType getType() {
        return skillType;
    }

    public void setType(OccupationSkillType skillType) {
        this.skillType = skillType;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("imageId=").append(imageId).append(", ");
        msg.append("disableImageId=").append(disableImageId).append(", ");
        msg.append("name=").append(name).append(", ");
        msg.append("factor=").append(factor).append(" , ");
        msg.append("skillType=").append(skillType.name());
        return msg.toString();
    }
}
