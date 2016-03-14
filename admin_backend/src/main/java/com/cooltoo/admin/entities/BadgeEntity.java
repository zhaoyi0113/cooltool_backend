package com.cooltoo.admin.entities;

import javax.persistence.*;

/**
 * Created by yzzhao on 2/24/16.
 */
@Entity
@Table(name = "badge")
public class BadgeEntity {

    private int id;

    private String name;

    private int grade;

    private String imageUrl;

    private long point;

    private long fileId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "grade")
    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Column(name = "image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Column(name = "point")
    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    @Column(name = "file_id")
    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append("name=").append(name).append(" ,");
        msg.append("grade=").append(grade).append(" ,");
        msg.append("point=").append(point).append(" ,");
        msg.append("fileId=").append(fileId).append(" ,");
        msg.append("imageUrl=").append(imageUrl);
        msg.append(" ]");
        return msg.toString();
    }
}
