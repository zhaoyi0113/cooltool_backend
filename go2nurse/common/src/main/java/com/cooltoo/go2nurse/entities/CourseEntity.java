package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.ActivityStatus;
import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/8.
 */
@Entity
@Table(name = "go2nurse_course")
public class CourseEntity {

    private long id;
    private Date time;
    private ActivityStatus status;
    private String name;
    private String introduction;
    private String content;
    private long frontCover;
    private String link;

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
    public ActivityStatus getStatus() {
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

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(ActivityStatus status) {
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
        msg.append("]");
        return msg.toString();
    }
}
