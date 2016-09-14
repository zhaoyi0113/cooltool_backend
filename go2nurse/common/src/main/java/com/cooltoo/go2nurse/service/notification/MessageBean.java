package com.cooltoo.go2nurse.service.notification;

/**
 * Created by hp on 2016/9/14.
 */
public class MessageBean {

    private String alertBody;
    private String type;
    private String status;
    private long relativeId;
    private String description;

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
