package com.cooltoo.entities;

import javax.persistence.*;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Entity
@Table(name = "cooltoo_hospital_department")
public class HospitalDepartmentEntity {
    private int id;
    private String name;
    private String description;
    private int enable;
    private long imageId;
    private long disableImageId;
    private int parentId;

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

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "enable")
    public int getEnable() {
        return this.enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    @Column(name = "image_id")
    public long getImageId() {
        return this.imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    @Column(name = "disable_image_id")
    public long getDisableImageId() {
        return this.disableImageId;
    }

    public void setDisableImageId(long disableImageId) {
        this.disableImageId = disableImageId;
    }

    @Column(name = "parent_id")
    public int getParentId() {
        return this.parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("name=").append(name).append(" , ");
        msg.append("description=").append(description).append(" , ");
        msg.append("enable=").append(enable).append(" , ");
        msg.append("imageId=").append(imageId).append(" , ");
        msg.append("disableImageId=").append(disableImageId).append(" , ");
        msg.append("parentId=").append(parentId).append("]");
        return msg.toString();
    }
}
