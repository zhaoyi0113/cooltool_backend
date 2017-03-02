package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 01/03/2017.
 */
@Entity
@Table(name = "go2nurse_patient_symptoms")
public class PatientSymptomsEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long orderId;
    private long userId;
    private long patientId;
    private String symptoms;
    private String symptomsDescription;
    private String symptomsImages;
    private String questionnaire;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }
    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "order_id")
    public long getOrderId() {
        return orderId;
    }
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Column(name = "patient_id")
    public long getPatientId() {
        return patientId;
    }
    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    @Column(name = "symptoms")
    public String getSymptoms() {
        return symptoms;
    }
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    @Column(name = "symptoms_description")
    public String getSymptomsDescription() {
        return symptomsDescription;
    }
    public void setSymptomsDescription(String symptomsDescription) {
        this.symptomsDescription = symptomsDescription;
    }

    @Column(name = "symptoms_images")
    public String getSymptomsImages() {
        return symptomsImages;
    }
    public void setSymptomsImages(String symptomsImages) {
        this.symptomsImages = symptomsImages;
    }

    @Column(name = "questionnaire")
    public String getQuestionnaire() {
        return questionnaire;
    }
    public void setQuestionnaire(String questionnaire) {
        this.questionnaire = questionnaire;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", orderId=").append(orderId);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", symptoms=").append(symptoms);
        msg.append(", symptomsDescription=").append(symptomsDescription);
        msg.append(", symptomsImages=").append(symptomsImages);
        msg.append(", questionnaire=").append(questionnaire);
        msg.append("]");
        return msg.toString();
    }
}
