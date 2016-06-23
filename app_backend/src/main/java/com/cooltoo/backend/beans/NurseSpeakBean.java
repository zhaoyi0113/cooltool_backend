package com.cooltoo.backend.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yzzhao on 3/15/16.
 */
public class NurseSpeakBean {
    private long id;
    private long userId;
    private String content;
    private String userName;
    private String anonymousName;
    private String userProfilePhotoUrl;
    private Date time;
    private List<ImagesInSpeakBean> images;
    /** the id of speak_type */
    private int speakType;
    private List<NurseSpeakCommentBean> comments;
    private List<NurseSpeakThumbsUpBean> thumbsUps;
    private int commentsCount;
    private int thumbsUpsCount;
    private CommonStatus status;
    private List<NurseSpeakTopicBean> topics = new ArrayList<>();
    private List<VideoInSpeakBean> videos = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserProfilePhotoUrl() {
        return this.userProfilePhotoUrl;
    }

    public void setUserProfilePhotoUrl(String userProfilePhotoUrl) {
        this.userProfilePhotoUrl = userProfilePhotoUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public List<ImagesInSpeakBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesInSpeakBean> images) {
        this.images = images;
    }

    public int getSpeakType() {
        return speakType;
    }

    public void setSpeakType(int speakType) {
        this.speakType = speakType;
    }

    public List<NurseSpeakCommentBean> getComments() {
        return this.comments;
    }

    public void setComments(List<NurseSpeakCommentBean> comments) {
        this.comments = comments;
    }

    public List<NurseSpeakThumbsUpBean> getThumbsUps() {
        return this.thumbsUps;
    }

    public void setThumbsUps(List<NurseSpeakThumbsUpBean> thumbsUps) {
        this.thumbsUps = thumbsUps;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getThumbsUpsCount() {
        return thumbsUpsCount;
    }

    public void setThumbsUpsCount(int thumbsUpsCount) {
        this.thumbsUpsCount = thumbsUpsCount;
    }

    public String getAnonymousName() {
        return anonymousName;
    }

    public void setAnonymousName(String anonymousName) {
        this.anonymousName = anonymousName;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public void setTopics(List<NurseSpeakTopicBean> topics) {
        this.topics = topics;
    }

    public List<VideoInSpeakBean> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoInSpeakBean> videos) {
        this.videos = videos;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("userId=").append(userId).append(" , ");
        msg.append("status=").append(status).append(" , ");
        msg.append("username=").append(userName).append(" , ");
        msg.append("anonymousName=").append(anonymousName).append(" , ");
        msg.append("time=").append(time).append(" , ");
        msg.append("content=").append(content).append(" , ");
        msg.append("thumbsUpsCount=").append(thumbsUpsCount).append(" , ");
        msg.append("commentsCount=").append(commentsCount).append(" , ");
        msg.append("topicsCount={}").append(topics.size()).append(" , ");
        msg.append("speakType=").append(speakType);
        int countC = null==comments  ? 0 : comments.size();
        int countT = null==thumbsUps ? 0 : thumbsUps.size();
        int countI = null==images    ? 0 : images.size();
        if (countC>0 || countT>0) {
            msg.append("\r\n");
        }
        for (int i = 0; i < countC; i++) {
            msg.append(comments.get(i)).append("\r\n");
        }
        for (int i = 0; i < countT; i++) {
            msg.append(thumbsUps.get(i)).append("\r\n");
        }
        for (int i = 0; i < countI; i++) {
            msg.append(images.get(i)).append("\r\n");
        }
        msg.append("]");
        return msg.toString();
    }
}
