package com.cooltoo.backend.beans;

import com.cooltoo.constants.VetStatus;
import com.cooltoo.constants.WorkFileType;

import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 16/3/23.
 */
public class NurseQualificationBean {

    private long id;
    private String name;
    private long userId;
    private VetStatus status;
    private String statusDescr;
    private List<NurseQualificationFileBean> workfiles;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<NurseQualificationFileBean> getWorkfiles() {
        return this.workfiles;
    }

    public void setWorkfiles(List<NurseQualificationFileBean> workfiles) {
        this.workfiles = workfiles;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("name=").append(name).append(" , ");
        msg.append("userId=").append(userId).append(" , ");
        msg.append("status=").append(status).append(" , ");
        msg.append("statusDescr=").append(statusDescr).append(" , ");
        msg.append("workfiles=").append(workfiles).append(" , ");
        msg.append("]");
        return msg.toString();
    }
}
