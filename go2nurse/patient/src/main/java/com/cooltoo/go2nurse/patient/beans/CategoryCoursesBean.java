package com.cooltoo.go2nurse.patient.beans;

import com.cooltoo.go2nurse.beans.CourseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoyi0113 on 18/10/2016.
 */
public class CategoryCoursesBean {

    private long id;
    private String name;
    private String introduction;
    private String imageUrl;
    private List<CourseBean> courses = new ArrayList<>();

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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<CourseBean> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseBean> courses) {
        this.courses = courses;
    }
}
