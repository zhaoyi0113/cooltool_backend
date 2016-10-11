package com.cooltoo.nurse360.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/10/9.
 */
@Entity
@Table(name = "nurse360_notification")
public class Nurse360NotificationEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private String title;
    private String introduction;
    private String content;
    private YesNoEnum significance;
    private int hospitalId;
    private int departmentId;

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

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    @Column(name = "introduction")
    public String getIntroduction() {
        return introduction;
    }

    @Column(name = "content")
    public String getContent() {
        return content;
    }

    @Column(name = "significance")
    @Enumerated
    public YesNoEnum getSignificance() {
        return significance;
    }

    @Column(name = "hospital_id")
    public int getHospitalId() {
        return hospitalId;
    }

    @Column(name = "department_id")
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSignificance(YesNoEnum significance) {
        this.significance = significance;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
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
        msg.append(", title=").append(title);
        msg.append(", introduction=").append(introduction);
        msg.append(", content=").append(content);
        msg.append(", significance=").append(significance);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", departmentId=").append(departmentId);
        msg.append("]");
        return msg.toString();
    }
}
