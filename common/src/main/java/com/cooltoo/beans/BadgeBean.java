package com.cooltoo.beans;

import com.cooltoo.constants.BadgeGrade;
import com.cooltoo.constants.SocialAbilityType;

import javax.ws.rs.FormParam;

/**
 * Created by yzzhao on 2/24/16.
 */
public class BadgeBean {
    private int id;
    private String name;
    private BadgeGrade grade;
    private long point;
    private long imageId;
    private String imageUrl;
    private int    abilityId;
    private SocialAbilityType abilityType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BadgeGrade getGrade() {
        return grade;
    }

    public void setGrade(BadgeGrade grade) {
        this.grade = grade;
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

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
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

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(BadgeBean.class).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", grade=").append(grade);
        msg.append(", point=").append(point);
        msg.append(", abilityId=").append(abilityId);
        msg.append(", abilityType=").append(abilityType);
        msg.append(", imageId=").append(imageId);
        msg.append(", imageUrl=").append(imageUrl);
        msg.append("]");
        return msg.toString();
    }
}
