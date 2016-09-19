package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.ConsultationCategoryBean;
import com.cooltoo.go2nurse.beans.UserConsultationBean;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.service.ConsultationCategoryService;
import com.cooltoo.go2nurse.service.UserConsultationService;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/8/28.
 */
@Path("/user/consultation")
public class UserConsultationAPI {

    private static final Logger logger = LoggerFactory.getLogger(UserConsultationAPI.class);

    @Autowired private UserConsultationService userConsultationService;
    @Autowired private ConsultationCategoryService categoryService;
    @Autowired private WeChatService weChatService;

    //=================================================================================================================
    //                                           consultation category service
    //=================================================================================================================
    @Path("/category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getServiceCategory(@Context HttpServletRequest request) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        List<ConsultationCategoryBean> topCategories = categoryService.getCategoryByStatus(statuses);
        return Response.ok(topCategories).build();
    }

    //=================================================================================================================
    //                                           consultation service
    //=================================================================================================================
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

    @Path("/query/by_nurse_id")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getConsultation(@Context HttpServletRequest request,
                                    @PathParam("nurse_id") @DefaultValue("0") long nurseId,
                                    @PathParam("index")  @DefaultValue("0")  int pageIndex,
                                    @PathParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserConsultationBean> consultations = userConsultationService.getUserConsultation(userId, nurseId, pageIndex, sizePerPage);
        return Response.ok(consultations).build();
    }

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
        Map<String, Long> retValue = new HashMap<>();
        retValue.put("id", consultationId);
        return Response.ok(retValue).build();
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

    @Path("/add_image/wx")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addConsultationImageFromWX(@Context HttpServletRequest request,
                                         @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                         @FormParam("media_id") @DefaultValue("") String mediaId
    ) {
        logger.info("download image from wx with the media id "+mediaId+" consultant id "+consultationId);
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        InputStream inputStream = weChatService.downloadImageFromWX(mediaId);
        Map<String, String> imageIdToUrl = userConsultationService.addConsultationImage(userId, consultationId, "", inputStream);
        return Response.ok(imageIdToUrl).build();
    }

    @Path("/edit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response editConsultation(@Context HttpServletRequest request,
                                     @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                     @FormParam("category_id") @DefaultValue("") String strCategoryId,
                                     @FormParam("nurse_id") @DefaultValue("") String strNurseId,
                                     @FormParam("completed") @DefaultValue("") String strCompleted/* YES , NO */
    ) {
        Long categoryId = VerifyUtil.isIds(strCategoryId) ? VerifyUtil.parseLongIds(strCategoryId).get(0) : null;
        Long nurseId = VerifyUtil.isIds(strNurseId) ? VerifyUtil.parseLongIds(strNurseId).get(0) : null;
        YesNoEnum completed = YesNoEnum.parseString(strCompleted);
        userConsultationService.updateConsultationStatus(consultationId, categoryId, nurseId, null, completed);
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
        Map<String, Long> returnValue = new HashMap<>();
        returnValue.put("talk_id", talkId);
        return Response.ok(returnValue).build();
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
