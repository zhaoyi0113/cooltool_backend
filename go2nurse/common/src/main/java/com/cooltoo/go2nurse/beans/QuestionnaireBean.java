package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/6/28.
 */
public class QuestionnaireBean {
    private long id;
    private String title;
    private String description;
    private int hospitalId;
    private List<QuestionBean> questions;
    private Date time;
    private CommonStatus status;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getHospitalId() {
        return hospitalId;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public List<QuestionBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionBean> questions) {
        this.questions = questions;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", title=").append(title);
        msg.append(", description=").append(description);
        msg.append(", questions count=").append(null==questions ? 0 : questions.size());
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
