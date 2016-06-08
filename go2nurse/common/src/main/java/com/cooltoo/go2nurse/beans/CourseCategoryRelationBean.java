package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by hp on 2016/6/8.
 */
public class CourseCategoryRelationBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long courseId;
    private String courseName;
    private long courseCategoryId;
    private String courseCategoryName;

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

    public String getCourseName() {
        return courseName;
    }

    public long getCourseCategoryId() {
        return courseCategoryId;
    }

    public String getCourseCategoryName() {
        return courseCategoryName;
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

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseCategoryId(long courseCategoryId) {
        this.courseCategoryId = courseCategoryId;
    }

    public void setCourseCategoryName(String courseCategoryName) {
        this.courseCategoryName = courseCategoryName;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", courseId=").append(courseId);
        msg.append(", courseName=").append(courseName);
        msg.append(", courseCategoryId=").append(courseCategoryId);
        msg.append(", courseCategoryName=").append(courseCategoryName);
        msg.append("]");
        return msg.toString();
    }
}
