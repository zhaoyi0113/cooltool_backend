package com.cooltoo.beans;

import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.UserAuthority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lg380357 on 2016/3/2.
 */
public class NurseBean {

    public static final String QUALIFICATION = "qualification";
    public static final String SKILL_NOMINATION = "skill_nomination";
    public static final String HOSPITAL_DEPARTMENT= "hospital_department";
    public static final String FRIENDS_COUNT = "friend_count";
    public static final String SPEAK_COUNT = "speak_count";
    public static final String ABILITY_COUNT = "skill_count";
    public static final String INFO_EXTENSION = "info_extension";

    public static String getSkillNomination() {
        return SKILL_NOMINATION;
    }

    public static String getFriendsCount() {
        return FRIENDS_COUNT;
    }

    private long id;
    private String name;
    private GenderType gender;
    private String mobile;
    private int age;
    private int integral;
    private String realName;
    private String identification;
    private String shortNote;
    private UserAuthority authority;
    private String password;
    private long profilePhotoId;
    private String profilePhotoUrl;
    private long backgroundImageId;
    private String backgroundImageUrl;
    private String hospital;
    private List<NurseRelationshipBean> relationshipToRequester;
    private Map<String, Object> properties = new HashMap<String, Object>();


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GenderType getGender() {
        return gender;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getProfilePhotoId() {
        return profilePhotoId;
    }

    public void setProfilePhotoId(long profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public long getBackgroundImageId() {
        return backgroundImageId;
    }

    public void setBackgroundImageId(long backgroundImageId) {
        this.backgroundImageId = backgroundImageId;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public Object getProperty(String key){
        return this.properties.get(key);
    }

    public void setProperty(String key, Object value){
        this.properties.put(key, value);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getShortNote() {
        return shortNote;
    }

    public void setShortNote(String shortNote) {
        this.shortNote = shortNote;
    }

    public UserAuthority getAuthority() {
        return this.authority;
    }

    public void setAuthority(UserAuthority authority) {
        this.authority = authority;
    }

    public List<NurseRelationshipBean> getRelationshipToRequester() {
        return relationshipToRequester;
    }

    public void setRelationshipToRequester(List<NurseRelationshipBean> relationshipToRequester) {
        this.relationshipToRequester = relationshipToRequester;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", gender=").append(gender);
        msg.append(", age=").append(age);
        msg.append(", password=").append(password);
        msg.append(", profilePhotoUrl=").append(profilePhotoUrl);
        msg.append(", backgroundImageUrl=").append(backgroundImageUrl);
        msg.append(", integral=").append(integral);
        msg.append(", realName=").append(realName);
        msg.append(", identification=").append(identification);
        msg.append(", shortNote=").append(shortNote);
        msg.append(", relationshipToRequester=").append(relationshipToRequester);
        msg.append(", properties=").append(properties);
        msg.append("]");
        return msg.toString();
    }
}
