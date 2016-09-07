package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.ActivityStatus;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.AdvertisementType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hp on 2016/9/6.
 */
@Entity
@Table(name = "go2nurse_advertisement")
public class AdvertisementEntity {
    private long id;
    private Date time;
    private CommonStatus status;
    private long frontCover;
    private String detailsUrl;
    private long orderIndex;
    private String description;
    private AdvertisementType type;

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

    @Column(name = "front_cover")
    public long getFrontCover() {
        return frontCover;
    }

    public void setFrontCover(long frontCover) {
        this.frontCover = frontCover;
    }

    @Column(name = "details_url")
    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    @Column(name = "order_index")
    public long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(long orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "advertisement_type")
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
        msg.append(", orderIndex=").append(orderIndex);
        msg.append(", type=").append(type);
        msg.append(", frontCover=").append(frontCover);
        msg.append(", detailsUrl").append(detailsUrl);
        msg.append(", description=").append(description);
        msg.append("]");
        return msg.toString();
    }
}
