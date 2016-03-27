package com.cooltoo.backend.entities;

import com.cooltoo.constants.OccupationSkillType;

import javax.persistence.*;

/**
 * Created by yzzhao on 3/10/16.
 */
@Entity
@Table(name = "occupation_skill")
public class OccupationSkillEntity {
    private int id;
    private long imageId;
    private String name;
    private OccupationSkillType type;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "image_id")
    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="type")
    @Enumerated
    public OccupationSkillType getType() {
        return type;
    }

    public void setType(OccupationSkillType type) {
        this.type = type;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("imageId=").append(imageId).append(", ");
        msg.append("name=").append(name).append(", ");
        msg.append("type=").append(type.name());
        return msg.toString();
    }
}
