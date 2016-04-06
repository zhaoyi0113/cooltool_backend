package com.cooltoo.beans;

import java.util.Date;

/**
 * Created by zhaolisong on 16/4/6.
 */
public class TagsBean {
    private long id;
    private long categoryId;
    private String name;
    private long imageId;
    private String imageUrl;
    private Date timeCreated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append(" id=").append(id).append(", ");
        msg.append(" categoryId=").append(categoryId).append(", ");
        msg.append(" name=").append(name).append(", ");
        msg.append(" imageId=").append(imageId).append(", ");
        msg.append(" imageUrl=").append(imageUrl).append(", ");
        msg.append(" timeCreated=").append(timeCreated).append("] ");
        return msg.toString();
    }
}
