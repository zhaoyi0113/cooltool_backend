package com.cooltoo.entities;

import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.UserAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzzhao on 3/1/16.
 */
@Entity
@Table(name = "cooltoo_nurse")
public class NurseEntity {

    private long id;
    private GenderType gender;
    private String name;
    private String mobile;
    private int age;
    private String password;
    private long profilePhotoId;
    private long backgroundImageId;
    //用户积分
    private int integral;
    private String realName;
    private String identification;
    private String shortNote;
    private UserAuthority authority;
    private List<NurseExtensionEntity> extensions = new ArrayList<>();
    private List<NurseHospitalRelationEntity> hospitalRelation = new ArrayList<>();

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "gender")
    @Enumerated
    public GenderType getGender() {
        return gender;
    }

    public void setGender(GenderType gender) {
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

    @Column(name = "password")
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    @Column(name = "profile_photo_id")
    public long getProfilePhotoId() { return profilePhotoId; }
    public void setProfilePhotoId(long profilePhotoId) { this.profilePhotoId = profilePhotoId; }
    @Column(name = "background_image_id")
    public long getBackgroundImageId() { return backgroundImageId; }
    public void setBackgroundImageId(long backgroundImageId) {this.backgroundImageId = backgroundImageId;}

    @Column(name = "integral")
    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    @Column(name = "real_name")
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Column(name = "identification")
    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    @Column(name = "short_note")
    public String getShortNote() {
        return shortNote;
    }

    public void setShortNote(String shortNote) {
        this.shortNote = shortNote;
    }

    @Column(name = "authority")
    public UserAuthority getAuthority() {
        return this.authority;
    }

    public void setAuthority(UserAuthority authority) {
        this.authority = authority;
    }

    // 映射一对多的关联关系
    @JoinColumn(name="nurse_id", insertable=false, updatable=false)// cooltoo_nurse_info_extension 关联 cooltoo_nurse 表的字段
    @OneToMany
    public List<NurseExtensionEntity> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<NurseExtensionEntity> extensions) {
        this.extensions = extensions;
    }

    // 映射一对多的关联关系
    @JoinColumn(name="nurse_id", insertable=false, updatable=false)// nursego_nurse_hospital_relation 关联 cooltoo_nurse 表的字段
    @OneToMany
    public List<NurseHospitalRelationEntity> getHospitalRelation() {
        return hospitalRelation;
    }

    public void setHospitalRelation(List<NurseHospitalRelationEntity> hospitalRelation) {
        this.hospitalRelation = hospitalRelation;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append("name=").append(name).append(" ,");
        msg.append("gender=").append(gender).append(" ,");
        msg.append("mobile=").append(mobile).append(" ,");
        msg.append("age=").append(age).append(" , ");
        msg.append("password=").append(password).append(" , ");
        msg.append("profilePhotoId=").append(profilePhotoId).append(" , ");
        msg.append("backgroundImageId=").append(backgroundImageId).append(" , ");
        msg.append("integral=").append(integral).append(" , ");
        msg.append("realName=").append(realName).append(" , ");
        msg.append("identification=").append(identification).append(" , ");
        msg.append("authority=").append(authority).append(" , ");
        msg.append("shortNote=").append(shortNote);
        msg.append(" ]");
        return msg.toString();
    }
}
