package com.cooltoo.nurse360.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.ServiceVendorType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/10/9.
 */
@Entity
@Table(name = "nurse360_notification")
public class Nurse360NotificationEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private String title;
    private String introduction;
    private String content;
    private YesNoEnum significance;
    private ServiceVendorType vendorType;
    private long vendorId;
    private long departId;

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

    @Column(name = "title")
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "introduction")
    public String getIntroduction() {
        return introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Column(name = "content")
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "significance")
    @Enumerated
    public YesNoEnum getSignificance() {
        return significance;
    }
    public void setSignificance(YesNoEnum significance) {
        this.significance = significance;
    }

    @Column(name = "vendor_type")
    @Enumerated
    public ServiceVendorType getVendorType() {
        return vendorType;
    }
    public void setVendorType(ServiceVendorType vendorType) {
        this.vendorType = vendorType;
    }

    @Column(name = "vendor_id")
    public long getVendorId() {
        return vendorId;
    }
    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    @Column(name = "depart_id")
    public long getDepartId() {
        return departId;
    }
    public void setDepartId(long departId) {
        this.departId = departId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", title=").append(title);
        msg.append(", introduction=").append(introduction);
        msg.append(", content=").append(content);
        msg.append(", significance=").append(significance);
        msg.append("]");
        return msg.toString();
    }
}
