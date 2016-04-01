package com.cooltoo.beans;

/**
 * Created by lg380357 on 2016/3/5.
 */
public class NurseHospitalRelationBean {

    private long id;
    private long nurseId;
    private int hospitalId;
    private int departmentId;
    private HospitalBean hospital;
    private HospitalDepartmentBean department;
    private HospitalDepartmentBean parentDepart;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNurseId() {
        return nurseId;
    }

    public void setNurseId(long nurseId) {
        this.nurseId = nurseId;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public HospitalBean getHospital() {
        return this.hospital;
    }

    public void setHospital(HospitalBean hospital) {
        this.hospital = hospital;
    }

    public HospitalDepartmentBean getDepartment() {
        return this.department;
    }

    public void setDepartment(HospitalDepartmentBean department) {
        this.department = department;
    }

    public HospitalDepartmentBean getParentDepart() {
        return this.parentDepart;
    }

    public void setParentDepart(HospitalDepartmentBean parentDepart) {
        this.parentDepart = parentDepart;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append("nurseId=").append(nurseId).append(" ,");
        msg.append("hospitalId=").append(hospitalId).append(" ,");
        msg.append("departmentId=").append(departmentId);
        msg.append(" ]");
        return msg.toString();
    }
}
