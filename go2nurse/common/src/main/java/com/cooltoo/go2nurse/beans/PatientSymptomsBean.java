package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 01/03/2017.
 */
public class PatientSymptomsBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long orderId;
    private long userId;
    private long patientId;
    private String symptoms;
    private String symptomsDescription;
    private String symptomsImages;
    private List<String> arraySymptomsImages;
    private String questionnaire;

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

    public long getOrderId() {
        return orderId;
    }
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPatientId() {
        return patientId;
    }
    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public String getSymptoms() {
        return symptoms;
    }
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getSymptomsDescription() {
        return symptomsDescription;
    }
    public void setSymptomsDescription(String symptomsDescription) {
        this.symptomsDescription = symptomsDescription;
    }

    public String getSymptomsImages() {
        return symptomsImages;
    }
    public void setSymptomsImages(String symptomsImages) {
        this.symptomsImages = symptomsImages;
    }

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
        msg.append(", questionnaire=").append(questionnaire);
        msg.append("]");
        return msg.toString();
    }
}
