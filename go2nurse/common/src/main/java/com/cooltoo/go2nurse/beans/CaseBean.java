package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.util.VerifyUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/10/23.
 */
public class CaseBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long nurseId;
    private NurseBean nurse;
    private long casebookId;
    private String caseRecord;
    private List<String> imagesUrl;
    private Map<Long, String> imageIdToUrl;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getNurseId() {
        return nurseId;
    }

    public long getCasebookId() {
        return casebookId;
    }

    public String getCaseRecord() {
        return caseRecord;
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

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public void setCasebookId(long casebookId) {
        this.casebookId = casebookId;
    }
    public void setCaseRecord(String caseRecord) {
        this.caseRecord = caseRecord;
    }

    public NurseBean getNurse() {
        return nurse;
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

    public Map<Long, String> getImageIdToUrl() {
        return imageIdToUrl;
    }
    public void setImageIdToUrl(Map<Long, String> imageIdToUrl) {
        this.imageIdToUrl = imageIdToUrl;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", consultationId=").append(casebookId);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", talkContent=").append(caseRecord);
        msg.append(", imagesUrl=").append(VerifyUtil.isListEmpty(imagesUrl) ? 0 : imagesUrl.size());
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
