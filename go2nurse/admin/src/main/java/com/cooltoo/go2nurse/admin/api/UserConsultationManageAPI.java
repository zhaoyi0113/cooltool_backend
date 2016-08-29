package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.UserConsultationBean;
import com.cooltoo.go2nurse.service.UserConsultationService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/8/29.
 */
@Path("/admin/consultation")
public class UserConsultationManageAPI {

    @Autowired private UserConsultationService userConsultationService;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConsultation(@Context HttpServletRequest request,
                                    @QueryParam("user_id") @DefaultValue("") String strUserId,
                                    @QueryParam("patient_id") @DefaultValue("") String strPatientId,
                                    @QueryParam("nurse_id") @DefaultValue("") String strNurseId,
                                    @QueryParam("category_id") @DefaultValue("") String strCategoryId,
                                    @QueryParam("content") @DefaultValue("") String content
    ) {
        Long userId = VerifyUtil.isIds(strUserId) ? VerifyUtil.parseLongIds(strUserId).get(0) : null;
        Long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : null;
        Long nurseId = VerifyUtil.isIds(strNurseId) ? VerifyUtil.parseLongIds(strNurseId).get(0) : null;
        Long categoryId = VerifyUtil.isIds(strCategoryId) ? VerifyUtil.parseLongIds(strCategoryId).get(0) : null;
        long count = userConsultationService.countUserConsultationByCondition(userId, patientId, nurseId, categoryId, content);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConsultation(@Context HttpServletRequest request,
                                    @QueryParam("user_id") @DefaultValue("") String strUserId,
                                    @QueryParam("patient_id") @DefaultValue("") String strPatientId,
                                    @QueryParam("nurse_id") @DefaultValue("") String strNurseId,
                                    @QueryParam("category_id") @DefaultValue("") String strCategoryId,
                                    @QueryParam("content") @DefaultValue("") String content,
                                    @QueryParam("index")  @DefaultValue("0")  int pageIndex,
                                    @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        Long userId = VerifyUtil.isIds(strUserId) ? VerifyUtil.parseLongIds(strUserId).get(0) : null;
        Long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : null;
        Long nurseId = VerifyUtil.isIds(strNurseId) ? VerifyUtil.parseLongIds(strNurseId).get(0) : null;
        Long categoryId = VerifyUtil.isIds(strCategoryId) ? VerifyUtil.parseLongIds(strCategoryId).get(0) : null;
        List<UserConsultationBean> consultations = userConsultationService.getUserConsultationByCondition(userId, patientId, nurseId, categoryId, content, pageIndex, sizePerPage);
        return Response.ok(consultations).build();
    }

    @Path("/with_talks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConsultationWithTalks(@Context HttpServletRequest request,
                                             @QueryParam("consultation_id") @DefaultValue("0") long consultationId
    ) {
        UserConsultationBean consultation = userConsultationService.getUserConsultationWithTalk(consultationId);
        return Response.ok(consultation).build();
    }

    @Path("/edit/status")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editConsultation(@Context HttpServletRequest request,
                                     @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                     @FormParam("status") @DefaultValue("") String strStatus
    ) {
        CommonStatus status = CommonStatus.parseString(strStatus);
        userConsultationService.updateConsultationStatus(null, null, null, status);
        return Response.ok().build();
    }
}
