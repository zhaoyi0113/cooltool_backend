package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Time;

/**
 * Created by hp on 2016/8/8.
 */

public class DoctorAppintmentEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private String orderNo;
    private int hospitalId;
    private String hospitalJson;
    private int departmentId;
    private String departmentJson;
    private long doctorId;
    private String doctorJson;
    private long clinicDateId;
    private Date clinicDate;
    private long clinicHoursId;
    private Time clinicHoursStart;
    private Time clinicHoursEnd;
    private long patientId;
    private String patientJson;

}
