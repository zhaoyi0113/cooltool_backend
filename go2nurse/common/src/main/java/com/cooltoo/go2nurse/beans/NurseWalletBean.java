package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.WalletInOutType;

import java.util.Date;

/**
 * Created by zhaolisong on 12/12/2016.
 */
public class NurseWalletBean {

    private long id;
    private CommonStatus status;
    private Date time;
    private long nurseId;
    private String summary;
    private long amount;
    private WalletInOutType reason;
    private long reasonId;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public CommonStatus getStatus() {
        return status;
    }
    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    public long getNurseId() {
        return nurseId;
    }
    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getAmount() {
        return amount;
    }
    public void setAmount(long amount) {
        this.amount = amount;
    }

    public WalletInOutType getReason() {
        return reason;
    }
    public void setReason(WalletInOutType reason) {
        this.reason = reason;
    }

    public long getReasonId() {
        return reasonId;
    }
    public void setReasonId(long reasonId) {
        this.reasonId = reasonId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", summary=").append(summary);
        msg.append(", amount=").append(amount);
        msg.append(", reason=").append(reason);
        msg.append(", reasonId=").append(reasonId);
        msg.append("]");
        return msg.toString();
    }
}
