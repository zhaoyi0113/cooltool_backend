package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import java.util.Date;

/**
 * Created by hp on 2016/7/3.
 */
public class UserReExaminationDateBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long userId;
    private long groupId;
    private long hospitalizedGroupId;
    private Date reExaminationDate;
    private String strReExaminationDate;
    private CommonStatus ignore;
    private YesNoEnum isStartDate;
    private int hasOperation;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getUserId() {
        return userId;
    }

    public long getGroupId() {
        return groupId;
    }

    public long getHospitalizedGroupId() {
        return hospitalizedGroupId;
    }

    public Date getReExaminationDate() {
        return reExaminationDate;
    }

    public String getStrReExaminationDate() {
        return strReExaminationDate;
    }

    public CommonStatus getIgnore() {
        return ignore;
    }

    public YesNoEnum getIsStartDate() {
        return isStartDate;
    }

    public int getHasOperation() {
        return hasOperation;
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

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setHospitalizedGroupId(long hospitalizedGroupId) {
        this.hospitalizedGroupId = hospitalizedGroupId;
    }

    public void setReExaminationDate(Date reExaminationDate) {
        this.reExaminationDate = reExaminationDate;
    }

    public void setStrReExaminationDate(String strReExaminationDate) {
        this.strReExaminationDate = strReExaminationDate;
    }

    public void setIgnore(CommonStatus ignore) {
        this.ignore = ignore;
    }

    public void setIsStartDate(YesNoEnum isStartDate) {
        this.isStartDate = isStartDate;
    }

    public void setHasOperation(int hasOperation) {
        this.hasOperation = hasOperation;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", isStartDate=").append(isStartDate);
        msg.append(", hasOperation=").append(hasOperation);
        msg.append(", groupId=").append(groupId);
        msg.append(", hospitalizedGroupId=").append(hospitalizedGroupId);
        msg.append(", reExaminationDate=").append(reExaminationDate);
        msg.append(", ignore=").append(ignore);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
