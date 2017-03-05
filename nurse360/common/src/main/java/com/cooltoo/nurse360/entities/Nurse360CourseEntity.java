package com.cooltoo.nurse360.entities;

import com.cooltoo.go2nurse.constants.CourseStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/10/9.
 */
@Entity
@Table(name = "nurse360_course")
public class Nurse360CourseEntity {

    private long id;
    private Date time;
    private CourseStatus status;
    private String name;
    private String introduction;
    private String content;
    private long frontCover;
    private String link;
    private String uniqueId;
    private String keyword;
    private long categoryId;
    private long publisherId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    @Column(name = "status")
    @Enumerated
    public CourseStatus getStatus() {
        return status;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "introduction")
    public String getIntroduction() {
        return introduction;
    }

    @Column(name = "content")
    public String getContent() {
        return content;
    }

    @Column(name = "front_cover")
    public long getFrontCover() {
        return frontCover;
    }

    @Column(name = "link")
    public String getLink() {
        return link;
    }

    @Column(name = "unique_id")
    public String getUniqueId() {
        return uniqueId;
    }

    @Column(name = "keyword")
    public String getKeyword() {
        return keyword;
    }

    @Column(name = "category_id")
    public long getCategoryId() {
        return categoryId;
    }

    @Column(name = "publisher_id")
    public long getPublisherId() {
        return publisherId;
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

    public void setLink(String link) {
        this.link = link;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setPublisherId(long publisherId) {
        this.publisherId = publisherId;
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
        msg.append(", link=").append(link);
        msg.append(", uniqueId=").append(uniqueId);
        msg.append(", keyword=").append(keyword);
        msg.append(", categoryId=").append(categoryId);
        msg.append(", publisherId=").append(publisherId);
        msg.append("]");
        return msg.toString();
    }
}
