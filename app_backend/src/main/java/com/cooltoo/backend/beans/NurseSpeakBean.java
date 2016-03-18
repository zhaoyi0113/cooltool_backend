package com.cooltoo.backend.beans;

import com.cooltoo.constants.SpeakType;

import java.util.Date;
import java.util.List;

/**
 * Created by yzzhao on 3/15/16.
 */
public class NurseSpeakBean {
    private long id;
    private long userId;
    private String content;
    private Date time;
    private String imageUrl;
    private SpeakType speakType;
    List<NurseSpeakCommentBean> comments;

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

    public SpeakType getSpeakType() {
        return speakType;
    }

    public void setSpeakType(SpeakType speakType) {
        this.speakType = speakType;
    }

    public List<NurseSpeakCommentBean> getComments() {
        return this.comments;
    }

    public void setComments(List<NurseSpeakCommentBean> comments) {
        this.comments = comments;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("userId=").append(userId).append(" , ");
        msg.append("time=").append(time).append(" , ");
        msg.append("content=").append(content).append(" , ");
        msg.append("imageUrl=").append(imageUrl).append(" , ");
        msg.append("speakType=").append(speakType).append("\r\n");
        int count = null==comments ? 0 : comments.size();
        for (int i = 0; i < count; i++) {
            msg.append(comments.get(i)).append("\r\n");
        }
        msg.append("\r\n").append("]");
        return msg.toString();
    }
}
