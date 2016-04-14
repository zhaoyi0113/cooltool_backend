package com.cooltoo.backend.beans;

import java.util.Date;

/**
 * Created by zhaolisong on 16/4/14.
 */
public class ImagesInSpeakBean {
    private long id;
    private long speakId;
    private long imageId;
    private String imageUrl;
    private Date timeCreated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSpeakId() {
        return speakId;
    }

    public void setSpeakId(long speakId) {
        this.speakId = speakId;
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

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("speakId=").append(speakId).append(", ");
        msg.append("imageId=").append(imageId).append(", ");
        msg.append("imageUrl=").append(imageUrl).append(", ");
        msg.append("timeCreated=").append(timeCreated).append("] ");
        return msg.toString();
    }
}
