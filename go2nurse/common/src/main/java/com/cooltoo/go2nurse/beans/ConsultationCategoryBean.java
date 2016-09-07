package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import java.util.Date;

/**
 * Created by hp on 2016/8/22.
 */
public class ConsultationCategoryBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private String description;
    private long imageId;
    private String imageUrl;
    private long order;

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

    public String getDescription() {
        return description;
    }

    public long getImageId() {
        return imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getOrder() {
        return order;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", name=").append(name);
        msg.append(", description=").append(description);
        msg.append(", imageId=").append(imageId);
        msg.append(", order=").append(order);
        msg.append("]");
        return msg.toString();
    }
}
