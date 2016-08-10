package com.cooltoo.backend.beans;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;

import java.util.Date;

/**
 * Created by hp on 2016/6/2.
 */
public class NurseSpeakTopicBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long creatorId;
    private UserType creatorType;
    private NurseBean creator;
    private String title;
    private long profileImageId;
    private String profileImageUrl;
    private long backgroundImageId;
    private String backgroundImageUrl;
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

    public UserType getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(UserType creatorType) {
        this.creatorType = creatorType;
    }

    public NurseBean getCreator() {
        return creator;
    }

    public void setCreator(NurseBean creator) {
        this.creator = creator;
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

    public long getBackgroundImageId() {
        return backgroundImageId;
    }

    public void setBackgroundImageId(long backgroundImageId) {
        this.backgroundImageId = backgroundImageId;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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
        msg.append(", creatorType=").append(creatorType);
        msg.append(", creator=").append(creator);
        msg.append(", title=").append(title);
        msg.append(", profileImageId=").append(profileImageId);
        msg.append(", profileImageUrl=").append(profileImageUrl);
        msg.append(", backgroundImageId=").append(backgroundImageId);
        msg.append(", backgroundImageUrl=").append(backgroundImageUrl);
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
