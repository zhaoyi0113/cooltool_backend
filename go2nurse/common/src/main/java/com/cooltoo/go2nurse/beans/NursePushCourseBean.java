package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.ReadingStatus;

import java.util.Date;

/**
 * Created by zhaolisong on 2016/11/4.
 */
public class NursePushCourseBean {

    private long id;
    private Date time;
    private ReadingStatus read;
    private long nurseId;
    private long userId;
    private long patientId;
    private long courseId;
    private CourseBean course;

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

    public ReadingStatus getRead() {
        return read;
    }

    public void setRead(ReadingStatus status) {
        this.read = status;
    }

    public long getNurseId() {
        return nurseId;
    }

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public CourseBean getCourse() {
        return course;
    }

    public void setCourse(CourseBean course) {
        this.course = course;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", userId=").append(userId);
        msg.append(", patientId=").append(patientId);
        msg.append(", courseId=").append(courseId);
        msg.append(", read=").append(read);
        msg.append("]");
        return msg.toString();
    }
}
