package com.cooltoo.backend.entities;

import com.cooltoo.constants.CommonStatus;
import org.hibernate.annotations.Generated;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Test111 on 2016/3/18.
 */
@Entity
@Table(name = "nurse_speak_comment")
public class NurseSpeakCommentEntity {
    private long id;
    private long nurseSpeakId;
    private long commentMakerId;
    private long commentReceiverId;
    private String comment;
    private Date time;
    private CommonStatus status;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "nurse_speak_id")
    public long getNurseSpeakId() {
        return nurseSpeakId;
    }

    public void setNurseSpeakId(long nurseSpeakId) {
        this.nurseSpeakId = nurseSpeakId;
    }

    @Column(name = "user_make_comment_id")
    public long getCommentMakerId() {
        return commentMakerId;
    }

    public void setCommentMakerId(long commentMakerId) {
        this.commentMakerId = commentMakerId;
    }

    @Column(name = "user_replied_to_id")
    public long getCommentReceiverId() {
        return commentReceiverId;
    }

    public void setCommentReceiverId(long commentReceiverId) {
        this.commentReceiverId = commentReceiverId;
    }

    @Column(name = "comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(name = "time")
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

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("nurseSpeakId=").append(nurseSpeakId).append(" , ");
        msg.append("commentMakerId=").append(commentMakerId).append(" , ");
        msg.append("commentReceiverId=").append(commentReceiverId).append(" , ");
        msg.append("comment=").append(comment).append(" , ");
        msg.append("time=").append(time).append(", ");
        msg.append("status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
