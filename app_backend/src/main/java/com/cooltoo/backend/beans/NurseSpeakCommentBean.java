package com.cooltoo.backend.beans;

import java.util.Date;

/**
 * Created by Test111 on 2016/3/18.
 */
public class NurseSpeakCommentBean {
    private long id;
    private long nurseSpeakId;
    private long commentMakerId;
    private long commentReceiverId;
    private String comment;
    private Date time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNurseSpeakId() {
        return nurseSpeakId;
    }

    public void setNurseSpeakId(long nurseSpeakId) {
        this.nurseSpeakId = nurseSpeakId;
    }

    public long getCommentMakerId() {
        return commentMakerId;
    }

    public void setCommentMakerId(long commentMakerId) {
        this.commentMakerId = commentMakerId;
    }

    public long getCommentReceiverId() {
        return commentReceiverId;
    }

    public void setCommentReceiverId(long commentReceiverId) {
        this.commentReceiverId = commentReceiverId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("nurseSpeakId=").append(nurseSpeakId).append(" , ");
        msg.append("commentMakerId=").append(commentMakerId).append(" , ");
        msg.append("commentReceiverId=").append(commentReceiverId).append(" , ");
        msg.append("comment=").append(comment).append(" , ");
        msg.append("time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
