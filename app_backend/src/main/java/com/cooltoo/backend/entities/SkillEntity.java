package com.cooltoo.backend.entities;

import com.cooltoo.constants.OccupationSkillStatus;

import javax.persistence.*;

/**
 * Created by yzzhao on 3/10/16.
 */
@Entity
@Table(name = "nursego_skill")
public class SkillEntity {
    private int id;
    private long imageId;
    private long disableImageId;
    private String name;
    private int factor;
    private OccupationSkillStatus status;
    private String description;


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

    @Column(name = "disable_image_id")
    public long getDisableImageId() {
        return disableImageId;
    }

    public void setDisableImageId(long disableImageId) {
        this.disableImageId = disableImageId;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "factor")
    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    @Column(name = "status")
    public OccupationSkillStatus getStatus() {
        return status;
    }

    public void setStatus(OccupationSkillStatus status) {
        this.status = status;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("imageId=").append(imageId).append(", ");
        msg.append("disableImageId=").append(disableImageId).append(", ");
        msg.append("name=").append(name).append(", ");
        msg.append("description=").append(description).append(", ");
        msg.append("factor=").append(factor);
        return msg.toString();
    }
}
