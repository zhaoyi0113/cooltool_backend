package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.util.VerifyUtil;

import java.util.*;

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
    private List<Integer> intReExaminationDay;
    private boolean recycled;
    private boolean isOperation;

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

    public List<Integer> getIntReExaminationDay() {
        return intReExaminationDay;
    }

    public List<Date> getDateReExaminationDay(Date fromDate) {
        List<Date> reExamDate = new ArrayList<>();
        if (null==fromDate) {
            return reExamDate;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        List<Integer> deltaDays = null==intReExaminationDay ? new ArrayList<>() : intReExaminationDay;
        for (Integer deltaDay : deltaDays) {
            calendar.add(Calendar.DAY_OF_MONTH, deltaDay);
            reExamDate.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, -deltaDay);
        }
        return reExamDate;
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
        this.intReExaminationDay = VerifyUtil.parseIntIds(reExaminationDay);
        Collections.sort(intReExaminationDay, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (null==o1) {
                    return -1;
                }
                if (null==o2) {
                    return 1;
                }
                return o1 - o2;
            }
        });
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

    public boolean isOperation() {
        return isOperation;
    }

    public void setOperation(boolean operation) {
        isOperation = operation;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", reExaminationDay=").append(reExaminationDay);
        msg.append(", recycled=").append(recycled);
        msg.append(", isOperation=").append(isOperation);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
