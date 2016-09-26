package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/8/28.
 */
@Entity
@Table(name = "go2nurse_user_consultation")
public class UserConsultationEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long categoryId;
    private String diseaseDescription;
    private String clinicalHistory;
    private long userId;
    private long patientId;
    private long nurseId;
    private YesNoEnum completed;
    private float score;

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
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "category_id")
    public long getCategoryId() {
        return categoryId;
    }

    @Column(name = "disease_description")
    public String getDiseaseDescription() {
        return diseaseDescription;
    }

    @Column(name = "clinical_history")
    public String getClinicalHistory() {
        return clinicalHistory;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    @Column(name = "patient_id")
    public long getPatientId() {
        return patientId;
    }

    @Column(name = "nurse_id")
    public long getNurseId() {
        return nurseId;
    }

    @Column(name = "completed")
    @Enumerated
    public YesNoEnum getCompleted() {
        return completed;
    }

    @Column(name = "score")
    public float getScore() {
        return score;
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

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setDiseaseDescription(String diseaseDescription) {
        this.diseaseDescription = diseaseDescription;
    }

    public void setClinicalHistory(String clinicalHistory) {
        this.clinicalHistory = clinicalHistory;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public void setCompleted(YesNoEnum completed) {
        this.completed = completed;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", categoryId=").append(categoryId);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", clinicalHistory=").append(clinicalHistory);
        msg.append(", diseaseDescription=").append(diseaseDescription);
        msg.append(", completed=").append(completed);
        msg.append(", score=").append(score);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
