package com.cooltoo.nurse360.admin.api;

import com.cooltoo.go2nurse.beans.NursePatientFollowUpBean;
import com.cooltoo.go2nurse.service.NursePatientFollowUpService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/8.
 */
@Path("/admin/nurse/follow-up/patient")
public class NursePatientFollowUpManageAPI {

    @Autowired private NursePatientFollowUpService patientFollowUpService;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countPatientFollowUp(@Context HttpServletRequest request,
                                         @QueryParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                         @QueryParam("department_id") @DefaultValue("-1") int departmentId,
                                         @QueryParam("nurse_id") @DefaultValue("-1") long nurseId,
                                         @QueryParam("user_id") @DefaultValue("-1") long userId,
                                         @QueryParam("patient_id") @DefaultValue("-1") long patientId
    ) {

        long visits = patientFollowUpService.countPatientFollowUp(
                0>hospitalId ? null : hospitalId,
                0>departmentId ? null : departmentId,
                0>userId ? null : userId,
                0>patientId ? null : patientId,
                0>nurseId ? null : nurseId
                );
        return Response.ok(visits).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientFollowUp(@Context HttpServletRequest request,
                                       @QueryParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                       @QueryParam("department_id") @DefaultValue("-1") int departmentId,
                                       @QueryParam("nurse_id") @DefaultValue("-1") long nurseId,
                                       @QueryParam("user_id") @DefaultValue("-1") long userId,
                                       @QueryParam("patient_id") @DefaultValue("-1") long patientId,
                                       @QueryParam("index") @DefaultValue("0") int pageIndex,
                                       @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<NursePatientFollowUpBean> visits = patientFollowUpService.getPatientFollowUp(
                0>hospitalId ? null : hospitalId,
                0>departmentId ? null : departmentId,
                0>userId ? null : userId,
                0>patientId ? null : patientId,
                0>nurseId ? null : nurseId,
                pageIndex, sizePerPage);
        return Response.ok(visits).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response setPatientFollowUpStatusToDelete(@Context HttpServletRequest request,
                                                     @FormParam("follow_up_id") @DefaultValue("0") long followUpId
    ) {
        List<Long> updateIds = patientFollowUpService.setDeleteStatusPatientFollowUpByIds(null, Arrays.asList(new Long[]{followUpId}));
        return Response.ok(updateIds).build();
    }
}
