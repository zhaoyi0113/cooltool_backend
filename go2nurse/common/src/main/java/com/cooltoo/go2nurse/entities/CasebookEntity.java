package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/10/23.
 */
@Entity
@Table(name = "go2nurse_casebook")
public class CasebookEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private long patientId;
    private long nurseId;
    private String name;
    private String description;

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

    @Column(name = "case_name")
    public String getName() {
        return name;
    }

    @Column(name = "case_description")
    public String getDescription() {
        return description;
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

    public void setDescription(String description) {
        this.description = description;
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

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", name=").append(name);
        msg.append(", description=").append(description);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
