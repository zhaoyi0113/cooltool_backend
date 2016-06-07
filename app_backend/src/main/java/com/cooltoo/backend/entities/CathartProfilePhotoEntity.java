package com.cooltoo.backend.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/4/18.
 */
@Entity
@Table(name = "nursego_cathart_profile_photo")
public class CathartProfilePhotoEntity {
    private long id;
    private String name;
    private long imageId;
    private CommonStatus enable;
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

    @Column(name = "enable")
    @Enumerated
    public CommonStatus getEnable() {
        return enable;
    }

    public void setEnable(CommonStatus enable) {
        this.enable = enable;
    }

    @Column(name = "create_time")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        StringBuffer msg = new StringBuffer();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append(" name=").append(name).append(", ");
        msg.append(" imageId=").append(imageId).append(", ");
        msg.append(" enable=").append(enable).append(", ");
        msg.append(" timeCreated=").append(timeCreated).append("] ");
        return msg.toString();
    }
}
