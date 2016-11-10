package com.cooltoo.nurse360.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.RequestMethod;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/9.
 */
public class HospitalManagementUrlBean {

    public static final String ACCESS_USERs = "accessUsers";

    private long id;
    private Date time;
    private CommonStatus status;
    private RequestMethod httpType;
    private String httpUrl;
    private String introduction;
    private YesNoEnum needToken;
    private Map<String, Object> properties = new HashMap<>();

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

    public RequestMethod getHttpType() {
        return httpType;
    }

    public void setHttpType(RequestMethod httpType) {
        this.httpType = httpType;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public YesNoEnum getNeedToken() {
        return needToken;
    }

    public void setNeedToken(YesNoEnum needToken) {
        this.needToken = needToken;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void setProperties(String key, Object value) {
        if (null==properties) {
            properties = new HashMap<>();
        }
        properties.put(key, value);
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
