package com.cooltoo.backend.entities;

import javax.persistence.*;

/**
 * Created by lg380357 on 2016/3/7.
 */
@Entity
@Table(name = "msg")
public class MessageEntity {

    private long id;
    private long nurseId;
    private long millisecond;
    private String content;

    @GeneratedValue
    @Id
    @Column(name = "id")
    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    @Column(name = "nurse_id")
    public long getNurseId() { return nurseId; }
    public void setNurseId(long nurseId) { this.nurseId = nurseId; }

    @Column(name = "msg_date")
    public long getMillisecond() { return millisecond; }
    public void setMillisecond(long millisecond) { this.millisecond = millisecond; }

    @Column(name = "msg_content")
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append("nurse_id=").append(nurseId).append(" ,");
        msg.append("msg_date=").append(millisecond).append(" ,");
        msg.append("msg_content=").append(content);
        msg.append(" ]");
        return msg.toString();
    }
}
