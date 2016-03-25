package com.cooltoo.backend.beans;

import com.cooltoo.constants.OccupationSkillType;

/**
 * Created by yzzhao on 3/10/16.
 */
public class OccupationSkillBean {
    private int id;
    private long imageId;
    private String name;
    private OccupationSkillType skillType;

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
}
