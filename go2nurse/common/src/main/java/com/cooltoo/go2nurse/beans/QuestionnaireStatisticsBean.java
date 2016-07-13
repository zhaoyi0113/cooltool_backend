package com.cooltoo.go2nurse.beans;

/**
 * Created by hp on 2016/7/11.
 */
public class QuestionnaireStatisticsBean {
    private long userId;
    private UserBean user;
    private long patientId;
    private PatientBean patientBean;
    private boolean isCompleted;
    private int questionNumber;
    private int answerNumber;
    private long score;
    private String conclusion;

    public long getUserId() {
        return userId;
    }

    public UserBean getUser() {
        return user;
    }

    public long getPatientId() {
        return patientId;
    }

    public PatientBean getPatientBean() {
        return patientBean;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public long getScore() {
        return score;
    }

    public String getConclusion() {
        return conclusion;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public int getAnswerNumber() {
        return answerNumber;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setPatientBean(PatientBean patientBean) {
        this.patientBean = patientBean;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public void setAnswerNumber(int answerNumber) {
        this.answerNumber = answerNumber;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", questionNumber=").append(questionNumber);
        msg.append(", answerNumber=").append(answerNumber);
        msg.append(", isCompleted=").append(isCompleted);
        msg.append(", score=").append(score);
        msg.append(", conclusion=").append(conclusion);
        msg.append("]");
        return msg.toString();
    }
}
