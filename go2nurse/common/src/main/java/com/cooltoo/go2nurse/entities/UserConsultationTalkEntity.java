package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by hp on 2016/8/28.
 */
public class UserConsultationTalkEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long consultationId;
    private long nurseId;
    private ConsultationTalkStatus talkStatus;
    private String talkContent;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "user_consultation_id")
    public long getConsultationId() {
        return consultationId;
    }

    @Column(name = "nurse_id")
    public long getNurseId() {
        return nurseId;
    }

    @Column(name = "talk_status")
    public ConsultationTalkStatus getTalkStatus() {
        return talkStatus;
    }

    @Column(name = "talk_content")
    public String getTalkContent() {
        return talkContent;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public void setConsultationId(long consultationId) {
        this.consultationId = consultationId;
    }

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public void setTalkStatus(ConsultationTalkStatus talkStatus) {
        this.talkStatus = talkStatus;
    }

    public void setTalkContent(String talkContent) {
        this.talkContent = talkContent;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("]");
        return msg.toString();
    }
}
