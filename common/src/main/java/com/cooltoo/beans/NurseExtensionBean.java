package com.cooltoo.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import java.util.Date;

/**
 * Created by hp on 2016/8/11.
 */
public class NurseExtensionBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long nurseId;
    private YesNoEnum answerNursingQuestion;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public long getNurseId() {
        return nurseId;
    }

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public YesNoEnum getAnswerNursingQuestion() {
        return answerNursingQuestion;
    }

    public void setAnswerNursingQuestion(YesNoEnum answerNursingQuestion) {
        this.answerNursingQuestion = answerNursingQuestion;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", answerNursingQuestion=").append(answerNursingQuestion);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
