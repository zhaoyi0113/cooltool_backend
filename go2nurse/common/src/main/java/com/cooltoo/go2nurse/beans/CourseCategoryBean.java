package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by hp on 2016/6/8.
 */
public class CourseCategoryBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private String introduction;
    private long imageId;
    private String imageUrl;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public long getImageId() {
        return imageId;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", name=").append(name);
        msg.append(", introduction=").append(introduction);
        msg.append(", imageId=").append(imageId);
        msg.append(", imageUrl=").append(imageUrl);
        msg.append("]");
        return msg.toString();
    }
}
