package com.cooltoo.backend.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by hp on 2016/4/18.
 */
public class CathartProfilePhotoBean {
    private long id;
    private String name;
    private long imageId;
    private String imageUrl;
    private CommonStatus enable;
    private Date timeCreated;

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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CommonStatus getEnable() {
        return enable;
    }

    public void setEnable(CommonStatus enable) {
        this.enable = enable;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        StringBuffer msg = new StringBuffer();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", imageId=").append(imageId);
        msg.append(", imageUrl=").append(imageUrl);
        msg.append(", enable=").append(enable);
        msg.append(", timeCreated=").append(timeCreated).append("] ");
        return msg.toString();
    }
}
