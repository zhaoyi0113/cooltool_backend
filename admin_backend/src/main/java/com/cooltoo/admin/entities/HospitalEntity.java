package com.cooltoo.admin.entities;

import javax.persistence.*;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Entity
@Table(name = "hospital")
public class HospitalEntity {
    private int id;
    private String name;
    private String province;
    private String city;

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

    @Column(name = "province")
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("name=").append(name).append(" , ");
        msg.append("province=").append(province).append(" , ");
        msg.append("city=").append(city);
        msg.append("]");
        return msg.toString();
    }
}
