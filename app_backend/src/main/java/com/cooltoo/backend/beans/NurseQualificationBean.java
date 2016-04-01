package com.cooltoo.backend.beans;

import com.cooltoo.constants.VetStatus;
import com.cooltoo.constants.WorkFileType;

import java.util.Date;

/**
 * Created by zhaolisong on 16/3/23.
 */
public class NurseQualificationBean {

    private long id;
    private String name;
    private long userId;
    /** the id of workfile_type */
    private int workFileType;
    private WorkFileTypeBean workFileTypeBean;
    /* this id is in storage_file key id */
    private long workFileId;
    private String workFileURL;
    private VetStatus status;
    private String statusDescr;
    private Date timeCreated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getWorkFileType() {
        return workFileType;
    }

    public void setWorkFileType(int workFileType) {
        this.workFileType = workFileType;
    }

    public WorkFileTypeBean getWorkFileTypeBean() {
        return workFileTypeBean;
    }

    public void setWorkFileTypeBean(WorkFileTypeBean workFileTypeBean) {
        this.workFileTypeBean = workFileTypeBean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getWorkFileId() {
        return workFileId;
    }

    public void setWorkFileId(long workFileId) {
        this.workFileId = workFileId;
    }

    public String getWorkFileURL() {
        return workFileURL;
    }

    public void setWorkFileURL(String workFileURL) {
        this.workFileURL = workFileURL;
    }

    public VetStatus getStatus() {
        return status;
    }

    public void setStatus(VetStatus status) {
        this.status = status;
    }

    public String getStatusDescr() {
        return this.statusDescr;
    }

    public void setStatusDescr(String statusDescr) {
        this.statusDescr = statusDescr;
    }

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
        msg.append("workFileURL=").append(workFileURL).append(" , ");
        msg.append("status=").append(status).append(" , ");
        msg.append("timeCreated=").append(timeCreated);
        msg.append("]");
        return msg.toString();
    }
}
