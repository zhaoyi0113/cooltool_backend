package com.cooltoo.backend.entities;

import javax.persistence.*;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Entity
@Table(name = "hospital_department")
public class HospitalDepartmentEntity {
    private int id;
    private String name;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("name=").append(name);
        msg.append("]");
        return msg.toString();
    }
}
