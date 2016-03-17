package com.cooltoo.backend.beans;

import java.util.Date;

/**
 * Created by yzzhao on 3/15/16.
 */
public class NurseSpeakBean {
    private long id;
    private long userId;
    private String content;
    private Date time;
    private String imageUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("userId=").append(userId).append(" , ");
        msg.append("time=").append(time).append(" , ");
        msg.append("content=").append(content).append(" , ");
        msg.append("imageUrl=").append(imageUrl);
        msg.append("]");
        return msg.toString();
    }
}
