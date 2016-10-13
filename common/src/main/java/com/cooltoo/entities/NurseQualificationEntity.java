package com.cooltoo.entities;

import com.cooltoo.constants.VetStatus;
import com.cooltoo.constants.WorkFileType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 16/3/23.
 */
@Entity
@Table(name = "nursego_nurse_qualification")
public class NurseQualificationEntity {
    private long      id;
    private String    name;
    private long      userId;
    private VetStatus status;
    private String    statusDesc;
    private Date      timeCreated;
    private Date      timeProcessed;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Column(name = "status")
    @Enumerated
    public VetStatus getStatus() {
        return status;
    }

    public void setStatus(VetStatus status) {
        this.status = status;
    }

    @Column(name = "status_description")
    public String getStatusDesc() {
        return this.statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    @Column(name = "create_time")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(name = "process_time")
    public Date getTimeProcessed() {
        return timeProcessed;
    }

    public void setTimeProcessed(Date timeProcessed) {
        this.timeProcessed = timeProcessed;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("name=").append(name).append(" , ");
        msg.append("userId=").append(userId).append(" , ");
        msg.append("status=").append(status).append(" , ");
        msg.append("statusDesc=").append(statusDesc).append("");
        msg.append("]");
        return msg.toString();
    }
}
