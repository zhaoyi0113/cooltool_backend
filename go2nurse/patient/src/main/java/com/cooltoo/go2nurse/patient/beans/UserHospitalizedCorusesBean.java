package com.cooltoo.go2nurse.patient.beans;

import com.cooltoo.go2nurse.beans.CourseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzzhao on 6/22/16.
 */
public class UserHospitalizedCorusesBean {

    private long id;
    private String name;
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

    public List<CourseBean> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseBean> courses) {
        this.courses = courses;
    }
}
