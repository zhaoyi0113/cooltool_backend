package com.cooltoo.entities;

import javax.persistence.*;

/**
 * Created by yzzhao on 3/1/16.
 */
@Entity
@Table(name = "nurse")
public class NurseEntity {

    private long id;

    /** 身份证号 */
    private String identificationId;

    private int gender;

    private String name;

    private String mobile;

    private int age;

    private String password;

    private long profilePhotoId;

    private long backgroundImageId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "identificate_id")
    public String getIdentificationId() {
        return identificationId;
    }

    public void setIdentificationId(String identificationId) {
        this.identificationId = identificationId;
    }

    @Column(name = "gender")
    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "mobile")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(name = "age")
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Column(name = "pwd")
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    @Column(name = "profile_photo_id")
    public long getProfilePhotoId() { return profilePhotoId; }
    public void setProfilePhotoId(long profilePhotoId) { this.profilePhotoId = profilePhotoId; }
    @Column(name = "background_image_id")
    public long getBackgroundImageId() { return backgroundImageId; }
    public void setBackgroundImageId(long backgroundImageId) {this.backgroundImageId = backgroundImageId;}


    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append("identificationId=").append(identificationId).append(" ,");
        msg.append("name=").append(name).append(" ,");
        msg.append("gender=").append(gender).append(" ,");
        msg.append("mobile=").append(mobile).append(" ,");
        msg.append("age=").append(age).append(" , ");
        msg.append("password=").append(password).append(" , ");
        msg.append("profilePhotoId=").append(profilePhotoId).append(" , ");
        msg.append("backgroundImageId=").append(backgroundImageId);
        msg.append(" ]");
        return msg.toString();
    }
}
