package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.QuestionType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/28.
 */
@Entity
@Table(name = "go2nurse_question")
public class QuestionEntity {

    private long id;
    private long questionnaireId;
    private String content;
    private String options;
    private QuestionType type;
    private Date time;
    private CommonStatus status;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "questionnaire_id")
    public long getQuestionnaireId() {
        return questionnaireId;
    }

    @Column(name = "content")
    public String getContent() {
        return content;
    }

    @Column(name = "options")
    public String getOptions() {
        return options;
    }

    @Column(name = "type")
    @Enumerated
    public QuestionType getType() {
        return type;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    @Column(name = "status")
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

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", questionnaireId=").append(questionnaireId);
        msg.append(", content=").append(content);
        msg.append(", options=").append(options);
        msg.append(", type=").append(type);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
