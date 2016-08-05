package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.DoctorClinicDateBean;
import com.cooltoo.go2nurse.beans.DoctorClinicHoursBean;
import com.cooltoo.go2nurse.service.DoctorClinicDateHoursService;
import com.cooltoo.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.List;

/**
 * Created by hp on 2016/8/5.
 */
@Path("/admin/doctor/clinic_date")
public class DoctorClinicDateManageAPI {

    @Autowired private DoctorClinicDateHoursService dateHoursService;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countClinicDateByDoctorId(@Context HttpServletRequest request,
                                              @QueryParam("doctor_id") @DefaultValue("0") long doctorId
    ) {
        long count = dateHoursService.countClinicDateByDoctorId(doctorId, CommonStatus.getAll());
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClinicDateByDoctorId(@Context HttpServletRequest request,
                                            @QueryParam("doctor_id") @DefaultValue("0") long doctorId,
                                            @QueryParam("index") @DefaultValue("0") int pageIndex,
                                            @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<DoctorClinicDateBean> clinicDate = dateHoursService.getClinicDateWithHoursByDoctorId(doctorId, CommonStatus.getAll(), pageIndex, sizePerPage);
        return Response.ok(clinicDate).build();
    }

    @Path("/{clinic_date_id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteClinicDateByClinicDateId(@Context HttpServletRequest request,
                                                   @PathParam("clinic_date_id") @DefaultValue("0") long clinicDateId
    ) {
        DoctorClinicDateBean clinicDate = dateHoursService.deleteClinicDateById(clinicDateId);
        return Response.ok(clinicDate).build();
    }

    @Path("/delete_hours/{clinic_hours_id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteClinicHoursByClinicHoursId(@Context HttpServletRequest request,
                                                     @PathParam("clinic_hours_id") @DefaultValue("0") long clinicHoursId
    ) {
        DoctorClinicHoursBean clinicHours = dateHoursService.deleteClinicHoursById(clinicHoursId);
        return Response.ok(clinicHours).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addClinicDate(@Context HttpServletRequest request,
                                  @FormParam("doctor_id") @DefaultValue("0") long doctorId,
                                  @FormParam("clinic_date") @DefaultValue("") String strClinicDate,
                                  @FormParam("clinic_time_start") @DefaultValue("") String clinicTimeStart,
                                  @FormParam("clinic_time_end") @DefaultValue("") String clinicTimeEnd,
                                  @FormParam("number") @DefaultValue("0") int number

    ) {
        long lClinicDate = NumberUtil.getTime(strClinicDate, NumberUtil.DATE_YYYY_MM_DD);
        Date clinicDate = null;
        if (lClinicDate>0) {
            clinicDate = new Date(lClinicDate);
        }
        DoctorClinicDateBean bean = dateHoursService.addClinicDate(doctorId, clinicDate, clinicTimeStart, clinicTimeEnd, number);
        return Response.ok(bean).build();
    }

    @Path("/add_hours")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addClinicHours(@Context HttpServletRequest request,
                                   @FormParam("clinic_date_id") @DefaultValue("0") long clinicDateId,
                                   @FormParam("clinic_time_start") @DefaultValue("") String clinicTimeStart,
                                   @FormParam("clinic_time_end") @DefaultValue("") String clinicTimeEnd,
                                   @FormParam("number") @DefaultValue("0") int number
    ) {
        DoctorClinicHoursBean bean = dateHoursService.addClinicHours(clinicDateId, clinicTimeStart, clinicTimeEnd, number);
        return Response.ok(bean).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editClinicDate(@Context HttpServletRequest request,
                                   @FormParam("clinic_date_id") @DefaultValue("0") long doctorDateId,
                                   @FormParam("clinic_date") @DefaultValue("") String strClinicDate
    ) {
        DoctorClinicDateBean bean = dateHoursService.updateClinicDate(doctorDateId, strClinicDate, null);
        return Response.ok(bean).build();
    }

    @Path("/edit/hours")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editClinicHours(@Context HttpServletRequest request,
                                    @FormParam("clinic_hours_id") @DefaultValue("0") long clinicHoursId,
                                    @FormParam("clinic_time_start") @DefaultValue("") String clinicTimeStart,
                                    @FormParam("clinic_time_end") @DefaultValue("") String clinicTimeEnd,
                                    @FormParam("number") @DefaultValue("0") int number
    ) {
        DoctorClinicHoursBean bean = dateHoursService.updateClinicHours(clinicHoursId, clinicTimeStart, clinicTimeEnd, number, null);
        return Response.ok(bean).build();
    }
}
