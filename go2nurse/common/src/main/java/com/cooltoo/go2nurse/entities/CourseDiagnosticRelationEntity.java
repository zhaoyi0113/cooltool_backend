package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/12.
 */
@Entity
@Table(name = "go2nurse_course_diagnostic_relation")
public class CourseDiagnosticRelationEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long courseId;
    private long diagnosticId;

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

    @Column(name = "course_id")
    public long getCourseId() {
        return courseId;
    }

    @Column(name = "diagnostic_id")
    public long getDiagnosticId() {
        return diagnosticId;
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

    public void setDiagnosticId(long diagnosticId) {
        this.diagnosticId = diagnosticId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", courseId=").append(courseId);
        msg.append(", diagnosticId=").append(diagnosticId);
        msg.append("]");
        return msg.toString();
    }
}
