package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/7/5.
 */
public class QuestionnaireCategoryBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private String instruction;
    private List<QuestionnaireBean> questionnaires;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getInstruction() {
        return instruction;
    }

    public List<QuestionnaireBean> getQuestionnaires() {
        return questionnaires;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void setQuestionnaires(List<QuestionnaireBean> questionnaires) {
        this.questionnaires = questionnaires;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", instruction=").append(instruction);
        msg.append(", questionnaire=").append(questionnaires);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
