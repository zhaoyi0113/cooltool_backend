package com.cooltoo.entities;

import javax.persistence.*;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Entity
@Table(name = "cooltoo_hospital")
public class HospitalEntity {
    private int id;
    private String name;
    private String aliasName;
    private int province;
    private int city;
    private int district;
    private String address;
    private int enable;
    private int supportGo2nurse;

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
    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    @Column(name = "city")
    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    @Column(name = "district")
    public int getDistrict() {
        return district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "enable")
    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    @Column(name = "alias_name")
    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    @Column(name = "support_go2nurse")
    public int getSupportGo2nurse() {
        return supportGo2nurse;
    }

    public void setSupportGo2nurse(int supportGo2nurse) {
        this.supportGo2nurse = supportGo2nurse;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", aliasName=").append(aliasName);
        msg.append(", province=").append(province);
        msg.append(", city=").append(city);
        msg.append(", district=").append(district);
        msg.append(", address=").append(address);
        msg.append(", enable=").append(enable);
        msg.append(", supportGo2nurse=").append(supportGo2nurse);
        msg.append("]");
        return msg.toString();
    }
}
