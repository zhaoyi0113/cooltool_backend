package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/8/28.
 */
@Entity
@Table(name = "go2nurse_user_consultation_talk")
public class UserConsultationTalkEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long consultationId;
    private long nurseId;
    private ConsultationTalkStatus talkStatus;
    private String talkContent;
    private YesNoEnum isBest;

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

    @Column(name = "best")
    @Enumerated
    public YesNoEnum getIsBest() {
        return isBest;
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

    public void setIsBest(YesNoEnum isBest) {
        this.isBest = isBest;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", consultationId=").append(consultationId);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", talkStatus=").append(talkStatus);
        msg.append(", talkContent=").append(talkContent);
        msg.append(", isBest=").append(isBest);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
