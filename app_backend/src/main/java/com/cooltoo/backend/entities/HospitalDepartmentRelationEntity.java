package com.cooltoo.backend.entities;

import javax.persistence.*;

/**
 * Created by lg380357 on 2/29/16.
 */
@Entity
@Table(name = "hospital_department_relation")
public class HospitalDepartmentRelationEntity {

    private int id;

    private int hospitalId;

    private int departmentId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("hospitalId=").append(hospitalId).append(" , ");
        msg.append("departmentId=").append(departmentId);
        msg.append("]");
        return msg.toString();
    }
}
