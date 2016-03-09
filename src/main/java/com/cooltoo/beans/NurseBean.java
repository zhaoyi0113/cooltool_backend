package com.cooltoo.beans;

import javax.ws.rs.FormParam;

/**
 * Created by lg380357 on 2016/3/2.
 */
public class NurseBean {

    @FormParam("id")
    private long id;

    @FormParam("identificateId")
    private String identificationId;

    @FormParam("name")
    private String name;

    @FormParam("gender")
    private int gender;

    @FormParam("mobile")
    private String mobile;

    @FormParam("age")
    private int age;

    private String password;
    private String profilePhotoUrl;
    private String backgroundImageUrl;


    public long getId() { return this.id; }
    public void setId(long id) { this.id = id; }
    public String getIdentificationId() { return identificationId; }
    public void setIdentificationId(String identificationId) { this.identificationId = identificationId; }
    public int getGender() { return gender; }
    public void setGender(int gender) { this.gender = gender; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("iid=").append(identificationId).append(" , ");
        msg.append("name=").append(name).append(" , ");
        msg.append("gender=").append(gender).append(" , ");
        msg.append("age=").append(age).append(" , ");
        msg.append("password=").append(password).append(" , ");
        msg.append("profilePhotoUrl=").append(profilePhotoUrl).append(" , ");
        msg.append("backgroundImageUrl=").append(backgroundImageUrl);
        msg.append("]");
        return msg.toString();
    }
}
