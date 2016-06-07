package com.cooltoo.backend.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/2.
 */
@Entity
@Table(name = "nursego_nurse_speak_topic_relation")
public class NurseSpeakTopicRelationEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long topicId;
    private long speakId;
    private long userId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "topic_id")
    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    @Column(name = "speak_id")
    public long getSpeakId() {
        return speakId;
    }

    public void setSpeakId(long speakId) {
        this.speakId = speakId;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", topicId=").append(topicId);
        msg.append(", speakId=").append(speakId);
        msg.append(", userId=").append(userId);
        msg.append("]");
        return msg.toString();
    }
}
