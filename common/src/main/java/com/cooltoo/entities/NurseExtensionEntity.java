package com.cooltoo.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/8/11.
 */
@Entity
@Table(name = "cooltoo_nurse_info_extension")
public class NurseExtensionEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long nurseId;
    private NurseEntity nurse;
    private YesNoEnum answerNursingQuestion;
    private String goodAt;
    private String jobTitle;
    private YesNoEnum isExpert;
    private YesNoEnum seeAllOrder;

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

    @Column(name = "nurse_id")
    public long getNurseId() {
        return nurseId;
    }

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    @Column(name = "answer_nursing_questions")
    @Enumerated
    public YesNoEnum getAnswerNursingQuestion() {
        return answerNursingQuestion;
    }

    public void setAnswerNursingQuestion(YesNoEnum answerNursingQuestion) {
        this.answerNursingQuestion = answerNursingQuestion;
    }

    @Column(name = "good_at")
    public String getGoodAt() {
        return goodAt;
    }

    public void setGoodAt(String goodAt) {
        this.goodAt = goodAt;
    }

    @Column(name = "job_title")
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @Column(name = "is_expert")
    @Enumerated
    public YesNoEnum getIsExpert() {
        return isExpert;
    }

    public void setIsExpert(YesNoEnum isExpert) {
        this.isExpert = isExpert;
    }

    @Column(name = "see_all_order")
    @Enumerated
    public YesNoEnum getSeeAllOrder() {
        return seeAllOrder;
    }

    public void setSeeAllOrder(YesNoEnum seeAllOrder) {
        this.seeAllOrder = seeAllOrder;
    }

    // 映射多对一的关联关系
    @JoinColumn(name="nurse_id", insertable=false, updatable=false)// cooltoo_nurse_info_extension 关联 cooltoo_nurse 表的字段
    @ManyToOne()
    public NurseEntity getNurse() {
        return nurse;
    }

    public void setNurse(NurseEntity nurse) {
        this.nurse = nurse;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", goodAt=").append(goodAt);
        msg.append(", jobTitle=").append(jobTitle);
        msg.append(", answerNursingQuestion=").append(answerNursingQuestion);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
