package com.cooltoo.beans;

import java.sql.Date;

/**
 * Created by yzzhao on 2/29/16.
 */
public class PatientBean {

    private long id;

    private String name;

    private int officeId;

    private String nickname;

    private int certificateId;

    private String mobile;

    private int age;

    private Date birthday;

    private String usercol;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getOfficeId() {
        return officeId;
    }
    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public int getCertificateId() {
        return certificateId;
    }
    public void setCertificateId(int certificateId) {
        this.certificateId = certificateId;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
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
