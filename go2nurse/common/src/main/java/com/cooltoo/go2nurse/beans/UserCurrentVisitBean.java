package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;

import java.util.Date;

/**
 * Created by zhaolisong on 2016/12/6.
 */
public class UserCurrentVisitBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private long diagnosticPointId;
    private DiagnosticEnumeration diagnostic;
    private DiagnosticEnumerationBean diagnosticBean;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    public CommonStatus getStatus() {
        return status;
    }
    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDiagnosticPoint() {
        return diagnosticPointId;
    }
    public void setDiagnosticPoint(long diagnosticPoint) {
        this.diagnosticPointId = diagnosticPoint;
        this.diagnostic = DiagnosticEnumeration.parseInt((int)diagnosticPointId);
        if (null!=diagnostic) {
            this.diagnosticBean = new DiagnosticEnumerationBean();
            this.diagnosticBean.setName(this.diagnostic.name());
            this.diagnosticBean.setId(this.diagnostic.ordinal());
        }
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", userId=").append(userId);
        msg.append(", diagnosticPoint=").append(diagnosticPointId);
        msg.append(", diagnostic=").append(diagnostic);
        msg.append(", diagnosticBean=").append(diagnosticBean);
        msg.append("]");
        return msg.toString();
    }
}
