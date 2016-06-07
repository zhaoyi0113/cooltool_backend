package com.cooltoo.backend.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 16/4/14.
 */
@Entity
@Table(name = "nursego_images_in_speak")
public class ImagesInSpeakEntity {
    private long id;
    private long speakId;
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

    @Column(name = "speak_id")
    public long getSpeakId() {
        return speakId;
    }

    public void setSpeakId(long speakId) {
        this.speakId = speakId;
    }

    @Column(name = "image_id")
    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
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
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("speakId=").append(speakId).append(", ");
        msg.append("imageId=").append(imageId).append(", ");
        msg.append("timeCreated=").append(timeCreated).append("] ");
        return msg.toString();
    }
}
