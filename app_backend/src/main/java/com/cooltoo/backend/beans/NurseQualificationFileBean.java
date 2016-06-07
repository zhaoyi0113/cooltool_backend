package com.cooltoo.backend.beans;

import com.cooltoo.beans.WorkFileTypeBean;

import java.util.Date;

/**
 * Created by hp on 2016/4/4.
 */
public class NurseQualificationFileBean {
    private long id;
    private long qualificationId;
    private int workfileTypeId;
    private WorkFileTypeBean workfileType;
    private long workfileId;
    private String workfileUrl;
    private Date timeCreated;
    private Date timeExpiry;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getQualificationId() {
        return this.qualificationId;
    }

    public void setQualificationId(long qualificationId) {
        this.qualificationId = qualificationId;
    }

    public int getWorkfileTypeId() {
        return workfileTypeId;
    }

    public void setWorkfileTypeId(int workfileTypeId) {
        this.workfileTypeId = workfileTypeId;
    }

    public WorkFileTypeBean getWorkfileType() {
        return this.workfileType;
    }

    public void setWorkfileType(WorkFileTypeBean workfileType) {
        this.workfileType = workfileType;
    }

    public long getWorkfileId() {
        return workfileId;
    }

    public void setWorkfileId(long workfileId) {
        this.workfileId = workfileId;
    }

    public String getWorkfileUrl() {
        return this.workfileUrl;
    }

    public void setWorkfileUrl(String workfileUrl) {
        this.workfileUrl = workfileUrl;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

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
        msg.append("workfileType=").append(workfileType).append(", ");
        msg.append("workfileId=").append(workfileId).append(", ");
        msg.append("timeCreated=").append(timeCreated).append(", ");
        msg.append("timeExpiry=").append(timeExpiry).append("]");
        return msg.toString();
    }
}
