package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import org.aspectj.lang.annotation.control.CodeGenerationHint;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/7/5.
 */
@Entity
@Table(name = "go2nurse_questionnaire_category")
public class QuestionnaireCategoryEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private String instruction;

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
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "instruction")
    public String getInstruction() {
        return instruction;
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

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", instruction=").append(instruction);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
