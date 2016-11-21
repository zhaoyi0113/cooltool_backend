package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.ConsultationCategoryBean;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpRecordBean;
import com.cooltoo.go2nurse.beans.UserConsultationBean;
import com.cooltoo.go2nurse.constants.ConsultationCreator;
import com.cooltoo.go2nurse.constants.ConsultationReason;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.constants.PatientFollowUpType;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.service.ConsultationCategoryService;
import com.cooltoo.go2nurse.service.NursePatientFollowUpRecordService;
import com.cooltoo.go2nurse.service.notification.NotifierForAllModule;
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
    @Autowired private NursePatientFollowUpRecordService patientFollowRecordService;
    @Autowired private NotifierForAllModule notifierForAllModule;

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
                                    @QueryParam("index") @DefaultValue("0") int pageIndex,
                                    @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserConsultationBean> consultations = userConsultationService.getUserConsultation(false, userId, null, null, content, ConsultationReason.CONSULTATION, pageIndex, sizePerPage, ConsultationTalkStatus.USER_SPEAK);
        return Response.ok(consultations).build();
    }

    @Path("/query/by_nurse_id")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getConsultation(@Context HttpServletRequest request,
                                    @QueryParam("nurse_id") @DefaultValue("0") long nurseId,
                                    @QueryParam("index") @DefaultValue("0") int pageIndex,
                                    @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserConsultationBean> consultations = userConsultationService.getUserConsultation(userId, nurseId, ConsultationReason.CONSULTATION, pageIndex, sizePerPage, ConsultationTalkStatus.USER_SPEAK);
        return Response.ok(consultations).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getConsultationWithTalks(@Context HttpServletRequest request,
                                             @QueryParam("consultation_id") @DefaultValue("0") long consultationId
    ) {
        UserConsultationBean consultation = userConsultationService.getUserConsultationWithTalk(consultationId, ConsultationTalkStatus.USER_SPEAK);
        return Response.ok(consultation).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response deleteConsultation(@Context HttpServletRequest request,
                                       @FormParam("consultation_id") @DefaultValue("0") long consultationId
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
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
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long consultationId = userConsultationService.addConsultation(
                categoryId, nurseId, userId, patientId,
                diseaseDescription, clinicalHistory,
                ConsultationCreator.USER,
                ConsultationReason.CONSULTATION
        );
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
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Map<String, String> imageIdToUrl = userConsultationService.addConsultationImage(userId, 0, consultationId, imageName, image);
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
        logger.info("download image from wx with the media id " + mediaId + " consultant id " + consultationId);
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        String accessToken = (String) request.getAttribute(ContextKeys.USER_ACCESS_TOKEN);
        InputStream inputStream = weChatService.downloadImageFromWX(accessToken, mediaId);
        Map<String, String> imageIdToUrl = userConsultationService.addConsultationImage(userId, 0, consultationId, "", inputStream);
        return Response.ok(imageIdToUrl).build();
    }

    @Path("/edit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response editConsultation(@Context HttpServletRequest request,
                                     @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                     @FormParam("category_id") @DefaultValue("") String strCategoryId,
                                     @FormParam("nurse_id") @DefaultValue("") String strNurseId, /* not used */
                                     @FormParam("completed") @DefaultValue("") String strCompleted/* YES , NO */
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Long categoryId = VerifyUtil.isIds(strCategoryId) ? VerifyUtil.parseLongIds(strCategoryId).get(0) : null;
        Long nurseId = VerifyUtil.isIds(strNurseId) ? VerifyUtil.parseLongIds(strNurseId).get(0) : null;
        YesNoEnum completed = YesNoEnum.parseString(strCompleted);
        UserConsultationBean bean = userConsultationService.updateConsultationStatus(userId, consultationId, categoryId, nurseId, null, completed);
        return Response.ok(bean).build();
    }

    @Path("/score")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response scoreConsultation(@Context HttpServletRequest request,
                                      @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                      @FormParam("nurse_id") @DefaultValue("0") long nurseId,
                                      @FormParam("score") @DefaultValue("0") float score
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserConsultationBean bean = userConsultationService.scoreConsultation(userId, nurseId, consultationId, score);
        return Response.ok(bean).build();
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
        UserConsultationBean consultation = userConsultationService.getUserConsultation(consultationId, null);
        long followUpRecordId = 0;
        if (ConsultationReason.PATIENT_FOLLOW_UP.equals(consultation.getReason())) {
            List<NursePatientFollowUpRecordBean> followUpRecords = patientFollowRecordService.getPatientFollowUpRecord(CommonStatus.ENABLED, PatientFollowUpType.CONSULTATION, consultationId);
            for (NursePatientFollowUpRecordBean tmp : followUpRecords) {
                patientFollowRecordService.updatePatientFollowUpRecordById(tmp.getId(), YesNoEnum.YES, YesNoEnum.NO, null);
                followUpRecordId = tmp.getId();
            }
        }
        Map<String, Long> talkReturn = userConsultationService.addTalk(consultationId, nurseId, talkStatus, talkContent);


        Long consultationNurseId = talkReturn.get(UserConsultationService.NURSE_ID);
        if (consultationNurseId>0) {
            if (!ConsultationReason.PATIENT_FOLLOW_UP.equals(consultation.getReason())) {
                notifierForAllModule.followUpAlertToNurse(
                        PatientFollowUpType.CONSULTATION,
                        consultationNurseId,
                        followUpRecordId,
                        talkStatus.toString(),
                        talkContent);
            }
            else {
                notifierForAllModule.consultationAlertToNurse(consultationNurseId, consultationId, talkStatus, talkContent);
            }
        }


        Map<String, Long> returnValue = new HashMap<>();
        Long talkId = talkReturn.get(UserConsultationService.TALK_ID);
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
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Map<String, String> imageIdToUrl = userConsultationService.addTalkImage(userId, consultationId, talkId, imageName, image);
        return Response.ok(imageIdToUrl).build();
    }

    @Path("/talk/add_image/wx")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addConsultationTalkImageFromWX(@Context HttpServletRequest request,
                                                   @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                                   @FormParam("talk_id") @DefaultValue("0") long talkId,
                                                   @FormParam("media_id") @DefaultValue("") String mediaId
    ) {
        logger.info("download image from wx with the media id " + mediaId + " consultant id " + consultationId);
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        String userAccessToken = (String)request.getAttribute(ContextKeys.USER_ACCESS_TOKEN);
        InputStream inputStream = weChatService.downloadImageFromWX(userAccessToken, mediaId);
        Map<String, String> imageIdToUrl = userConsultationService.addTalkImage(userId, consultationId, talkId, "", inputStream);
        return Response.ok(imageIdToUrl).build();
    }

    @Path("/talk/reading_status")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addConsultationTalkImage(@Context HttpServletRequest request,
                                             @FormParam("consultation_id") @DefaultValue("0") long consultationId
    ) {
        consultationId = userConsultationService.updateConsultationUnreadTalkStatusToRead(consultationId, ConsultationTalkStatus.USER_SPEAK);

        Map<String, Long> retValue = new HashMap<>();
        retValue.put("consultation_id", consultationId);
        return Response.ok(retValue).build();
    }
}
