package com.cooltoo.entities;

import com.cooltoo.constants.BadgeGrade;
import com.cooltoo.constants.SocialAbilityType;

import javax.persistence.*;

/**
 * Created by yzzhao on 2/24/16.
 */
@Entity
@Table(name = "badge")
public class BadgeEntity {

    private int    id;
    private String name;
    private BadgeGrade grade;
    private long   point;
    private long   imageId;
    private int    abilityId;
    private SocialAbilityType abilityType;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "grade")
    @Enumerated
    public BadgeGrade getGrade() {
        return grade;
    }

    public void setGrade(BadgeGrade grade) {
        this.grade = grade;
    }

    @Column(name = "point")
    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    @Column(name = "file_id")
    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    @Column(name = "ability_id")
    public int getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(int abilityId) {
        this.abilityId = abilityId;
    }

    @Column(name = "ability_type")
    @Enumerated
    public SocialAbilityType getAbilityType() {
        return abilityType;
    }

    public void setAbilityType(SocialAbilityType abilityType) {
        this.abilityType = abilityType;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append("name=").append(name).append(" ,");
        msg.append("grade=").append(grade).append(" ,");
        msg.append("point=").append(point).append(" ,");
        msg.append("abilityId=").append(abilityId).append(" ,");
        msg.append("abilityType=").append(abilityType).append(" ,");
        msg.append("imageId=").append(imageId).append("]");
        return msg.toString();
    }
}