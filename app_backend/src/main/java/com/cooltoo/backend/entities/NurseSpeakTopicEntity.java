package com.cooltoo.backend.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/6/2.
 */
@Entity
@Table(name = "nursego_nurse_speak_topic")
public class NurseSpeakTopicEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long creatorId;
    private UserType creatorType;
    private String title;
    private long profileImageId;
    private long backgroundImageId;
    private String label;
    private String taxonomy;
    private String description;
    private int province;
    private long clickNumber;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "creator")
    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    @Column(name = "creator_type")
    @Enumerated
    public UserType getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(UserType creatorType) {
        this.creatorType = creatorType;
    }

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "profile_photo")
    public long getProfileImageId() {
        return profileImageId;
    }

    public void setProfileImageId(long profileImageId) {
        this.profileImageId = profileImageId;
    }

    @Column(name = "background_photo")
    public long getBackgroundImageId() {
        return backgroundImageId;
    }

    public void setBackgroundImageId(long backgroundImageId) {
        this.backgroundImageId = backgroundImageId;
    }

    @Column(name = "label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(name = "taxonomy")
    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "province")
    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    @Column(name = "click_number")
    public long getClickNumber() {
        return clickNumber;
    }

    public void setClickNumber(long clickNumber) {
        this.clickNumber = clickNumber;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", creatorId=").append(creatorId);
        msg.append(", creatorType=").append(creatorType);
        msg.append(", title=").append(title);
        msg.append(", profileImageId=").append(profileImageId);
        msg.append(", backgroundImageId=").append(backgroundImageId);
        msg.append(", label=").append(label);
        msg.append(", taxonomy=").append(taxonomy);
        msg.append(", description=").append(description);
        msg.append(", province=").append(province);
        msg.append(", clickNumber=").append(clickNumber);
        msg.append("]");
        return msg.toString();
    }
}
