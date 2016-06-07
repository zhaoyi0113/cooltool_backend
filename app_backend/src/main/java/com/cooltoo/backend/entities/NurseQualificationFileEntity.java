package com.cooltoo.backend.entities;

import com.cooltoo.constants.WorkFileType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/4/4.
 */
@Entity
@Table(name = "nursego_nurse_qualification_file")
public class NurseQualificationFileEntity {
    private long id;
    private long qualificationId;
    /** the id of workfile_type  */
    private int workfileTypeId;
    private long workfileId;
    private Date timeCreated;
    private Date timeExpiry;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "qualification_id")
    public long getQualificationId() {
        return this.qualificationId;
    }

    public void setQualificationId(long qualificationId) {
        this.qualificationId = qualificationId;
    }

    @Column(name = "work_file_type_id")
    public int getWorkfileTypeId() {
        return workfileTypeId;
    }

    public void setWorkfileTypeId(int workfileTypeId) {
        this.workfileTypeId = workfileTypeId;
    }

    @Column(name = "work_file_id")
    public long getWorkfileId() {
        return workfileId;
    }

    public void setWorkfileId(long workfileId) {
        this.workfileId = workfileId;
    }

    @Column(name = "create_time")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(name = "expiry_time")
    public Date getTimeExpiry() {
        return timeExpiry;
    }

    public void setTimeExpiry(Date timeExpiry) {
        this.timeExpiry = timeExpiry;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("qualificationId=").append(qualificationId).append(", ");
        msg.append("workfileTypeId=").append(workfileTypeId).append(", ");
        msg.append("workfileId=").append(workfileId).append(", ");
        msg.append("timeCreated=").append(timeCreated).append(", ");
        msg.append("timeExpiry=").append(timeExpiry).append("]");
        return msg.toString();
    }
}
