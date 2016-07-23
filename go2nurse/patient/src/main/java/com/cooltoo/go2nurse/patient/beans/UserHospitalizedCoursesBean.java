package com.cooltoo.go2nurse.patient.beans;

import com.cooltoo.go2nurse.beans.CourseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzzhao on 6/22/16.
 */
public class UserHospitalizedCoursesBean {

    private long id;
    private String type;
    private String description;
    private String imageUrl;
    private String name;
    private List<CourseBean> courses = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
