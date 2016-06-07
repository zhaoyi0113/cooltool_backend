package com.cooltoo.backend.entities;

import com.cooltoo.constants.SuggestionStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/5/30.
 */
@Entity
@Table(name = "nursego_nurse_speak_complaint")
public class NurseSpeakComplaintEntity {

    private long id;
    private long informantId;
    private long speakId;
    private String reason;
    private Date time;
    private SuggestionStatus status;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "informant_id")
    public long getInformantId() {
        return informantId;
    }

    public void setInformantId(long informantId) {
        this.informantId = informantId;
    }

    @Column(name = "speak_id")
    public long getSpeakId() {
        return speakId;
    }

    public void setSpeakId(long speakId) {
        this.speakId = speakId;
    }

    @Column(name = "reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
    public SuggestionStatus getStatus() {
        return status;
    }

    public void setStatus(SuggestionStatus status) {
        this.status = status;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", informantId=").append(informantId);
        msg.append(", speakId=").append(speakId);
        msg.append(", reason=").append(reason);
        msg.append(", time=").append(time);
        msg.append(", status").append(status);
        msg.append("]");
        return msg.toString();
    }
}
