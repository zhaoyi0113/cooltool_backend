package com.cooltoo.go2nurse.beans;

import com.cooltoo.constants.CommonStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/8/4.
 */
public class DoctorClinicDateBean {

    private long id;
    private Date time;
    private CommonStatus status;
    private long doctorId;
    private Date clinicDate;
    private List<DoctorClinicHoursBean> clinicHours = new ArrayList<>();

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

    public Date getClinicDate() {
        return clinicDate;
    }

    public boolean existsClinicHours() {
        return !(null==clinicHours || clinicHours.isEmpty());
    }

    public List<DoctorClinicHoursBean> getClinicHours() {
        return clinicHours;
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

    public void setClinicDate(Date clinicDate) {
        this.clinicDate = clinicDate;
    }

    public void setClinicHours(List<DoctorClinicHoursBean> clinicHours) {
        this.clinicHours = clinicHours;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DoctorClinicDateBean) {
            return ((DoctorClinicDateBean) obj).getId() == id;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", doctorId=").append(doctorId);
        msg.append(", clinicDate=").append(clinicDate);
        msg.append(", clinicHours=").append(clinicHours);
        msg.append("]");
        return msg.toString();
    }
}
