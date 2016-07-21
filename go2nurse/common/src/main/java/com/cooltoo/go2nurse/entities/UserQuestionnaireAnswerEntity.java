package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.YesNoEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/28.
 */
@Entity
@Table(name = "go2nurse_user_questionnaire_answer")
public class UserQuestionnaireAnswerEntity {

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
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "group_id")
    public long getGroupId() {
        return groupId;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    @Column(name = "patient_id")
    public long getPatientId() {
        return patientId;
    }

    @Column(name = "patient_name")
    public String getPatientName() {
        return patientName;
    }

    @Column(name = "patient_gender")
    public GenderType getPatientGender() {
        return patientGender;
    }

    @Column(name = "patient_age")
    public int getPatientAge() {
        return patientAge;
    }

    @Column(name = "patient_mobile")
    public String getPatientMobile() {
        return patientMobile;
    }

    @Column(name = "questionnaire_id")
    public long getQuestionnaireId() {
        return questionnaireId;
    }

    @Column(name = "questionnaire_name")
    public String getQuestionnaireName() {
        return questionnaireName;
    }

    @Column(name = "questionnaire_conclusion")
    public String getQuestionnaireConclusion() {
        return questionnaireConclusion;
    }

    @Column(name = "question_id")
    public long getQuestionId() {
        return questionId;
    }

    @Column(name = "question_content")
    public String getQuestionContent() {
        return questionContent;
    }

    @Column(name = "answer")
    public String getAnswer() {
        return answer;
    }

    @Column(name = "answer_completed")
    @Enumerated
    public YesNoEnum getAnswerCompleted() {
        return answerCompleted;
    }

    @Column(name = "hospital_id")
    public long getHospitalId() {
        return hospitalId;
    }

    @Column(name = "hospital_name")
    public String getHospitalName() {
        return hospitalName;
    }

    @Column(name = "department_id")
    public long getDepartmentId() {
        return departmentId;
    }

    @Column(name = "department_name")
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
