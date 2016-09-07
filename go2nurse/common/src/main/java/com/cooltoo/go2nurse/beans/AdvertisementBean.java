package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.AdvertisementType;

import java.util.Date;

/**
 * Created by hp on 2016/9/6.
 */
public class AdvertisementBean {
    private long id;
    private Date time;
    private CommonStatus status;
    private long frontCover;
    private String frontCoverUrl;
    private String detailsUrl;
    private long order;
    private String description;
    private AdvertisementType type;

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

    public long getFrontCover() {
        return frontCover;
    }

    public void setFrontCover(long frontCover) {
        this.frontCover = frontCover;
    }

    public String getFrontCoverUrl() {
        return frontCoverUrl;
    }

    public void setFrontCoverUrl(String frontCoverUrl) {
        this.frontCoverUrl = frontCoverUrl;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AdvertisementType getType() {
        return type;
    }

    public void setType(AdvertisementType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", order=").append(order);
        msg.append(", type=").append(type);
        msg.append(", frontCover=").append(frontCover);
        msg.append(", detailsUrl").append(detailsUrl);
        msg.append(", description=").append(description);
        msg.append("]");
        return msg.toString();
    }
}
