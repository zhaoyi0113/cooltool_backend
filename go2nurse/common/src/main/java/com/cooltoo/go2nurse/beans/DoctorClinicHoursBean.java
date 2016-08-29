package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import java.sql.Time;
import java.util.Date;

/**
 * Created by hp on 2016/8/4.
 */
public class DoctorClinicHoursBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long doctorId;
    private long clinicDateId;
    private Time clinicHourStart;
    private Time clinicHourEnd;
    private int numberCount;
    private long numberUsed;

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public long getClinicDateId() {
        return clinicDateId;
    }

    public Time getClinicHourStart() {
        return clinicHourStart;
    }

    public Time getClinicHourEnd() {
        return clinicHourEnd;
    }

    public int getNumberCount() {
        return numberCount;
    }

    public long getNumberUsed() {
        return numberUsed;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public void setClinicDateId(long clinicDateId) {
        this.clinicDateId = clinicDateId;
    }

    public void setClinicHourStart(Time clinicHourStart) {
        this.clinicHourStart = clinicHourStart;
    }

    public void setClinicHourEnd(Time clinicHourEnd) {
        this.clinicHourEnd = clinicHourEnd;
    }

    public void setNumberCount(int numberCount) {
        this.numberCount = numberCount;
    }

    public void setNumberUsed(long numberUsed) {
        this.numberUsed = numberUsed;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", doctorId=").append(doctorId);
        msg.append(", clinicDateId=").append(clinicDateId);
        msg.append(", clinicHourStart=").append(clinicHourStart);
        msg.append(", clinicHourEnd=").append(clinicHourEnd);
        msg.append(", numberCount=").append(numberCount);
        msg.append(", numberUsed=").append(numberUsed);
        msg.append("]");
        return msg.toString();
    }
}
