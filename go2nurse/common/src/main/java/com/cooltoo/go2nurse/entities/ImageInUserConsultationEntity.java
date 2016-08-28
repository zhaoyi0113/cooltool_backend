package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/8/28.
 */
@Entity
@Table(name = "go2nurse_image_in_user_consultation")
public class ImageInUserConsultationEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long consultationId;
    private long talkId;
    private long imageId;

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
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "consultation_id")
    public long getConsultationId() {
        return consultationId;
    }

    @Column(name = "consultation_talk_id")
    public long getTalkId() {
        return talkId;
    }

    @Column(name = "image_id")
    public long getImageId() {
        return imageId;
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

    public void setConsultationId(long consultationId) {
        this.consultationId = consultationId;
    }

    public void setTalkId(long talkId) {
        this.talkId = talkId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }
}
