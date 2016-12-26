package com.cooltoo.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;

import java.util.Date;

/**
 * Created by zhaolisong on 19/12/2016.
 */
public class NurseAuthorizationBean {

    public  String    name = "nurse_authorization";
    private long      id;
    private Date      time;
    private CommonStatus status;
    private long      nurseId;
    private UserAuthority authOrderHeadNurse;
    private UserAuthority authOrderAdmin;
    private UserAuthority authNotificationHeadNurse;
    private UserAuthority authConsultationHeadNurse;

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

    public long getNurseId() {
        return nurseId;
    }
    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public UserAuthority getAuthOrderHeadNurse() {
        return authOrderHeadNurse;
    }
    public void setAuthOrderHeadNurse(UserAuthority authOrderHeadNurse) {
        this.authOrderHeadNurse = authOrderHeadNurse;
    }

    public UserAuthority getAuthOrderAdmin() {
        return authOrderAdmin;
    }
    public void setAuthOrderAdmin(UserAuthority authOrderAdmin) {
        this.authOrderAdmin = authOrderAdmin;
    }

    public UserAuthority getAuthNotificationHeadNurse() {
        return authNotificationHeadNurse;
    }
    public void setAuthNotificationHeadNurse(UserAuthority authNotificationHeadNurse) {
        this.authNotificationHeadNurse = authNotificationHeadNurse;
    }

    public UserAuthority getAuthConsultationHeadNurse() {
        return authConsultationHeadNurse;
    }
    public void setAuthConsultationHeadNurse(UserAuthority authConsultationHeadNurse) {
        this.authConsultationHeadNurse = authConsultationHeadNurse;
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
        msg.append(" ]");
        return msg.toString();
    }
}
