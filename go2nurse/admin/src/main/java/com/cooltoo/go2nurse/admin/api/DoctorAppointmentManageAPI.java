package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.DoctorAppointmentBean;
import com.cooltoo.go2nurse.service.DoctorAppointmentService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.List;

/**
 * Created by hp on 2016/8/19.
 */
@Path("/admin/doctor/appointment")
public class DoctorAppointmentManageAPI {

    @Autowired
    private DoctorAppointmentService doctorAppointmentService;

    @Path("/by_id")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAppointmentById(@Context HttpServletRequest request,
                                       @QueryParam("appointment_id") @DefaultValue("0") long appointmentId
    ) {
        List<DoctorAppointmentBean> appointment = doctorAppointmentService.getDoctorAppointment(false, 0L, appointmentId);
        return Response.ok(appointment.get(0)).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countAppointmentByConditions(@Context HttpServletRequest request,
                                                 @QueryParam("hospital_id") @DefaultValue("") String strHospitalId,
                                                 @QueryParam("department_id") @DefaultValue("") String strDepartmentId,
                                                 @QueryParam("doctor_id") @DefaultValue("") String strDoctorId,
                                                 @QueryParam("clinic_date_id") @DefaultValue("") String strClinicDateId,
                                                 @QueryParam("clinic_hour_id") @DefaultValue("") String strClinicHourId,
                                                 @QueryParam("start_date") @DefaultValue("") String strStartDate,
                                                 @QueryParam("end_date") @DefaultValue("") String strEndDate
    ) {
        Integer hospitalId = !VerifyUtil.isIds(strHospitalId) ? null : VerifyUtil.parseIntIds(strHospitalId).get(0);
        Integer departmentId = !VerifyUtil.isIds(strDepartmentId) ? null : VerifyUtil.parseIntIds(strDepartmentId).get(0);
        Long doctorId = !VerifyUtil.isIds(strDoctorId) ? null : VerifyUtil.parseLongIds(strDoctorId).get(0);
        Long clinicDateId = !VerifyUtil.isIds(strClinicDateId) ? null : VerifyUtil.parseLongIds(strClinicDateId).get(0);
        Long clinicHourId = !VerifyUtil.isIds(strClinicHourId) ? null : VerifyUtil.parseLongIds(strClinicHourId).get(0);
        long lStartDate = NumberUtil.getTime(strStartDate, NumberUtil.DATE_YYYY_MM_DD);
        long lEndDate = NumberUtil.getTime(strEndDate, NumberUtil.DATE_YYYY_MM_DD);
        Date startDate = lStartDate<0 ? null : new Date(lStartDate);
        Date endDate = lEndDate<0 ? null : new Date(lEndDate);
        long count = doctorAppointmentService.countDoctorAppointment(hospitalId, departmentId, doctorId, clinicDateId, clinicHourId, startDate, endDate);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAppointmentByConditions(@Context HttpServletRequest request,
                                               @QueryParam("hospital_id") @DefaultValue("") String strHospitalId,
                                               @QueryParam("department_id") @DefaultValue("") String strDepartmentId,
                                               @QueryParam("doctor_id") @DefaultValue("") String strDoctorId,
                                               @QueryParam("clinic_date_id") @DefaultValue("") String strClinicDateId,
                                               @QueryParam("clinic_hour_id") @DefaultValue("") String strClinicHourId,
                                               @QueryParam("start_date") @DefaultValue("") String strStartDate,
                                               @QueryParam("end_date") @DefaultValue("") String strEndDate,
                                               @QueryParam("index") @DefaultValue("0") int index,
                                               @QueryParam("number") @DefaultValue("10") int number
    ) {
        Integer hospitalId = !VerifyUtil.isIds(strHospitalId) ? null : VerifyUtil.parseIntIds(strHospitalId).get(0);
        Integer departmentId = !VerifyUtil.isIds(strDepartmentId) ? null : VerifyUtil.parseIntIds(strDepartmentId).get(0);
        Long doctorId = !VerifyUtil.isIds(strDoctorId) ? null : VerifyUtil.parseLongIds(strDoctorId).get(0);
        Long clinicDateId = !VerifyUtil.isIds(strClinicDateId) ? null : VerifyUtil.parseLongIds(strClinicDateId).get(0);
        Long clinicHourId = !VerifyUtil.isIds(strClinicHourId) ? null : VerifyUtil.parseLongIds(strClinicHourId).get(0);
        long lStartDate = NumberUtil.getTime(strStartDate, NumberUtil.DATE_YYYY_MM_DD);
        long lEndDate = NumberUtil.getTime(strEndDate, NumberUtil.DATE_YYYY_MM_DD);
        Date startDate = lStartDate<0 ? null : new Date(lStartDate);
        Date endDate = lEndDate<0 ? null : new Date(lEndDate);
        List<DoctorAppointmentBean> appointments = doctorAppointmentService.findDoctorAppointment(hospitalId, departmentId, doctorId, clinicDateId, clinicHourId, startDate, endDate, index, number);
        return Response.ok(appointments).build();
    }

    @Path("/cancel")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelAppointment(@Context HttpServletRequest request,
                                      @FormParam("appointment_id") @DefaultValue("0") long appointmentId
    ) {
        DoctorAppointmentBean appointment = doctorAppointmentService.cancelAppointment(0, 0, appointmentId);
        return Response.ok(appointment).build();
    }

    @Path("/complete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeAppointment(@Context HttpServletRequest request,
                                        @FormParam("appointment_id") @DefaultValue("0") long appointmentId
    ) {
        DoctorAppointmentBean appointment = doctorAppointmentService.completeAppointment(0, 0, appointmentId);
        return Response.ok(appointment).build();
    }
}
