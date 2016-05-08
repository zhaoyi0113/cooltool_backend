package com.cooltoo.backend.entities;

import javax.persistence.*;
import java.util.Date;
import com.cooltoo.constants.SpeakType;

/**
 * Created by yzzhao on 3/15/16.
 */
@Entity
@Table(name = "nurse_speak")
public class NurseSpeakEntity {
    private long id;
    private long userId;
    private String content;
    private Date time;
    /** the id of speak_type */
    private int speakType;
    private String anonymousName;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
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

    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "time")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) { this.time = time; }

    @Column(name = "speak_type")
    public int getSpeakType() { return speakType; }

    public void setSpeakType(int speakType) { this.speakType = speakType; }

    @Column(name = "anonymous_name")
    public String getAnonymousName() {
        return anonymousName;
    }

    public void setAnonymousName(String anonymousName) {
        this.anonymousName = anonymousName;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", time=").append(time);
        msg.append(", content=").append(content);
        msg.append(", speakType=").append(speakType);
        msg.append(", anonymousName=").append(anonymousName);
        msg.append("]");
        return msg.toString();
    }
}
