package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import java.util.Date;

/**
 * Created by hp on 2016/8/26.
 */
public class ReExaminationStrategyBean {
    private long id;
    private Date time;
    private CommonStatus status;
    private int departmentId;
    private HospitalDepartmentBean department;
    private String reExaminationDay;
    private boolean recycled;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public String getReExaminationDay() {
        return reExaminationDay;
    }

    public boolean getRecycled() {
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

    public void setRecycled(boolean recycled) {
        this.recycled = recycled;
    }

    public HospitalDepartmentBean getDepartment() {
        return department;
    }

    public void setDepartment(HospitalDepartmentBean department) {
        this.department = department;
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
