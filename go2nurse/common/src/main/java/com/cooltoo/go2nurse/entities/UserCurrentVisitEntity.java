package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/12/6.
 */
@Entity
@Table(name = "go2nurse_user_current_visit")
public class UserCurrentVisitEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private long diagnosticPoint;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "status")
    public CommonStatus getStatus() {
        return status;
    }
    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Column(name = "diagnostic_point_id")
    public long getDiagnosticPoint() {
        return diagnosticPoint;
    }
    public void setDiagnosticPoint(long diagnosticPoint) {
        this.diagnosticPoint = diagnosticPoint;
    }


    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", userId=").append(userId);
        msg.append(", diagnosticPoint=").append(diagnosticPoint);
        msg.append("]");
        return msg.toString();
    }
}
