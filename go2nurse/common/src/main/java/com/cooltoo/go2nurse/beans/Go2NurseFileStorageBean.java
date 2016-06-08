package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by hp on 2016/6/8.
 */
public class Go2NurseFileStorageBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private String realName;
    private String relativePath;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public String getRealName() {
        return realName;
    }

    public String getRelativePath() {
        return relativePath;
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

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", realName=").append(realName);
        msg.append(", relativePath=").append(relativePath);
        msg.append("]");
        return msg.toString();
    }
}
