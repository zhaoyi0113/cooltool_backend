package com.cooltoo.nurse360.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;

import java.util.Date;

/**
 * Created by hp on 2016/10/11.
 */
public class NurseNotificationRelationBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long nurseId;
    private long notificationId;
    private ReadingStatus readingStatus;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getNurseId() {
        return nurseId;
    }

    public long getNotificationId() {
        return notificationId;
    }

    public ReadingStatus getReadingStatus() {
        return readingStatus;
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

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public void setReadingStatus(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", notificationId").append(notificationId);
        msg.append(", readingStatus=").append(readingStatus);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
