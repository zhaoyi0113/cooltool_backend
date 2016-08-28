package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.util.VerifyUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/8/28.
 */
public class UserConsultationTalkBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long consultationId;
    private long nurseId;
    private NurseBean nurse;
    private ConsultationTalkStatus talkStatus;
    private String talkContent;
    private List<String> imagesUrl;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getConsultationId() {
        return consultationId;
    }

    public long getNurseId() {
        return nurseId;
    }

    public ConsultationTalkStatus getTalkStatus() {
        return talkStatus;
    }

    public String getTalkContent() {
        return talkContent;
    }

    public NurseBean getNurse() {
        return nurse;
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

    public void setNurse(NurseBean nurse) {
        this.nurse = nurse;
    }

    public List<String> getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(List<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", consultationId=").append(consultationId);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", talkStatus=").append(talkStatus);
        msg.append(", talkContent=").append(talkContent);
        msg.append(", imagesUrl=").append(VerifyUtil.isListEmpty(imagesUrl) ? 0 : imagesUrl.size());
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
