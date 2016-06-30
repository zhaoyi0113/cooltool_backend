package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/14.
 */
@Entity
@Table(name = "go2nurse_user_hospitalized_relation")
public class UserHospitalizedRelationEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private int hospitalId;
    private int departmentId;
    private long groupId;
    private YesNoEnum hasLeave;

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

    @Column(name = "hospital_id")
    public int getHospitalId() {
        return hospitalId;
    }

    @Column(name = "department_id")
    public int getDepartmentId() {
        return departmentId;
    }

    @Column(name = "group_id")
    public long getGroupId() {
        return groupId;
    }

    @Column(name = "has_leave")
    @Enumerated
    public YesNoEnum getHasLeave() {
        return hasLeave;
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

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setHasLeave(YesNoEnum hasLeave) {
        this.hasLeave = hasLeave;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", groupId=").append(groupId);
        msg.append(", hasLeave=").append(hasLeave);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
