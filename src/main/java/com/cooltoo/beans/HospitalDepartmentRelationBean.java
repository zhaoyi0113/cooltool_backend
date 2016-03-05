package com.cooltoo.beans;

import javax.persistence.*;

/**
 * Created by lg380357 on 2/29/16.
 */
public class HospitalDepartmentRelationBean {

    private int id;

    private int hospitalId;

    private int departmentId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getDepartmentId() { return departmentId; }

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
