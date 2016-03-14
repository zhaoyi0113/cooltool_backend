package com.cooltoo.backend.entities;

import javax.persistence.*;

/**
 * Created by yzzhao on 3/10/16.
 */
@Entity
@Table(name = "occupation_skill")
public class OccupationSkillEntity {
    private int id;
    private long imageId;
    private String name;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "image_id")
    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
