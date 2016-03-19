package com.cooltoo.backend.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/3/18.
 */
@Entity
@Table(name = "nurse_speak_thumbs_up")
public class NurseSpeakThumbsUpEntity {
    private long id;
    private long nurseSpeakId;
    private long thumbsUpUserId;
    private Date time;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "nurse_speak_id")
    public long getNurseSpeakId() {
        return nurseSpeakId;
    }

    public void setNurseSpeakId(long nurseSpeakId) {
        this.nurseSpeakId = nurseSpeakId;
    }

    @Column(name = "thumbs_up_user_id")
    public long getThumbsUpUserId() {
        return thumbsUpUserId;
    }

    public void setThumbsUpUserId(long thumbsUpUserId) {
        this.thumbsUpUserId = thumbsUpUserId;
    }

    @Column(name = "time")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append("nurseSpeakId=").append(nurseSpeakId).append(" ,");
        msg.append("thumbsUpUserId=").append(thumbsUpUserId).append(" ,");
        msg.append("time=").append(time);
        msg.append(" ]");
        return msg.toString();
    }
}
