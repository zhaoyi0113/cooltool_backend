package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 2016/10/23.
 */
@Entity
@Table(name = "go2nurse_image_in_case")
public class ImageInCaseEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private long casebookId;
    private long caseId;
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

    @Column(name = "casebook_id")
    public long getCasebookId() {
        return casebookId;
    }

    @Column(name = "case_id")
    public long getCaseId() {
        return caseId;
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

    public void setCasebookId(long casebookId) {
        this.casebookId = casebookId;
    }

    public void setCaseId(long caseId) {
        this.caseId = caseId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }
}
