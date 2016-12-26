package com.cooltoo.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 19/12/2016.
 */
@Entity
@Table(name = "cooltoo_nurse_authorization")
public class NurseAuthorizationEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long nurseId;
    private UserAuthority authOrderHeadNurse;
    private UserAuthority authOrderAdmin;
    private UserAuthority authNotificationHeadNurse;
    private UserAuthority authConsultationHeadNurse;
    private UserAuthority authConsultationAdmin;

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
    public CommonStatus getStatus() {
        return status;
    }
    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "nurse_id")
    public long getNurseId() {
        return nurseId;
    }
    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    @Column(name = "enable_order_head_nurse")
    public UserAuthority getAuthOrderHeadNurse() {
        return authOrderHeadNurse;
    }
    public void setAuthOrderHeadNurse(UserAuthority authOrderHeadNurse) {
        this.authOrderHeadNurse = authOrderHeadNurse;
    }

    @Column(name = "enable_order_admin")
    public UserAuthority getAuthOrderAdmin() {
        return authOrderAdmin;
    }
    public void setAuthOrderAdmin(UserAuthority authOrderAdmin) {
        this.authOrderAdmin = authOrderAdmin;
    }

    @Column(name = "enable_notification_head_nurse")
    public UserAuthority getAuthNotificationHeadNurse() {
        return authNotificationHeadNurse;
    }
    public void setAuthNotificationHeadNurse(UserAuthority authNotificationHeadNurse) {
        this.authNotificationHeadNurse = authNotificationHeadNurse;
    }

    @Column(name = "enable_consultation_head_nurse")
    public UserAuthority getAuthConsultationHeadNurse() {
        return authConsultationHeadNurse;
    }
    public void setAuthConsultationHeadNurse(UserAuthority authConsultationHeadNurse) {
        this.authConsultationHeadNurse = authConsultationHeadNurse;
    }

    @Column(name = "enable_consultation_admin")
    public UserAuthority getAuthConsultationAdmin() {
        return authConsultationAdmin;
    }
    public void setAuthConsultationAdmin(UserAuthority authConsultationAdmin) {
        this.authConsultationAdmin = authConsultationAdmin;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", authOrderHeadNurse=").append(authOrderHeadNurse);
        msg.append(", authOrderAdmin=").append(authOrderAdmin);
        msg.append(", authNotificationHeadNurse=").append(authNotificationHeadNurse);
        msg.append(", authConsultationHeadNurse=").append(authConsultationHeadNurse);
        msg.append(", authConsultationAdmin=").append(authConsultationAdmin);
        msg.append(" ]");
        return msg.toString();
    }
}
