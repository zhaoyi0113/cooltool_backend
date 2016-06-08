package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/8.
 */
@Entity
@Table(name = "go2nurse_course_category_relation")
public class CourseCategoryRelationEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long courseId;
    private long courseCategoryId;

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
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "course_id")
    public long getCourseId() {
        return courseId;
    }

    @Column(name = "course_category_id")
    public long getCourseCategoryId() {
        return courseCategoryId;
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

    public void setCourseCategoryId(long courseCategoryId) {
        this.courseCategoryId = courseCategoryId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", courseId=").append(courseId);
        msg.append(", courseCategoryId=").append(courseCategoryId);
        msg.append("]");
        return msg.toString();
    }
}
