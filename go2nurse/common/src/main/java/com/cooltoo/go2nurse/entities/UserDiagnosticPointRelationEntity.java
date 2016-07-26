package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ProcessStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/14.
 */
@Entity
@Table(name = "go2nurse_user_diagnostic_point_relation")
public class UserDiagnosticPointRelationEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private long diagnosticId;
    private Date diagnosticTime;
    private long groupId;
    private ProcessStatus processStatus;

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

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    @Column(name = "diagnostic_point_id")
    public long getDiagnosticId() {
        return diagnosticId;
    }

    @Column(name = "diagnostic_point_time")
    public Date getDiagnosticTime() {
        return diagnosticTime;
    }

    @Column(name = "group_id")
    public long getGroupId() {
        return groupId;
    }

    @Column(name = "cancelled")
    @Enumerated
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
        msg.append(", processStatus=").append(processStatus);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
