package com.cooltoo.nurse360.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.ServiceVendorType;

import java.util.Date;

/**
 * Created by hp on 2016/10/9.
 */
public class Nurse360NotificationBean {

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

    private YesNoEnum hasRead;

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

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroduction() {
        return introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public YesNoEnum getSignificance() {
        return significance;
    }
    public void setSignificance(YesNoEnum significance) {
        this.significance = significance;
    }

    public ServiceVendorType getVendorType() {
        return vendorType;
    }
    public void setVendorType(ServiceVendorType vendorType) {
        this.vendorType = vendorType;
    }

    public long getVendorId() {
        return vendorId;
    }
    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public long getDepartId() {
        return departId;
    }
    public void setDepartId(long departId) {
        this.departId = departId;
    }

    public YesNoEnum getHasRead() {
        return hasRead;
    }
    public void setHasRead(YesNoEnum hasRead) {
        this.hasRead = hasRead;
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
        msg.append(", hasRead=").append(hasRead);
        msg.append("]");
        return msg.toString();
    }
}
