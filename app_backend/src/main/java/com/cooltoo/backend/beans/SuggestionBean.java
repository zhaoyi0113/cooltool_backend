package com.cooltoo.backend.beans;

import com.cooltoo.constants.SuggestionStatus;

import java.util.Date;

/**
 * Created by zhaolisong on 16/4/6.
 */
public class SuggestionBean {
    private long   id;
    private long   userId;
    private String userName;
    private String suggestion;
    private Date   timeCreated;
    private SuggestionStatus status;

    public long getId() {
        return this.id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public SuggestionStatus getStatus() {
        return status;
    }

    public void setStatus(SuggestionStatus status) {
        this.status = status;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append(" id=").append(id).append(", ");
        msg.append(" userId=").append(userId).append(", ");
        msg.append(" suggestion=").append(suggestion).append(", ");
        msg.append(" timeCreated=").append(timeCreated).append("] ");
        return msg.toString();
    }
}
