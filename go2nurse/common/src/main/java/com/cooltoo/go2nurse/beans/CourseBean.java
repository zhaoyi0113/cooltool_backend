package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.constants.CourseStatus;

import java.util.Date;

/**
 * Created by hp on 2016/6/8.
 */
public class CourseBean {

    private long id;
    private Date time;
    private CourseStatus status;
    private String name;
    private String introduction;
    private String content;
    private long frontCover;
    private String frontCoverUrl;
    private String link;
    private ReadingStatus reading;
    private String uniqueId;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getContent() {
        return content;
    }

    public long getFrontCover() {
        return frontCover;
    }

    public String getFrontCoverUrl() {
        return frontCoverUrl;
    }

    public String getLink() {
        return link;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFrontCover(long frontCover) {
        this.frontCover = frontCover;
    }

    public void setFrontCoverUrl(String frontCoverUrl) {
        this.frontCoverUrl = frontCoverUrl;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ReadingStatus getReading() {
        return reading;
    }

    public void setReading(ReadingStatus reading) {
        this.reading = reading;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", name=").append(name);
        msg.append(", introduction=").append(introduction);
        msg.append(", content=").append(content);
        msg.append(", frontCover=").append(frontCover);
        msg.append(", frontCoverUrl=").append(frontCoverUrl);
        msg.append(", link=").append(link);
        msg.append(", uniqueId=").append(uniqueId);
        msg.append(", reading=").append(reading);
        msg.append("]");
        return msg.toString();
    }
}
