package com.cooltoo.go2nurse.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yzzhao on 2/29/16.
 */
@Entity
@Table(name = "go2nurse_patient")
public class PatientEntity {

    private long id;

    private String name;

    private int officeId;

    private String nickname;

    private int certificateId;

    private String mobile;

    private int age;

    private Date birthday;

    private String usercol;

    @Id
    @Column(name = "id")
    @GeneratedValue
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "office_id")
    public int getOfficeId() {
        return officeId;
    }
    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    @Column(name = "nickname")
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Column(name = "certificate_id")
    public int getCertificateId() {
        return certificateId;
    }
    public void setCertificateId(int certificateId) {
        this.certificateId = certificateId;
    }

    @Column(name = "mobile")
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(name="age")
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @Column(name = "birthdate")
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    @Column(name = "usercol")
    public String getUsercol() { return usercol; }
    public void setUsercol(String usercol) { this.usercol = usercol; }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("officeId=").append(officeId).append(" , ");
        msg.append("certificateId=").append(certificateId).append(" , ");
        msg.append("name=").append(name).append(" , ");
        msg.append("nickname=").append(nickname).append(" , ");
        msg.append("birthday=").append(birthday).append(" , ");
        msg.append("usercol=").append(usercol).append(" , ");
        msg.append("mobile=").append(mobile).append(" , ");
        msg.append("age=").append(age);
        msg.append("]");
        return msg.toString();
    }
}
