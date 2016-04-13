package com.cooltoo.backend.beans;

import java.util.Date;

/**
 * Created by hp on 2016/3/18.
 */
public class NurseSpeakThumbsUpBean {
    private long id;
    private long nurseSpeakId;
    private long thumbsUpUserId;
    private String thumbsUpUserName;
    private String thumbsUpUserHeadImageUrl;
    private long speakThumbsUpCount;
    private Date time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNurseSpeakId() {
        return nurseSpeakId;
    }

    public void setNurseSpeakId(long nurseSpeakId) {
        this.nurseSpeakId = nurseSpeakId;
    }

    public long getThumbsUpUserId() {
        return thumbsUpUserId;
    }

    public void setThumbsUpUserId(long thumbsUpUserId) {
        this.thumbsUpUserId = thumbsUpUserId;
    }

    public long getSpeakThumbsUpCount() {
        return this.speakThumbsUpCount;
    }

    public void setSpeakThumbsUpCount(long speakThumbsUpCount) {
        this.speakThumbsUpCount = speakThumbsUpCount;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getThumbsUpUserName() {
        return thumbsUpUserName;
    }

    public void setThumbsUpUserName(String thumbsUpUserName) {
        this.thumbsUpUserName = thumbsUpUserName;
    }

    public String getThumbsUpUserHeadImageUrl() {
        return thumbsUpUserHeadImageUrl;
    }

    public void setThumbsUpUserHeadImageUrl(String thumbsUpUserHeadImageUrl) {
        this.thumbsUpUserHeadImageUrl = thumbsUpUserHeadImageUrl;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append("nurseSpeakId=").append(nurseSpeakId).append(" ,");
        msg.append("thumbsUpUserId=").append(thumbsUpUserId).append(" ,");
        msg.append("thumbsUpUserName=").append(thumbsUpUserName).append(" ,");
        msg.append("thumbsUpUserHeadImageUrl=").append(thumbsUpUserHeadImageUrl).append(" ,");
        msg.append("time=").append(time);
        msg.append(" ]");
        return msg.toString();
    }
}