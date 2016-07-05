package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by hp on 2016/6/28.
 */
public class UserQuestionnaireAnswerBean {

    private long id;
    private long groupId;
    private long userId;
    private long patientId;
    private PatientBean patient;
    private long questionnaireId;
    private long questionId;
    private String answer;
    private Date time;
    private CommonStatus status;

    public long getId() {
        return id;
    }

    public long getGroupId() {
        return groupId;
    }

    public long getUserId() {
        return userId;
    }

    public long getPatientId() {
        return patientId;
    }

    public long getQuestionnaireId() {
        return questionnaireId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setQuestionnaireId(long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public PatientBean getPatient() {
        return patient;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", groupId=").append(groupId);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", questionnaireId=").append(questionnaireId);
        msg.append(", questionId=").append(questionId);
        msg.append(", answer=").append(answer);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
