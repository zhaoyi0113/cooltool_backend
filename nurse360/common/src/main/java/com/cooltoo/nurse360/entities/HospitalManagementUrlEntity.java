package com.cooltoo.nurse360.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.RequestMethod;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/11/9.
 */
@Entity
@Table(name = "nurse360_hospital_management_url")
public class HospitalManagementUrlEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private RequestMethod httpType;
    private String httpUrl;
    private String introduction;
    private YesNoEnum needToken;

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

    @Column(name = "http_requests_type")
    @Enumerated
    public RequestMethod getHttpType() {
        return httpType;
    }

    public void setHttpType(RequestMethod httpType) {
        this.httpType = httpType;
    }

    @Column(name = "http_relative_url")
    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    @Column(name = "introduction")
    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Column(name = "need_token")
    public YesNoEnum getNeedToken() {
        return needToken;
    }

    public void setNeedToken(YesNoEnum needToken) {
        this.needToken = needToken;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", needToken=").append(needToken);
        msg.append(", httpType=").append(httpType);
        msg.append(", httpUrl=").append(httpUrl);
        msg.append(", introduction=").append(introduction);
        msg.append("]");
        return msg.toString();
    }
}
