package com.cooltoo.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Entity
@Table(name = "tags_category")
public class TagsCategoryEntity {
    private long id;
    private String name;
    private long imageId;
    private Date timeCreated;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "image_id")
    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    @Column(name = "time_created")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append(" id=").append(id).append(", ");
        msg.append(" name=").append(name).append(", ");
        msg.append(" imageId=").append(imageId).append(", ");
        msg.append(" timeCreated=").append(timeCreated).append("] ");
        return msg.toString();
    }
}
