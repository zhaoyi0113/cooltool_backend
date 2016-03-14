package com.cooltoo.admin.beans;

import javax.ws.rs.FormParam;

/**
 * Created by yzzhao on 2/24/16.
 */
public class BadgeBean {

    @FormParam("id")
    private int id;

    @FormParam("name")
    private String name;

    @FormParam("grade")
    private int grade;

    private String imageUrl;

    @FormParam("point")
    private long point;

    private long fileId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(BadgeBean.class).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", grade=").append(grade);
        msg.append(", point=").append(point);
        msg.append(", file_id=").append(fileId);
        msg.append("]");
        return msg.toString();
    }
}
