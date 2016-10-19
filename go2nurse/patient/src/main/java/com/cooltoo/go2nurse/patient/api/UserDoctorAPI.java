package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.DoctorAppointmentBean;
import com.cooltoo.go2nurse.beans.DoctorBean;
import com.cooltoo.go2nurse.beans.DoctorClinicDateBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.DoctorAppointmentService;
import com.cooltoo.go2nurse.service.DoctorClinicDateHoursService;
import com.cooltoo.go2nurse.service.DoctorService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/7/25.
 */
@Path("/user/doctor")
public class UserDoctorAPI {

    @Autowired private DoctorService doctorService;
    @Autowired private DoctorClinicDateHoursService clinicDateHoursService;
    @Autowired private DoctorAppointmentService doctorAppointmentService;

    @Path("/count/by_hospital_department")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getDoctorByHospitalDepartment(@Context HttpServletRequest request,
                                                  @QueryParam("hospital_id") @DefaultValue("") String strHospitalId,
                                                  @QueryParam("department_id") @DefaultValue("") String strDepartmentId,
                                                  @QueryParam("index") @DefaultValue("0") int pageIndex,
                                                  @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        Integer hospitalId = !VerifyUtil.isIds(strHospitalId) ? null : VerifyUtil.parseIntIds(strHospitalId).get(0);
        Integer departmentId = !VerifyUtil.isIds(strDepartmentId) ? null : VerifyUtil.parseIntIds(strDepartmentId).get(0);
        List<DoctorBean> doctors = doctorService.getDoctor(null==departmentId||0==departmentId, hospitalId, departmentId, statuses, pageIndex, sizePerPage);
        return Response.ok(doctors).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getDoctorById(@Context HttpServletRequest request,
                                  @QueryParam("doctor_id") @DefaultValue("0") long doctorId
    ) {
        DoctorBean doctor = doctorService.getDoctorById(doctorId);
        return Response.ok(doctor).build();
    }

    @Path("/clinic_date")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getDoctorClinicDate(@Context HttpServletRequest request,
                                        @QueryParam("doctor_id") @DefaultValue("0") long doctorId,
                                        @QueryParam("flag") @DefaultValue("0") int flag
    ) {
        List<DoctorClinicDateBean> monthClinicDates = clinicDateHoursService.getClinicDateWithHours(doctorId, flag, CommonStatus.getAll());
        return Response.ok(monthClinicDates).build();
    }

    @Path("/appointment")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getAppointmentById(@Context HttpServletRequest request,
                                       @QueryParam("appointment_id") @DefaultValue("0") long appointmentId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<DoctorAppointmentBean> appointment = doctorAppointmentService.getDoctorAppointment(true, userId, appointmentId);
        return Response.ok(appointment.get(0)).build();
    }

    @Path("/appointment")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response appointOneDoctor(@Context HttpServletRequest request,
                                     @FormParam("patient_id") @DefaultValue("0") long patientId,
                                     @FormParam("clinic_hour_id") @DefaultValue("0") long clinicHourId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        DoctorAppointmentBean appointment = doctorAppointmentService.appointDoctor(userId, patientId, clinicHourId);
        return Response.ok(appointment).build();
    }

    @Path("/appointment/modify")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response cancelAppointment(@Context HttpServletRequest request,
                                      @FormParam("appointment_id") @DefaultValue("0") long appointmentId,
                                      @FormParam("patient_id") @DefaultValue("0") long patientId,
                                      @FormParam("clinic_hour_id") @DefaultValue("0") long clinicHourId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        DoctorAppointmentBean appointment = doctorAppointmentService.modifyAppointment(appointmentId, userId, patientId, clinicHourId);
        return Response.ok(appointment).build();
    }

    @Path("/appointment/cancel")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response cancelAppointment(@Context HttpServletRequest request,
                                      @FormParam("appointment_id") @DefaultValue("0") long appointmentId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        DoctorAppointmentBean appointment = doctorAppointmentService.cancelAppointment(userId, 0, appointmentId);
        return Response.ok(appointment).build();
    }

    @Path("/appointment/complete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response completeAppointment(@Context HttpServletRequest request,
                                        @FormParam("appointment_id") @DefaultValue("0") long appointmentId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        DoctorAppointmentBean appointment = doctorAppointmentService.completeAppointment(userId, 0, appointmentId);
        return Response.ok(appointment).build();
    }

    @Path("/appointment/score")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response scoreAppointment(@Context HttpServletRequest request,
                                     @FormParam("appointment_id") @DefaultValue("0") long appointmentId,
                                     @FormParam("score") @DefaultValue("0") float score
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        DoctorAppointmentBean appointment = doctorAppointmentService.scoreAppointment(userId, 0, appointmentId, score);
        return Response.ok(appointment).build();
    }

    @Path("/list/{hospital_id}/{department_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByHospitalDepartment(@Context HttpServletRequest request,
                                            @PathParam("hospital_id") @DefaultValue("") String strHospitalId,
                                            @PathParam("department_id") @DefaultValue("") String strDepartmentId
    ) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        statuses.add(CommonStatus.DISABLED);
        Integer hospitalId = !VerifyUtil.isIds(strHospitalId) ? null : VerifyUtil.parseIntIds(strHospitalId).get(0);
        Integer departmentId = !VerifyUtil.isIds(strDepartmentId) ? null : VerifyUtil.parseIntIds(strDepartmentId).get(0);

        int countDoctor = (int) doctorService.countDoctor(null==departmentId||0==departmentId, hospitalId, departmentId, statuses);
        List<DoctorBean> doctors = doctorService.getDoctor(null==departmentId||0==departmentId, hospitalId, departmentId, statuses, 0, countDoctor);
        return Response.ok(doctors).build();
    }
}
