package com.cooltoo.entities;

import javax.persistence.*;

/**
 * Created by yzzhao on 2/22/16.
 */
@Entity
@Table(name = "hello")
public class HelloEntity {

    private long id;

    private String name;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
