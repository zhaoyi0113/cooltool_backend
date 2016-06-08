package com.cooltoo.go2nurse.entities;

import javax.persistence.*;

/**
 * Created by yzzhao on 2/29/16.
 */
@Entity
@Table(name = "go2nurse_patient_badge")
public class PatientBadgeEntity {

    private int id;

    private long patientId;

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
    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    @Column(name = "badge_id")
    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("patientId=").append(patientId).append(" , ");
        msg.append("badgeId=").append(badgeId);
        msg.append("]");
        return msg.toString();
    }
}
