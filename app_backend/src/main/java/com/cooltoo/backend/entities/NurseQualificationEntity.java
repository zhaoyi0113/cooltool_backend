package com.cooltoo.backend.entities;

import com.cooltoo.constants.VetStatus;
import com.cooltoo.constants.WorkFileType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 16/3/23.
 */
@Entity
@Table(name = "nurse_qualification")
public class NurseQualificationEntity {
    private long id;
    private String name;
    private long userId;
    private WorkFileType workFileType;
    /* this id is in storage_file key id */
    private long workFileId;
    private VetStatus status;
    private Date timeCreated;

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

    @Column(name = "work_file_type")
    @Enumerated
    public WorkFileType getWorkFileType() {
        return workFileType;
    }

    public void setWorkFileType(WorkFileType workFileType) {
        this.workFileType = workFileType;
    }

    @Column(name = "work_file_id")
    public long getWorkFileId() {
        return workFileId;
    }

    public void setWorkFileId(long workFileId) {
        this.workFileId = workFileId;
    }

    @Column(name = "status")
    @Enumerated
    public VetStatus getStatus() {
        return status;
    }

    public void setStatus(VetStatus status) {
        this.status = status;
    }

    @Column(name = "create_time")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("name=").append(name).append(" , ");
        msg.append("userId=").append(userId).append(" , ");
        msg.append("workFileType=").append(workFileType).append(" , ");
        msg.append("workFileId=").append(workFileId).append(" , ");
        msg.append("status=").append(status).append(" , ");
        msg.append("timeCreated=").append(timeCreated);
        msg.append("]");
        return msg.toString();
    }
}
