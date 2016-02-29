package com.cooltoo.entities;

import javax.persistence.*;

/**
 * Created by yzzhao on 2/29/16.
 */
@Entity
@Table(name = "patient_badge")
public class PatientBadgeEntity {

    private int id;

    private int patientId;

    private int badgeId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "patient_id")
    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    @Column(name = "badge_id")
    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }
}
