package com.cooltoo.entities;

import com.cooltoo.constants.ActivityStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hp on 2016/4/20.
 */
@Entity
@Table(name = "platform_activities")
public class ActivityEntity {
    private long id;
    private String title;
    private String subtitle;
    private String description;
    private Date time;
    private String place;
    private BigDecimal price;
    private long frontCover;
    private String content;
    private Date createTime;
    private ActivityStatus status;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "subtitle")
    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "time")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "place")
    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @Column(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "status")
    @Enumerated
    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    @Column(name = "front_cover")
    public long getFrontCover() {
        return frontCover;
    }

    public void setFrontCover(long frontCover) {
        this.frontCover = frontCover;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
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
        msg.append("]");
        return msg.toString();
    }
}
