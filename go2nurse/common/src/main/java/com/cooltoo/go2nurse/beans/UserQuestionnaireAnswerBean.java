package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.YesNoEnum;

import java.util.Date;

/**
 * Created by hp on 2016/6/28.
 */
public class UserQuestionnaireAnswerBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long groupId;
    private long userId;
    private long patientId;
    private String patientName;
    private GenderType patientGender;
    private int patientAge;
    private String patientMobile;
    private long questionnaireId;
    private String questionnaireName;
    private String questionnaireConclusion;
    private long questionId;
    private String questionContent;
    private String answer;
    private YesNoEnum answerCompleted;
    private long hospitalId;
    private String hospitalName;
    private long departmentId;
    private String departmentName;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
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

    private String getPatientName() {
        return patientName;
    }

    public GenderType getPatientGender() {
        return patientGender;
    }

    private int getPatientAge() {
        return patientAge;
    }

    private String getPatientMobile() {
        return patientMobile;
    }

    public long getQuestionnaireId() {
        return questionnaireId;
    }

    public String getQuestionnaireName() {
        return questionnaireName;
    }

    public String getQuestionnaireConclusion() {
        return questionnaireConclusion;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public String getAnswer() {
        return answer;
    }

    public YesNoEnum getAnswerCompleted() {
        return answerCompleted;
    }

    public long getHospitalId() {
        return hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
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

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setPatientGender(GenderType patientGender) {
        this.patientGender = patientGender;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public void setPatientMobile(String patientMobile) {
        this.patientMobile = patientMobile;
    }

    public void setQuestionnaireId(long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public void setQuestionnaireName(String questionnaireName) {
        this.questionnaireName = questionnaireName;
    }

    public void setQuestionnaireConclusion(String questionnaireConclusion) {
        this.questionnaireConclusion = questionnaireConclusion;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnswerCompleted(YesNoEnum answerCompleted) {
        this.answerCompleted = answerCompleted;
    }

    public void setHospitalId(long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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
        msg.append(", departmentId=").append(departmentId);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", answer=").append(answer);
        msg.append(", answerCompleted=").append(answerCompleted);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
