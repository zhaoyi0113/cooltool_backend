package com.cooltoo.backend.beans;

import java.util.Date;

/**
 * Created by Test111 on 2016/3/18.
 */
public class NurseSpeakCommentBean {
    private long id;
    private long nurseSpeakId;
    private long commentMakerId;
    private String makerName;
    private String makerHeadImageUrl;
    private long commentReceiverId;
    private String receiverName;
    private String receiverHeadImageUrl;
    private String comment;
    private boolean isCurrentUserMade;
    private Date time;
    private long speakMakerId;
    private int nurseSpeakTypeId;

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

    public String getMakerName() {
        return makerName;
    }

    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    public String getMakerHeadImageUrl() {
        return makerHeadImageUrl;
    }

    public void setMakerHeadImageUrl(String makerHeadImageUrl) {
        this.makerHeadImageUrl = makerHeadImageUrl;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverHeadImageUrl() {
        return receiverHeadImageUrl;
    }

    public void setReceiverHeadImageUrl(String receiverHeadImageUrl) {
        this.receiverHeadImageUrl = receiverHeadImageUrl;
    }

    public boolean isCurrentUserMade() {
        return isCurrentUserMade;
    }

    public void setIsCurrentUserMade(boolean isCurrentUserMade) {
        this.isCurrentUserMade = isCurrentUserMade;
    }

    public long getSpeakMakerId() {
        return speakMakerId;
    }

    public void setSpeakMakerId(long speakMakerId) {
        this.speakMakerId = speakMakerId;
    }

    public int getNurseSpeakTypeId() {
        return nurseSpeakTypeId;
    }

    public void setNurseSpeakTypeId(int nurseSpeakTypeId) {
        this.nurseSpeakTypeId = nurseSpeakTypeId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(this.hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("nurseSpeakId=").append(nurseSpeakId).append(" , ");
        msg.append("speakMakerId=").append(speakMakerId).append(" , ");
        msg.append("nurseSpeakTypeId=").append(nurseSpeakTypeId).append(" , ");


        msg.append("commentMakerId=").append(commentMakerId).append(" , ");
        msg.append("makerName=").append(makerName).append(" , ");
        msg.append("makerHeadImageUrl=").append(makerHeadImageUrl).append(" , ");

        msg.append("commentReceiverId=").append(commentReceiverId).append(" , ");
        msg.append("receiverName=").append(receiverName).append(" , ");
        msg.append("receiverHeadImageUrl=").append(receiverHeadImageUrl).append(" , ");

        msg.append("isCurrentUserMade=").append(isCurrentUserMade).append(" , ");

        msg.append("comment=").append(comment).append(" , ");
        msg.append("time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
