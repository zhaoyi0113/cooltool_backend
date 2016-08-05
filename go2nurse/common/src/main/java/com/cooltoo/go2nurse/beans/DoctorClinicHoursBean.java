package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

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
    private Date clinicHourStart;
    private Date clinicHourEnd;
    private int numberCount;

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

    public Date getClinicHourStart() {
        return clinicHourStart;
    }

    public Date getClinicHourEnd() {
        return clinicHourEnd;
    }

    public int getNumberCount() {
        return numberCount;
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

    public void setClinicHourStart(Date clinicHourStart) {
        this.clinicHourStart = clinicHourStart;
    }

    public void setClinicHourEnd(Date clinicHourEnd) {
        this.clinicHourEnd = clinicHourEnd;
    }

    public void setNumberCount(int numberCount) {
        this.numberCount = numberCount;
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
        msg.append("]");
        return msg.toString();
    }
}
