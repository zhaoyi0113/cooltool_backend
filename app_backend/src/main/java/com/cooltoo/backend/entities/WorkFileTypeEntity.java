package com.cooltoo.backend.entities;

import com.cooltoo.constants.WorkFileType;

import javax.persistence.*;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Entity
@Table(name = "workfile_type")
public class WorkFileTypeEntity {
    private int id;
    private String name;
    private WorkFileType type;
    private int factor;
    private int maxFileCount;
    private int minFileCount;
    private long imageId;
    private long disableImageId;

    @GeneratedValue
    @Id
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

    @Column(name = "type")
    @Enumerated
    public WorkFileType getType() {
        return type;
    }

    public void setType(WorkFileType type) {
        this.type = type;
    }

    @Column(name = "factor")
    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    @Column(name = "max_file_count")
    public int getMaxFileCount() {
        return maxFileCount;
    }

    public void setMaxFileCount(int maxFileCount) {
        this.maxFileCount = maxFileCount;
    }

    @Column(name = "min_file_count")
    public int getMinFileCount() {
        return minFileCount;
    }

    public void setMinFileCount(int minFileCount) {
        this.minFileCount = minFileCount;
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

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("name=").append(name).append(", ");
        msg.append("type=").append(type).append(", ");
        msg.append("factor=").append(factor).append(", ");
        msg.append("maxFileCount=").append(maxFileCount).append(", ");
        msg.append("minFileCount=").append(minFileCount).append(", ");
        msg.append("imageId=").append(imageId).append(", ");
        msg.append("disableImageId").append(disableImageId).append("]");
        return msg.toString();
    }
}
