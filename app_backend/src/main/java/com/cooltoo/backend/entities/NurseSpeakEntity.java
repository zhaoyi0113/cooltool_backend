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
    private long imageId;
    /** the id of speak_type */
    private int speakType;


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

    @Column(name = "image_id")
    public long getImageId() { return imageId; }

    public void setImageId(long imageId) { this.imageId = imageId; }

    @Column(name = "speak_type")
    public int getSpeakType() { return speakType; }

    public void setSpeakType(int speakType) { this.speakType = speakType; }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("userId=").append(userId).append(" , ");
        msg.append("time=").append(time).append(" , ");
        msg.append("content=").append(content).append(" , ");
        msg.append("imageId=").append(imageId).append(" , ");
        msg.append("speakType=").append(speakType);
        msg.append("]");
        return msg.toString();
    }
}
