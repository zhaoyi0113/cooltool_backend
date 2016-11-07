package com.cooltoo.nurse360.admin.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.NurseVisitPatientBean;
import com.cooltoo.go2nurse.beans.NurseVisitPatientServiceItemBean;
import com.cooltoo.go2nurse.service.NurseVisitPatientService;
import com.cooltoo.go2nurse.service.NurseVisitPatientServiceItemService;
import com.cooltoo.util.JSONUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/7.
 */
@Path("/admin/nurse/visit/patient")
public class NurseVisitPatientManageAPI {

    @Autowired private NurseVisitPatientService visitPatientService;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countVisitPatientRecord(@Context HttpServletRequest request,
                                            @QueryParam("nurse_id") @DefaultValue("-1") long nurseId,
                                            @QueryParam("user_id") @DefaultValue("-1") long userId,
                                            @QueryParam("patient_id") @DefaultValue("-1") long patientId,
                                            @QueryParam("record_content_like") @DefaultValue("") String contentLike
    ) {

        long visits = visitPatientService.countVisitRecordByCondition(
                0>userId ? null : userId,
                0>patientId ? null : patientId,
                0>nurseId ? null : nurseId,
                contentLike);
        return Response.ok(visits).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVisitPatientRecord(@Context HttpServletRequest request,
                                          @QueryParam("nurse_id") @DefaultValue("-1") long nurseId,
                                          @QueryParam("user_id") @DefaultValue("-1") long userId,
                                          @QueryParam("patient_id") @DefaultValue("-1") long patientId,
                                          @QueryParam("record_content_like") @DefaultValue("") String contentLike,
                                          @QueryParam("index") @DefaultValue("0") int pageIndex,
                                          @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<NurseVisitPatientBean> visits = visitPatientService.getVisitRecordByCondition(
                0>userId ? null : userId,
                0>patientId ? null : patientId,
                0>nurseId ? null : nurseId,
                contentLike,
                pageIndex, sizePerPage);
        return Response.ok(visits).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response setVisitPatientRecordStatusToDelete(@Context HttpServletRequest request,
                                                        @FormParam("visit_record_id") @DefaultValue("0") long visitRecordId
    ) {
        List<Long> updateIds = visitPatientService.setDeleteStatusVisitRecordByIds(null, Arrays.asList(new Long[]{visitRecordId}));
        return Response.ok(updateIds).build();
    }
}
