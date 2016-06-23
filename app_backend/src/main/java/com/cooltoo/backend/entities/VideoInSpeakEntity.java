package com.cooltoo.backend.entities;

import com.cooltoo.constants.CCVideoStatus;
import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/23.
 */
@Entity
@Table(name = "nursego_video_in_speak")
public class VideoInSpeakEntity {

    private long id;
    private long speakId;
    private String videoId;
    private long snapshot;
    private long background;
    private CCVideoStatus videoStatus;
    private Date time;
    private CommonStatus status;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "speak_id")
    public long getSpeakId() {
        return speakId;
    }

    @Column(name = "video_id")
    public String getVideoId() {
        return videoId;
    }

    @Column(name = "snapshot")
    public long getSnapshot() {
        return snapshot;
    }

    @Column(name = "background")
    public long getBackground() {
        return background;
    }

    @Column(name = "video_status")
    @Enumerated
    public CCVideoStatus getVideoStatus() {
        return videoStatus;
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

    public void setId(long id) {
        this.id = id;
    }

    public void setSpeakId(long speakId) {
        this.speakId = speakId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setSnapshot(long snapshot) {
        this.snapshot = snapshot;
    }

    public void setBackground(long background) {
        this.background = background;
    }

    public void setVideoStatus(CCVideoStatus videoStatus) {
        this.videoStatus = videoStatus;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", speakId=").append(speakId);
        msg.append(", videoId=").append(videoId);
        msg.append(", snapshot=").append(snapshot);
        msg.append(", background=").append(background);
        msg.append(", videoStatus=").append(videoStatus);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
