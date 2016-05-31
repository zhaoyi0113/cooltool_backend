package com.cooltoo.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SensitiveWordType;

import java.util.Date;

/**
 * Created by hp on 2016/5/31.
 */
public class SensitiveWordBean {


    private int id;
    private String word;
    private SensitiveWordType type;
    private Date time;
    private CommonStatus status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public SensitiveWordType getType() {
        return type;
    }

    public void setType(SensitiveWordType type) {
        this.type = type;
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

    public SensitiveWordBean clone() {
        SensitiveWordBean clone = new SensitiveWordBean();
        clone.setId(id);
        clone.setWord(word);
        clone.setType(type);
        clone.setTime(time);
        clone.setStatus(status);
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj instanceof SensitiveWordBean) {
            return id==((SensitiveWordBean)obj).getId();
        }
        return false;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", word=").append(word);
        msg.append(", type=").append(type);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
