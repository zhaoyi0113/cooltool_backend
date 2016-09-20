package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.util.VerifyUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/8/28.
 */
public class UserConsultationBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long categoryId;
    private ConsultationCategoryBean category;
    private String diseaseDescription;
    private String clinicalHistory;
    private long userId;
    private UserBean user;
    private long patientId;
    private PatientBean patient;
    private long nurseId;
    private NurseBean nurse;
    private List<String> imagesUrl;
    private List<UserConsultationTalkBean> talks;
    private YesNoEnum completed;
    private boolean hasUnreadTalk;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getDiseaseDescription() {
        return diseaseDescription;
    }

    public String getClinicalHistory() {
        return clinicalHistory;
    }

    public long getUserId() {
        return userId;
    }

    public long getPatientId() {
        return patientId;
    }

    public ConsultationCategoryBean getCategory() {
        return category;
    }

    public UserBean getUser() {
        return user;
    }

    public PatientBean getPatient() {
        return patient;
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

    public void setCategory(ConsultationCategoryBean category) {
        this.category = category;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public List<String> getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(List<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public List<UserConsultationTalkBean> getTalks() {
        return talks;
    }

    public void setTalks(List<UserConsultationTalkBean> talks) {
        this.talks = talks;
    }

    public long getNurseId() {
        return nurseId;
    }

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public NurseBean getNurse() {
        return nurse;
    }

    public void setNurse(NurseBean nurse) {
        this.nurse = nurse;
    }

    public YesNoEnum getCompleted() {
        return completed;
    }

    public void setCompleted(YesNoEnum completed) {
        this.completed = completed;
    }

    public boolean isHasUnreadTalk() {
        return hasUnreadTalk;
    }

    public void setHasUnreadTalk(boolean hasUnreadTalk) {
        this.hasUnreadTalk = hasUnreadTalk;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", categoryId=").append(categoryId);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", completed=").append(completed);
        msg.append(", hasUnreadTalk=").append(hasUnreadTalk);
        msg.append(", clinicalHistory=").append(clinicalHistory);
        msg.append(", diseaseDescription=").append(diseaseDescription);
        msg.append(", imagesUrl=").append(VerifyUtil.isListEmpty(imagesUrl) ? 0 : imagesUrl.size());
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
