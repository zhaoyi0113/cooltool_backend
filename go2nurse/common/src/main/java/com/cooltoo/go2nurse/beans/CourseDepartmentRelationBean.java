package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by hp on 2016/6/12.
 */
public class CourseDepartmentRelationBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long courseId;
    private int departmentId;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getCourseId() {
        return courseId;
    }

    public int getDepartmentId() {
        return departmentId;
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

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", courseId=").append(courseId);
        msg.append(", departmentId=").append(departmentId);
        msg.append("]");
        return msg.toString();
    }
}
