package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by hp on 2016/7/3.
 */
public class UserReExaminationBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private long hospitalizedGroupId;
    private Date reExaminationDate;

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

    public long getHospitalizedGroupId() {
        return hospitalizedGroupId;
    }

    public Date getReExaminationDate() {
        return reExaminationDate;
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

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", hospitalizedGroupId=").append(hospitalizedGroupId);
        msg.append(", reExaminationDate=").append(reExaminationDate);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
