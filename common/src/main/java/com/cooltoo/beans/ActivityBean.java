package com.cooltoo.beans;

import com.cooltoo.constants.ActivityStatus;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hp on 2016/4/20.
 */
public class ActivityBean {
    private long id;
    private String title;
    private String subtitle;
    private String description;
    private Date time;
    private String place;
    private BigDecimal price;
    private String content;
    private Date createTime;
    private ActivityStatus status;
    private long frontCover;
    private String frontCoverUrl;
    private String enrollUrl;
    private int grade;

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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
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

    public String getEnrollUrl() {
        return enrollUrl;
    }

    public void setEnrollUrl(String enrollUrl) {
        this.enrollUrl = enrollUrl;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", grade=").append(grade);
        msg.append(", title=").append(title);
        msg.append(", subtitle=").append(subtitle);
        msg.append(", time=").append(time);
        msg.append(", place=").append(place);
        msg.append(", price=").append(price);
        msg.append(", create_time=").append(createTime);
        msg.append(", status=").append(status);
        msg.append(", frontCover=").append(frontCover);
        msg.append(", description=").append(description);
        msg.append(", content=").append(content);
        msg.append(", enrollUrl=").append(enrollUrl);
        msg.append("]");
        return msg.toString();
    }
}
