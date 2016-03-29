package com.cooltoo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Entity
@Table(name = "region")
public class RegionEntity {

    private int id;
    private String code;
    private String name;
    private String enName;
    private int parentId;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "en_name")
    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    @Column(name = "parent_id")
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append("id").append(", ");
        msg.append("code=").append(code).append(", ");
        msg.append("name=").append(name).append(", ");
        msg.append("enName=").append(enName).append(", ");
        msg.append("parentId=").append(parentId).append("]");
        return msg.toString();
    }
}
