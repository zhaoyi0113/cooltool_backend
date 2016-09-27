package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.go2nurse.constants.ReasonType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 16/9/27.
 */
@Entity
@Table(name = "go2nurse_nurse_doctor_score")
public class NurseDoctorScoreEntity {

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

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "receiver_type")
    @Enumerated
    public UserType getReceiverType() {
        return receiverType;
    }

    @Column(name = "receiver_id")
    public long getReceiverId() {
        return receiverId;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    @Column(name = "reason_type")
    @Enumerated
    public ReasonType getReasonType() {
        return reasonType;
    }

    @Column(name = "reason_id")
    public long getReasonId() {
        return reasonId;
    }

    @Column(name = "score")
    public float getScore() {
        return score;
    }

    @Column(name = "weight")
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
