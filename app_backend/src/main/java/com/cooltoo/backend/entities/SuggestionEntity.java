package com.cooltoo.backend.entities;

import com.cooltoo.constants.SuggestionStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Entity
@Table(name = "nursego_user_suggestion")
public class SuggestionEntity {
    private long   id;
    private long   userId;
    private String suggestion;
    private Date   timeCreated;
    private SuggestionStatus status;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Column(name = "suggestion")
    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    @Column(name = "create_time")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(name = "status")
    @Enumerated
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
        msg.append(" status").append(status).append(", ");
        msg.append(" timeCreated=").append(timeCreated).append("] ");
        return msg.toString();
    }
}
