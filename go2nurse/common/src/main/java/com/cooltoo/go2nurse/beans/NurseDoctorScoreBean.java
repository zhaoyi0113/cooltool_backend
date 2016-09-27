package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.go2nurse.constants.ReasonType;

import java.util.Date;

/**
 * Created by zhaolisong on 16/9/27.
 */
public class NurseDoctorScoreBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private UserType receiverType;
    private long receiverId;
    private long userId;
    private ReasonType reasonType;
    private long reasonId;
    private float score;
    private int weight;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public UserType getReceiverType() {
        return receiverType;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public long getUserId() {
        return userId;
    }

    public ReasonType getReasonType() {
        return reasonType;
    }

    public long getReasonId() {
        return reasonId;
    }

    public float getScore() {
        return score;
    }

    public int getWeight() {
        return weight;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public void setReceiverType(UserType receiverType) {
        this.receiverType = receiverType;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setReasonType(ReasonType reasonType) {
        this.reasonType = reasonType;
    }

    public void setReasonId(long reasonId) {
        this.reasonId = reasonId;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", receiverType=").append(receiverType);
        msg.append(", receiverId=").append(receiverId);
        msg.append(", userId=").append(userId);
        msg.append(", reasonType=").append(reasonType);
        msg.append(", reasonId=").append(reasonId);
        msg.append(", score=").append(score);
        msg.append(", weight=").append(weight);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
