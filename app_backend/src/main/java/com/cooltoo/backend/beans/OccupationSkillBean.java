package com.cooltoo.backend.beans;

import com.cooltoo.constants.OccupationSkillStatus;

/**
 * Created by yzzhao on 3/10/16.
 */
public class OccupationSkillBean {
    private int id;
    private long imageId;
    private long disableImageId;
    private String name;
    private String imageUrl;
    private String disableImageUrl;
    private int factor;
    private OccupationSkillStatus status;

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

    public String getImageUrl() {
        return this.imageUrl;
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

    public String getDisableImageUrl() {
        return this.disableImageUrl;
    }

    public void setDisableImageUrl(String disableImageUrl) {
        this.disableImageUrl = disableImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public OccupationSkillStatus getStatus() {
        return status;
    }

    public void setStatus(OccupationSkillStatus status) {
        this.status = status;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("imageId=").append(imageId).append(", ");
        msg.append("disableImageId=").append(disableImageId).append(", ");
        msg.append("name=").append(name).append(", ");
        msg.append("status=").append(status).append(", ");
        msg.append("factor=").append(factor);
        return msg.toString();
    }
}
