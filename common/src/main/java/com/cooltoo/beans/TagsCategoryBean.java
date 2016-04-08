package com.cooltoo.beans;

import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/6.
 */
public class TagsCategoryBean {
    private long id;
    private String name;
    private long imageId;
    private String imageUrl;
    private Date timeCreated;
    private List<TagsBean> tags;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<TagsBean> getTags() {
        return this.tags;
    }

    public void setTags(List<TagsBean> tags) {
        this.tags = tags;
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
        msg.append(" name=").append(name).append(", ");
        msg.append(" imageId=").append(imageId).append(", ");
        msg.append(" timeCreated=").append(timeCreated).append(", ");
        msg.append(" tags=").append(tags).append("] ");
        return msg.toString();
    }
}
