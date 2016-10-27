package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/7/25.
 */
@Entity
@Table(name = "go2nurse_doctor")
public class DoctorEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private int grade;
    private String name;
    private String post;
    private String jobTitle;
    private String beGoodAt;
    private long imageId;
    private int hospitalId;
    private int departmentId;
    private String introduction;
    private long headImageId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "grade")
    public int getGrade() {
        return grade;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "post")
    public String getPost() {
        return post;
    }

    @Column(name = "job_title")
    public String getJobTitle() {
        return jobTitle;
    }

    @Column(name = "be_good_at")
    public String getBeGoodAt() {
        return beGoodAt;
    }

    @Column(name = "image_id")
    public long getImageId() {
        return imageId;
    }

    @Column(name = "hospital_id")
    public int getHospitalId() {
        return hospitalId;
    }

    @Column(name = "department_id")
    public int getDepartmentId() {
        return departmentId;
    }

    @Column(name = "introduction")
    public String getIntroduction() {
        return introduction;
    }

    @Column(name = "head_image_id")
    public long getHeadImageId() {
        return headImageId;
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

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setHeadImageId(long headImageId) {
        this.headImageId = headImageId;
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
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", introduction=").append(introduction);
        msg.append(", headImageId=").append(headImageId);
        msg.append("]");
        return msg.toString();
    }
}
