package com.cooltoo.go2nurse.service.notification;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2016/9/14.
 */
public class MessageBean {

    private String alertBody;
    private String type;
    private String status;
    private long relativeId;
    private String description;
    private Map<String, Object> properties = new HashMap<>();

    public String getAlertBody() {
        return alertBody;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public long getRelativeId() {
        return relativeId;
    }

    public String getDescription() {
        return description;
    }

    public void setAlertBody(String alertBody) {
        this.alertBody = alertBody;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRelativeId(long relativeId) {
        this.relativeId = relativeId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(String key, Object value) {
        if (null==key || key.trim().length()==0 || null==value) {
            return;
        }
        properties.put(key, value);
    }

    public StringBuilder toHtmlParam() {
        StringBuilder msg = new StringBuilder();
        msg.append("alert=").append(alertBody);
        msg.append("&description=").append(description);
        msg.append("&message_type=").append(type);
        msg.append("&relative_id=").append(relativeId);
        msg.append("&status=").append(status);
        return msg;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("alertBody=").append(alertBody);
        msg.append(", type=").append(type);
        msg.append(", status=").append(status);
        msg.append(", relativeId=").append(relativeId);
        msg.append(", description=").append(description);
        msg.append("]");
        return msg.toString();
    }
}
