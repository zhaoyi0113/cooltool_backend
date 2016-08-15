package com.cooltoo.entities;

import javax.persistence.*;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Entity
@Table(name = "cooltoo_hospital_department")
public class HospitalDepartmentEntity {
    private int id;
    private int hospitalId;
    private String name;
    private String description;
    private int enable;
    private long imageId;
    private long disableImageId;
    private int parentId;
    private String uniqueId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "hospital_id")
    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
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

    @Column(name = "unique_id")
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", description=").append(description);
        msg.append(", enable=").append(enable);
        msg.append(", imageId=").append(imageId);
        msg.append(", disableImageId=").append(disableImageId);
        msg.append(", parentId=").append(parentId);
        msg.append(", uniqueId=").append(uniqueId);
        msg.append("]");
        return msg.toString();
    }
}
