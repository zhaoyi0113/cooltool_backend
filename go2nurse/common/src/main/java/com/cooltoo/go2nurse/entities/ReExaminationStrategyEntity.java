package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/8/26.
 */
@Entity
@Table(name = "go2nurse_re_examination_strategy")
public class ReExaminationStrategyEntity {
    private long id;
    private Date time;
    private CommonStatus status;
    private int departmentId;
    private String reExaminationDay;
    private YesNoEnum recycled;

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

    @Column(name = "department_id")
    public int getDepartmentId() {
        return departmentId;
    }

    @Column(name = "re_examination_day")
    public String getReExaminationDay() {
        return reExaminationDay;
    }

    @Column(name = "recycled")
    public YesNoEnum getRecycled() {
        return recycled;
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

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public void setReExaminationDay(String reExaminationDay) {
        this.reExaminationDay = reExaminationDay;
    }

    public void setRecycled(YesNoEnum recycled) {
        this.recycled = recycled;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", reExaminationDay=").append(reExaminationDay);
        msg.append(", recycled=").append(recycled);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
