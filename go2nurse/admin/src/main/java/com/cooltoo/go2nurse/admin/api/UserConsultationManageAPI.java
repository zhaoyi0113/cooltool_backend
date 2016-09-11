package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.UserConsultationBean;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.service.UserConsultationService;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    //=================================================================================================================
    //                                           talk service
    //=================================================================================================================
    @Path("/talk")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteConsultationTalk(@Context HttpServletRequest request,
                                           @FormParam("talk_id") @DefaultValue("0") long talkId
    ) {
        List<Long> allIds = new ArrayList<>();
        allIds.add(talkId);
        allIds = userConsultationService.deleteTalk(allIds);
        return Response.ok(allIds).build();
    }

    @Path("/talk")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addConsultationTalk(@Context HttpServletRequest request,
                                        @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                        @FormParam("talk_content") @DefaultValue("") String talkContent
    ) {
        long talkId = userConsultationService.addTalk(consultationId, 0, ConsultationTalkStatus.ADMIN_SPEAK, talkContent);
        return Response.ok(talkId).build();
    }

    @Path("/talk/add_image")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addConsultationTalkImage(@Context HttpServletRequest request,
                                             @FormDataParam("consultation_id") @DefaultValue("0") long consultationId,
                                             @FormDataParam("talk_id") @DefaultValue("0") long talkId,
                                             @FormDataParam("image_name") @DefaultValue("") String imageName,
                                             @FormDataParam("image") InputStream image
    ) {
        Map<String, String> imageIdToUrl = userConsultationService.addTalkImage(0, consultationId, talkId, imageName, image);
        return Response.ok(imageIdToUrl).build();
    }
}
