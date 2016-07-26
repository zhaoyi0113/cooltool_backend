package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.constants.ProcessStatus;

import java.util.Date;

/**
 * Created by hp on 2016/6/14.
 */
public class UserDiagnosticPointRelationBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private long diagnosticId;
    private DiagnosticEnumeration diagnostic;
    private DiagnosticEnumerationBean diagnosticBean;
    private Date diagnosticTime;
    private long groupId;
    private ProcessStatus processStatus;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getUserId() {
        return userId;
    }

    public long getDiagnosticId() {
        return diagnosticId;
    }

    public DiagnosticEnumeration getDiagnostic() {
        return diagnostic;
    }

    public Date getDiagnosticTime() {
        return diagnosticTime;
    }

    public long getGroupId() {
        return groupId;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
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

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setDiagnosticId(long diagnosticId) {
        this.diagnosticId = diagnosticId;
        this.diagnostic = DiagnosticEnumeration.parseInt((int)diagnosticId);
        if (null!=diagnostic) {
            this.diagnosticBean = new DiagnosticEnumerationBean();
            this.diagnosticBean.setName(this.diagnostic.name());
            this.diagnosticBean.setId(this.diagnostic.ordinal());
        }
    }

    public void setDiagnosticTime(Date diagnosticTime) {
        this.diagnosticTime = diagnosticTime;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", diagnosticId=").append(diagnosticId);
        msg.append(", diagnosticTime=").append(diagnosticTime);
        msg.append(", groupId=").append(groupId);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
