package com.cooltoo.backend.beans;

import com.cooltoo.constants.SuggestionStatus;
import java.util.Date;

/**
 * Created by hp on 2016/5/30.
 */
public class NurseSpeakComplaintBean {

    private long id;
    private long informantId;
    private NurseBean informant;
    private long speakId;
    private NurseSpeakBean speakBean;
    private String reason;
    private Date time;
    private SuggestionStatus status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInformantId() {
        return informantId;
    }

    public void setInformantId(long informantId) {
        this.informantId = informantId;
    }

    public long getSpeakId() {
        return speakId;
    }

    public void setSpeakId(long speakId) {
        this.speakId = speakId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public SuggestionStatus getStatus() {
        return status;
    }

    public void setStatus(SuggestionStatus status) {
        this.status = status;
    }

    public NurseBean getInformant() {
        return informant;
    }

    public void setInformant(NurseBean informant) {
        this.informant = informant;
    }

    public NurseSpeakBean getSpeakBean() {
        return speakBean;
    }

    public void setSpeakBean(NurseSpeakBean speakBean) {
        this.speakBean = speakBean;
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
        msg.append(", speakBean=").append(speakBean);
        msg.append(", informant=").append(informant);
        msg.append("]");
        return msg.toString();
    }
}

