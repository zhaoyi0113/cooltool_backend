package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.WalletInOutType;
import com.cooltoo.go2nurse.constants.WalletProcess;
import com.cooltoo.util.VerifyUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaolisong on 12/12/2016.
 */
public class NurseWalletBean {

    public static final String REASON_ORDER = "reason_order";

    private long id;
    private CommonStatus status;
    private Date time;
    private long nurseId;
    private NurseBean nurse;
    private String summary;
    private long amountCent;
    private String amount;
    private WalletInOutType reason;
    private long reasonId;
    private WalletProcess process;
    private Map<String, Object> properties = new HashMap<>();

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

    public NurseBean getNurse() {
        return nurse;
    }
    public void setNurse(NurseBean nurse) {
        this.nurse = nurse;
    }

    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAmount() { return amount; }
    public long getAmountCent() {
        return amountCent;
    }
    public void setAmountCent(long amountCent) {
        this.amountCent = amountCent;
        this.amount = VerifyUtil.parsePrice((int)amountCent);
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

    public WalletProcess getProcess() {
        return process;
    }
    public void setProcess(WalletProcess process) {
        this.process = process;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    public void setProperties(String key, Object value) {
        this.properties.put(key, value);
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", summary=").append(summary);
        msg.append(", amountCent=").append(amountCent);
        msg.append(", reason=").append(reason);
        msg.append(", reasonId=").append(reasonId);
        msg.append(", process=").append(process);
        msg.append("]");
        return msg.toString();
    }
}
