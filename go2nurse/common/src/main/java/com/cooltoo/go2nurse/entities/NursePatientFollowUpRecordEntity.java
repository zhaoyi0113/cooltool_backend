package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.PatientFollowUpType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/11/8.
 */
@Entity
@Table(name = "go2nurse_nurse_patient_follow_up_record")
public class NursePatientFollowUpRecordEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long followUpId; /* 护士随访对象记录 */
    private PatientFollowUpType followUpType; /* 护士随访类型: 指定病人回答问卷 / 向病人发问 */
    private long relativeConsultationId; /* 护士向病人发问 */
    private long relativeQuestionnaireId; /* 护士指定病人回答问卷 */
    private long relativeQuestionnaireAnswerGroupId; /* 病人回答问卷结果 */
    private YesNoEnum patientReplied; /* 病人是否回答了 */
    private YesNoEnum nurseRead; /* 护士是否已读 */
    private Date timeUpdated; /* 随访记录更新时间 */

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
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "follow_up_id")
    public long getFollowUpId() {
        return followUpId;
    }

    public void setFollowUpId(long followUpId) {
        this.followUpId = followUpId;
    }

    @Column(name = "follow_up_type")
    @Enumerated
    public PatientFollowUpType getFollowUpType() {
        return followUpType;
    }

    public void setFollowUpType(PatientFollowUpType followUpType) {
        this.followUpType = followUpType;
    }

    @Column(name = "relative_consultation_id")
    public long getRelativeConsultationId() {
        return relativeConsultationId;
    }

    public void setRelativeConsultationId(long relativeConsultationId) {
        this.relativeConsultationId = relativeConsultationId;
    }

    @Column(name = "relative_questionnaire_id")
    public long getRelativeQuestionnaireId() {
        return relativeQuestionnaireId;
    }

    public void setRelativeQuestionnaireId(long relativeQuestionnaireId) {
        this.relativeQuestionnaireId = relativeQuestionnaireId;
    }

    @Column(name = "relative_questionnaire_answer_group_id")
    public long getRelativeQuestionnaireAnswerGroupId() {
        return relativeQuestionnaireAnswerGroupId;
    }

    public void setRelativeQuestionnaireAnswerGroupId(long relativeQuestionnaireAnswerGroupId) {
        this.relativeQuestionnaireAnswerGroupId = relativeQuestionnaireAnswerGroupId;
    }

    @Column(name = "patient_replied")
    @Enumerated
    public YesNoEnum getPatientReplied() {
        return patientReplied;
    }

    public void setPatientReplied(YesNoEnum patientReplied) {
        this.patientReplied = patientReplied;
    }

    @Column(name = "nurse_read")
    @Enumerated
    public YesNoEnum getNurseRead() {
        return nurseRead;
    }

    public void setNurseRead(YesNoEnum nurseRead) {
        this.nurseRead = nurseRead;
    }

    @Column(name = "time_updated")
    public Date getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", timeUpdated=").append(timeUpdated);
        msg.append(", followUpId=").append(followUpId);
        msg.append(", followUpType=").append(followUpType);
        msg.append(", patientReplied=").append(patientReplied);
        msg.append(", nurseRead=").append(nurseRead);
        msg.append(", relativeConsultationId=").append(relativeConsultationId);
        msg.append(", relativeQuestionnaireId=").append(relativeQuestionnaireId);
        msg.append(", relativeQuestionnaireAnswerGroupId=").append(relativeQuestionnaireAnswerGroupId);
        msg.append("]");
        return msg.toString();
    }
}
