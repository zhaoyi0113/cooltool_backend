package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/7/3.
 */
@Entity
@Table(name = "go2nurse_user_re_examination")
public class UserReExaminationDateEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private long hospitalizedGroupId;
    private Date reExaminationDate;
    private CommonStatus ignore;

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

    @Column(name = "hospitalized_group_id")
    public long getHospitalizedGroupId() {
        return hospitalizedGroupId;
    }

    @Column(name = "re_examination_date")
    public Date getReExaminationDate() {
        return reExaminationDate;
    }

    @Column(name = "ignore")
    @Enumerated
    public CommonStatus getIgnore() {
        return ignore;
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

    public void setHospitalizedGroupId(long hospitalizedGroupId) {
        this.hospitalizedGroupId = hospitalizedGroupId;
    }

    public void setReExaminationDate(Date reExaminationDate) {
        this.reExaminationDate = reExaminationDate;
    }

    public void setIgnore(CommonStatus ignore) {
        this.ignore = ignore;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", hospitalizedGroupId=").append(hospitalizedGroupId);
        msg.append(", reExaminationDate=").append(reExaminationDate);
        msg.append(", ignore=").append(ignore);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
