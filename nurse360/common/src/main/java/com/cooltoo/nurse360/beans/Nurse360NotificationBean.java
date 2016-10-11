package com.cooltoo.nurse360.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import java.util.Date;

/**
 * Created by hp on 2016/10/9.
 */
public class Nurse360NotificationBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private String title;
    private String introduction;
    private String content;
    private YesNoEnum significance;
    private int hospitalId;
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

    public String getTitle() {
        return title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getContent() {
        return content;
    }

    public YesNoEnum getSignificance() {
        return significance;
    }

    public int getHospitalId() {
        return hospitalId;
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
