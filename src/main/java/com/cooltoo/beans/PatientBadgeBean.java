package com.cooltoo.beans;

/**
 * Created by lg380357 on 2016/3/3.
 */
public class PatientBadgeBean {

    private int id;

    private long patientId;

    private int badgeId;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public long getPatientId() {
        return patientId;
    }
    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }
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
