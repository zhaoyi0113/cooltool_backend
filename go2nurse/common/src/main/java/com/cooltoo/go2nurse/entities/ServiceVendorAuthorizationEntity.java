package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/12/1.
 */
@Entity
@Table(name = "go2nurse_service_vendor_authorization")
public class ServiceVendorAuthorizationEntity {

    private long id;
    private Date time;
    private CommonStatus status; /* ENABLED means been forbidden */
    private long userId;
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

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    @Column(name = "vendor_depart_id")
    public long getDepartId() {
        return departId;
    }

    public void setDepartId(long departId) {
        this.departId = departId;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", userId=").append(userId);
        msg.append(", vendorType=").append(vendorType);
        msg.append(", vendorId=").append(vendorId);
        msg.append(", departId=").append(departId);
        msg.append("]");
        return msg.toString();
    }
}
