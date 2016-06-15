package com.cooltoo.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.EmploymentType;

import java.util.Date;

/**
 * Created by hp on 2016/4/20.
 */
public class EmploymentInformationBean {
    private long id;
    private Date time;
    private CommonStatus status;
    private String title;
    private long frontCover;
    private String frontCoverUrl;
    private String url;
    private int grade;
    private EmploymentType type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getFrontCover() {
        return frontCover;
    }

    public void setFrontCover(long frontCover) {
        this.frontCover = frontCover;
    }

    public String getFrontCoverUrl() {
        return frontCoverUrl;
    }

    public void setFrontCoverUrl(String frontCoverUrl) {
        this.frontCoverUrl = frontCoverUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public EmploymentType getType() {
        return type;
    }

    public void setType(EmploymentType type) {
        this.type = type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", grade=").append(grade);
        msg.append(", title=").append(title);
        msg.append(", frontCover=").append(frontCover);
        msg.append(", url").append(url);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
