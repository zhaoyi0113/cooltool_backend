package com.cooltoo.backend.entities;

import javax.persistence.*;

/**
 * Created by yzzhao on 3/10/16.
 */
@Table(name = "nursego_nurse_department_relation")
@Entity
public class NurseDepartmentRelationEntity {

    private long id;

    private long urseId;

    private int departmentId;

    @GeneratedValue
    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "user_id")
    public long getUrseId() {
        return urseId;
    }

    public void setUrseId(long urseId) {
        this.urseId = urseId;
    }

    @Column(name = "department_id")
    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }
}
