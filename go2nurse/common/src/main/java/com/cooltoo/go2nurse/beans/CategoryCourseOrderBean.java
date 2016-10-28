package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/10/28.
 */
public class CategoryCourseOrderBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private int hospitalId;
    private HospitalBean hospital;
    private int departmentId;
    private HospitalDepartmentBean department;
    private long categoryId;
    private CourseCategoryBean category;
    private long courseId;
    private CourseBean course;
    private int order;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public HospitalBean getHospital() {
        return hospital;
    }

    public void setHospital(HospitalBean hospital) {
        this.hospital = hospital;
    }

    public HospitalDepartmentBean getDepartment() {
        return department;
    }

    public void setDepartment(HospitalDepartmentBean department) {
        this.department = department;
    }

    public CourseCategoryBean getCategory() {
        return category;
    }

    public void setCategory(CourseCategoryBean category) {
        this.category = category;
    }

    public CourseBean getCourse() {
        return course;
    }

    public void setCourse(CourseBean course) {
        this.course = course;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", departmentId=").append(departmentId);
        msg.append(", categoryId=").append(categoryId);
        msg.append(", courseId=").append(courseId);
        msg.append(", order=").append(order);
        msg.append("]");
        return msg.toString();
    }
}
