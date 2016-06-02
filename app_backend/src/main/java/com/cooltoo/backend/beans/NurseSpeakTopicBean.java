package com.cooltoo.backend.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by hp on 2016/6/2.
 */
public class NurseSpeakTopicBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long creatorId;
    private String title;
    private long profileImageId;
    private String label;
    private String taxonomy;
    private String description;
    private int province;
    private long clickNumber;
    private long commentNumber;
    private long subscriberNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getProfileImageId() {
        return profileImageId;
    }

    public void setProfileImageId(long profileImageId) {
        this.profileImageId = profileImageId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public long getClickNumber() {
        return clickNumber;
    }

    public void setClickNumber(long clickNumber) {
        this.clickNumber = clickNumber;
    }

    public long getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(long commentNumber) {
        this.commentNumber = commentNumber;
    }

    public long getSubscriberNumber() {
        return subscriberNumber;
    }

    public void setSubscriberNumber(long subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", creatorId=").append(creatorId);
        msg.append(", title=").append(title);
        msg.append(", profileImageId=").append(profileImageId);
        msg.append(", label=").append(label);
        msg.append(", taxonomy=").append(taxonomy);
        msg.append(", description=").append(description);
        msg.append(", province=").append(province);
        msg.append(", clickNumber=").append(clickNumber);
        msg.append(", commentNumber=").append(commentNumber);
        msg.append(", subscriberNumber=").append(subscriberNumber);
        msg.append("]");
        return msg.toString();
    }
}
