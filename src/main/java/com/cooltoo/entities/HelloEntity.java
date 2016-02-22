package com.cooltoo.entities;

import javax.persistence.*;

/**
 * Created by yzzhao on 2/22/16.
 */
@Entity
public class HelloEntity {

    private long id;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
