package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.util.VerifyUtil;

import java.util.*;

/**
 * Created by hp on 2016/6/28.
 */
public class QuestionnaireBean {
    public static final String FOLLOW_UP_RECORD = "follow_up";

    private String from = "patient";

    private long id;
    private long categoryId;
    private String title;
    private String description;
    private String conclusion;
    private int hospitalId;
    private HospitalBean hospital;
    private List<QuestionBean> questions;
    private Date time;
    private CommonStatus status;
    private Map<String, Object> properties = new HashMap<>();

    //=================================
    //    user questionnaire score
    //=================================
    private long groupId;
    private PatientBean patient;
    private int userScore;
    private QuestionnaireConclusionBean userConclusion;

    //=================================
    //  questionnaire statistics data
    //=================================
    private QuestionnaireStatisticsBean statistics;

    public long getId() {
        return id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getConclusion() {
        return conclusion;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public HospitalBean getHospital() {
        return hospital;
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

    public void setHospital(HospitalBean hospital) {
        this.hospital = hospital;
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

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public PatientBean getPatient() {
        return patient;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public QuestionnaireConclusionBean getUserConclusion() {
        return userConclusion;
    }

    public void setUserConclusion(QuestionnaireConclusionBean userConclusion) {
        this.userConclusion = userConclusion;
    }

    public QuestionnaireStatisticsBean getStatistics() {
        return statistics;
    }

    public void setStatistics(QuestionnaireStatisticsBean statistics) {
        this.statistics = statistics;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(String key, Object value) {
        this.properties.put(key, value);
    }

    public String getFrom() {
        return from;
    }

    public QuestionnaireBean clone() {
        QuestionnaireBean bean = new QuestionnaireBean();
        bean.setId(id);
        bean.setCategoryId(categoryId);
        bean.setTitle(title);
        bean.setDescription(description);
        bean.setConclusion(conclusion);
        bean.setHospitalId(hospitalId);
        bean.setHospital(hospital);
        bean.setTime(time);
        bean.setStatus(status);

        List<QuestionBean> questions = new ArrayList<>();
        bean.setQuestions(questions);
        if (!VerifyUtil.isListEmpty(this.questions)) {
            for (QuestionBean question : this.questions) {
                questions.add(question.clone());
            }
        }

        bean.setGroupId(groupId);
        bean.setPatient(patient);
        bean.setUserScore(userScore);
        bean.setUserConclusion(userConclusion);

        bean.setStatistics(statistics);
        return bean;
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
        msg.append(", questions count=").append(null==questions ? 0 : questions.size());
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append(", groupId=").append(groupId);
        msg.append(", patient=").append(patient);
        msg.append(", userScore=").append(userScore);
        msg.append(", userConclusion=").append(userConclusion);
        msg.append("]");
        return msg.toString();
    }
}
