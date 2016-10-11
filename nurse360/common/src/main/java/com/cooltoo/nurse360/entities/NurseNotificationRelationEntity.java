package com.cooltoo.nurse360.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/10/11.
 */
@Entity
@Table(name = "nurse360_nurse_notification_relation")
public class NurseNotificationRelationEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long nurseId;
    private long notificationId;
    private ReadingStatus readingStatus;

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

    @Column(name = "nurse_id")
    public long getNurseId() {
        return nurseId;
    }

    @Column(name = "notification_id")
    public long getNotificationId() {
        return notificationId;
    }

    @Column(name = "reading_status")
    @Enumerated
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
