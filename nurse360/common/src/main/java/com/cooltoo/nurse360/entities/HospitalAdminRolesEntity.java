package com.cooltoo.nurse360.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.constants.AdminRole;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/11/10.
 */
@Entity
@Table(name = "nurse360_hospital_admin_roles")
public class HospitalAdminRolesEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long adminId;
    private AdminRole role;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "status")
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "admin_id")
    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    @Column(name = "admin_role")
    public AdminRole getRole() {
        return role;
    }

    public void setRole(AdminRole role) {
        this.role = role;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", adminId=").append(adminId);
        msg.append(", role=").append(role);
        msg.append("]");
        return msg.toString();
    }
}
