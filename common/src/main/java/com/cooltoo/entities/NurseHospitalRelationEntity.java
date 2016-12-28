package com.cooltoo.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Entity
@Table(name = "nursego_nurse_hospital_relation")
public class NurseHospitalRelationEntity {

    private long id;
    private long nurseId;
    private NurseEntity nurse;
    private int hospitalId;
    private int departmentId;
    private Date time;
    private CommonStatus status;
    private YesNoEnum approval;
    private Date approvalTime;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "nurse_id")
    public long getNurseId() {
        return nurseId;
    }

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    @Column(name = "hospital_id")
    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    @Column(name = "department_id")
    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    @Column(name = "time_created")
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

    // 映射多对一的关联关系
    @JoinColumn(name="nurse_id", insertable=false, updatable=false)// nursego_nurse_hospital_relation 关联 cooltoo_nurse 表的字段
    @ManyToOne()
    public NurseEntity getNurse() {
        return nurse;
    }

    public void setNurse(NurseEntity nurse) {
        this.nurse = nurse;
    }

    @Column(name = "approval")
    @Enumerated
    public YesNoEnum getApproval() {
        return approval;
    }
    public void setApproval(YesNoEnum approval) {
        this.approval = approval;
    }

    @Column(name = "approval_time")
    public Date getApprovalTime() {
        return approvalTime;
    }
    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", nurseId=").append(nurseId);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(" ]");
        return msg.toString();
    }
}
