package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/28.
 */
@Entity
@Table(name = "go2nurse_questionnaire")
public class QuestionnaireEntity {
    private long id;
    private long categoryId;
    private String title;
    private String description;
    private String conclusion;
    private int hospitalId;
    private Date time;
    private CommonStatus status;
    private YesNoEnum evaluateBeforeOrder;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "category_id")
    public long getCategoryId() {
        return categoryId;
    }

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Column(name = "conclusion")
    public String getConclusion() {
        return conclusion;
    }

    @Column(name = "hospital_id")
    public int getHospitalId() {
        return hospitalId;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    @Column(name = "status")
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "evaluate_before_order")
    public YesNoEnum getEvaluateBeforeOrder() {
        return evaluateBeforeOrder;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
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

    public void setEvaluateBeforeOrder(YesNoEnum evaluateBeforeOrder) {
        this.evaluateBeforeOrder = evaluateBeforeOrder;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", categoryId=").append(categoryId);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", title=").append(title);
        msg.append(", description=").append(description);
        msg.append(", conclusion=").append(conclusion);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append(", evaluateBeforeOrder=").append(evaluateBeforeOrder);
        msg.append("]");
        return msg.toString();
    }
}
