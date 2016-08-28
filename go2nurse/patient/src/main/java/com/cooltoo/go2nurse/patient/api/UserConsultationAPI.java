package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.UserConsultationBean;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
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
 * Created by hp on 2016/8/28.
 */
@Path("/user/consultation")
public class UserConsultationAPI {

    @Autowired private UserConsultationService userConsultationService;


    @Path("/query")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getConsultation(@Context HttpServletRequest request,
                                    @QueryParam("content") @DefaultValue("") String content,
                                    @QueryParam("index")  @DefaultValue("0")  int pageIndex,
                                    @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserConsultationBean> consultations = userConsultationService.getUserConsultation(userId, content, pageIndex, sizePerPage);
        return Response.ok(consultations).build();
    }

    @Path("/with_talks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getConsultationWithTalks(@Context HttpServletRequest request,
                                             @QueryParam("consultation_id") @DefaultValue("0") long consultationId
    ) {
        UserConsultationBean consultation = userConsultationService.getUserConsultationWithTalk(consultationId);
        return Response.ok(consultation).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response deleteConsultation(@Context HttpServletRequest request,
                                       @FormParam("consultation_id") @DefaultValue("0") long consultationId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<Long> allIds = new ArrayList<>();
        allIds.add(consultationId);
        allIds = userConsultationService.deleteConsultationByIds(userId, allIds);
        return Response.ok(allIds).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addConsultation(@Context HttpServletRequest request,
                                    @FormParam("category_id") @DefaultValue("0") long categoryId,
                                    @FormParam("nurse_id") @DefaultValue("0") long nurseId,
                                    @FormParam("patient_id") @DefaultValue("0") long patientId,
                                    @FormParam("disease_description") @DefaultValue("") String diseaseDescription,
                                    @FormParam("clinical_history") @DefaultValue("") String clinicalHistory
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long consultationId = userConsultationService.addConsultation(categoryId, nurseId, userId, patientId, diseaseDescription, clinicalHistory);
        return Response.ok(consultationId).build();
    }

    @Path("/add_image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addConsultationImage(@Context HttpServletRequest request,
                                         @FormDataParam("consultation_id") @DefaultValue("0") long consultationId,
                                         @FormDataParam("image_name") @DefaultValue("") String imageName,
                                         @FormDataParam("image") InputStream image
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Map<String, String> imageIdToUrl = userConsultationService.addConsultationImage(userId, consultationId, imageName, image);
        return Response.ok(imageIdToUrl).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response editConsultation(@Context HttpServletRequest request,
                                     @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                     @FormParam("category_id") @DefaultValue("") String strCategoryId,
                                     @FormParam("nurse_id") @DefaultValue("") String strNurseId
    ) {
        Long categoryId = VerifyUtil.isIds(strCategoryId) ? VerifyUtil.parseLongIds(strCategoryId).get(0) : null;
        Long nurseId = VerifyUtil.isIds(strNurseId) ? VerifyUtil.parseLongIds(strNurseId).get(0) : null;
        userConsultationService.updateConsultationStatus(consultationId, categoryId, nurseId, null);
        return Response.ok().build();
    }


    //=================================================================================================================
    //                                           talk service
    //=================================================================================================================

    @Path("/talk")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
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
    @LoginAuthentication(requireUserLogin = true)
    public Response addConsultationTalk(@Context HttpServletRequest request,
                                        @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                        @FormParam("nurse_id") @DefaultValue("0") long nurseId,
                                        @FormParam("talk_status") @DefaultValue("") String strTalkStatus,
                                        @FormParam("talk_content") @DefaultValue("") String talkContent
    ) {
        ConsultationTalkStatus talkStatus = ConsultationTalkStatus.parseString(strTalkStatus);
        long talkId = userConsultationService.addTalk(consultationId, nurseId, talkStatus, talkContent);
        return Response.ok(talkId).build();
    }

    @Path("/talk/add_image")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @LoginAuthentication(requireUserLogin = true)
    public Response addConsultationTalkImage(@Context HttpServletRequest request,
                                             @FormDataParam("consultation_id") @DefaultValue("0") long consultationId,
                                             @FormDataParam("talk_id") @DefaultValue("0") long talkId,
                                             @FormDataParam("image_name") @DefaultValue("") String imageName,
                                             @FormDataParam("image") InputStream image
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Map<String, String> imageIdToUrl = userConsultationService.addTalkImage(userId, consultationId, talkId, imageName, image);
        return Response.ok(imageIdToUrl).build();
    }
}
