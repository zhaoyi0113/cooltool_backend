package com.cooltoo.backend.beans;

import com.cooltoo.constants.CCVideoStatus;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.VideoPlatform;

import java.util.Date;

/**
 * Created by hp on 2016/6/23.
 */
public class VideoInSpeakBean {

    private long id;
    private long speakId;
    private String videoId;
    private long snapshot;
    private String snapshotUrl;
    private long background;
    private String backgroundUrl;
    private CCVideoStatus videoStatus;
    private Date time;
    private CommonStatus status;
    private VideoPlatform platform;

    public long getId() {
        return id;
    }

    public long getSpeakId() {
        return speakId;
    }

    public String getVideoId() {
        return videoId;
    }

    public long getSnapshot() {
        return snapshot;
    }

    public long getBackground() {
        return background;
    }

    public CCVideoStatus getVideoStatus() {
        return videoStatus;
    }

    public Date getTime() {
        return time;
    }

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

    public String getSnapshotUrl() {
        return snapshotUrl;
    }

    public void setSnapshotUrl(String snapshotUrl) {
        this.snapshotUrl = snapshotUrl;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public VideoPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(VideoPlatform platform) {
        this.platform = platform;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", speakId=").append(speakId);
        msg.append(", videoId=").append(videoId);
        msg.append(", snapshot=").append(snapshot);
        msg.append(", snapshotUrl=").append(snapshotUrl);
        msg.append(", background=").append(background);
        msg.append(", backgroundUrl=").append(backgroundUrl);
        msg.append(", videoStatus=").append(videoStatus);
        msg.append(", platform=").append(platform);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
