package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/11/06.
 */
@Entity
@Table(name = "go2nurse_nurse_visit_patient_photo")
public class ImageInVisitPatientEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long nurseVisitPatientId;
    private long imageId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "nurse_visit_patient_id")
    public long getNurseVisitPatientId() {
        return nurseVisitPatientId;
    }

    @Column(name = "image_id")
    public long getImageId() {
        return imageId;
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

    public void setNurseVisitPatientId(long nurseVisitPatientId) {
        this.nurseVisitPatientId = nurseVisitPatientId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }
}
