package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/10/23.
 */
@Entity
@Table(name = "go2nurse_case")
public class CaseEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long nurseId;
    private long casebookId;
    private String caseRecord;

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

    @Column(name = "nurse_id")
    public long getNurseId() {
        return nurseId;
    }

    @Column(name = "casebook_id")
    public long getCasebookId() {
        return casebookId;
    }

    @Column(name = "case_record")
    public String getCaseRecord() {
        return caseRecord;
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

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public void setCasebookId(long casebookId) {
        this.casebookId = casebookId;
    }

    public void setCaseRecord(String caseRecord) {
        this.caseRecord = caseRecord;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", consultationId=").append(casebookId);
        msg.append(", talkStatus=").append(caseRecord);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
