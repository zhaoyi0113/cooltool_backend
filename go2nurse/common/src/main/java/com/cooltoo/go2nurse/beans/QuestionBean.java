package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.QuestionType;

import java.util.Date;

/**
 * Created by hp on 2016/6/28.
 */
public class QuestionBean {

    private long id;
    private long questionnaireId;
    private String content;
    private String options;
    private QuestionType type;
    private int grade;
    private long imageId;
    private String imageUrl="";
    private Date time;
    private CommonStatus status;

    //===================================
    //         user answer
    //===================================
    private String userAnswer;

    public long getId() {
        return id;
    }

    public long getQuestionnaireId() {
        return questionnaireId;
    }

    public String getContent() {
        return content;
    }

    public String getOptions() {
        return options;
    }

    public QuestionType getType() {
        return type;
    }

    public int getGrade() {
        return grade;
    }

    public long getImageId() {
        return imageId;
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

    public void setQuestionnaireId(long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public QuestionBean clone() {
        QuestionBean bean = new QuestionBean();
        bean.setId(id);
        bean.setQuestionnaireId(questionnaireId);
        bean.setContent(content);
        bean.setOptions(options);
        bean.setType(type);
        bean.setGrade(grade);
        bean.setTime(time);
        bean.setStatus(status);
        bean.setUserAnswer(userAnswer);
        bean.setImageId(imageId);
        bean.setImageUrl(imageUrl);
        return bean;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", questionnaireId=").append(questionnaireId);
        msg.append(", content=").append(content);
        msg.append(", options=").append(options);
        msg.append(", type=").append(type);
        msg.append(", grade=").append(grade);
        msg.append(", userAnswer={}").append(userAnswer);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
