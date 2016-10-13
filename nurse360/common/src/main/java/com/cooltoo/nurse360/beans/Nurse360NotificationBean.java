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
    private YesNoEnum hasRead;

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

    public YesNoEnum getHasRead() {
        return hasRead;
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

    public void setHasRead(YesNoEnum hasRead) {
        this.hasRead = hasRead;
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
        msg.append(", hasRead=").append(hasRead);
        msg.append("]");
        return msg.toString();
    }
}
