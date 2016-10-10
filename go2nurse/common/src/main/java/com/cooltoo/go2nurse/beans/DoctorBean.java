package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;

import java.util.Date;

/**
 * Created by hp on 2016/7/25.
 */
public class DoctorBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private int grade;
    private String name;
    private String post;
    private String jobTitle;
    private String beGoodAt;
    private long imageId;
    private String imageUrl;
    private int hospitalId;
    private HospitalBean hospital;
    private int departmentId;
    private HospitalDepartmentBean department;
    private float score;
    private String introduction;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public int getGrade() {
        return grade;
    }

    public String getName() {
        return name;
    }

    public String getPost() {
        return post;
    }

    public String getJobTitle() {
        return null==jobTitle ? "" : jobTitle;
    }

    public String getBeGoodAt() {
        return null==beGoodAt ? "" : beGoodAt;
    }

    public long getImageId() {
        return imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public HospitalBean getHospital() {
        return hospital;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public HospitalDepartmentBean getDepartment() {
        return department;
    }

    public float getScore() {
        return score;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setBeGoodAt(String beGoodAt) {
        this.beGoodAt = beGoodAt;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setHospital(HospitalBean hospital) {
        this.hospital = hospital;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public void setDepartment(HospitalDepartmentBean department) {
        this.department = department;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", grade=").append(grade);
        msg.append(", name=").append(name);
        msg.append(", post=").append(post);
        msg.append(", jobTitle=").append(jobTitle);
        msg.append(", beGoodAt=").append(beGoodAt);
        msg.append(", imageId=").append(imageId);
        msg.append(", imageUrl=").append(imageUrl);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", score=").append(score);
        msg.append(", introduction=").append(introduction);
        msg.append("]");
        return msg.toString();
    }
}
