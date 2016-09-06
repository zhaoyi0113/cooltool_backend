package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.ActivityStatus;
import com.cooltoo.constants.CommonStatus;

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
    private int order;

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

    @Column(name = "enroll_url")
    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    @Column(name = "order")
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", grade=").append(order);
        msg.append(", frontCover=").append(frontCover);
        msg.append(", detailsUrl").append(detailsUrl);
        msg.append("]");
        return msg.toString();
    }
}
