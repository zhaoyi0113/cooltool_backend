package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.go2nurse.beans.ConsultationCategoryBean;
import com.cooltoo.go2nurse.beans.UserConsultationBean;
import com.cooltoo.go2nurse.beans.UserConsultationTalkBean;
import com.cooltoo.go2nurse.constants.ConsultationCreator;
import com.cooltoo.go2nurse.constants.ConsultationReason;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.service.ConsultationCategoryService;
import com.cooltoo.go2nurse.service.notification.NotifierForAllModule;
import com.cooltoo.go2nurse.service.UserConsultationService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.services.NurseExtensionService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/10/23.
 */
@Path("/nurse/consultation")
public class NurseConsultationAPI {

    @Autowired private ConsultationCategoryService categoryService;
    @Autowired private UserConsultationService userConsultationService;
    @Autowired private NurseExtensionService nurseExtensionService;
    @Autowired private NotifierForAllModule notifierForAllModule;

    //=================================================================================================================
    //                                           consultation category service
    //=================================================================================================================
    @Path("/category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getServiceCategory(@Context HttpServletRequest request) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        List<ConsultationCategoryBean> topCategories = categoryService.getCategoryByStatus(statuses);
        return Response.ok(topCategories).build();
    }

    //=================================================================================================================
    //                                           consultation service
    //=================================================================================================================
    @Path("/content_like")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getConsultation(@Context HttpServletRequest request,
                                    @QueryParam("all_consultation") @DefaultValue("NO") String searchAllConsultation/* YES, NO */,
                                    @QueryParam("content") @DefaultValue("") String content,
                                    @QueryParam("category_id") @DefaultValue("0") long lCategoryId,
                                    @QueryParam("index") @DefaultValue("0") int pageIndex,
                                    @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        YesNoEnum allConsultation = YesNoEnum.parseString(searchAllConsultation);
        Long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        nurseId = YesNoEnum.YES.equals(allConsultation) ? null : nurseId;
        Long categoryId = 0==lCategoryId ? null : new Long(lCategoryId);

        List<UserConsultationBean> consultations = userConsultationService.getUserConsultation(
                YesNoEnum.YES.equals(allConsultation),
                null,
                nurseId,
                categoryId,
                content,
                ConsultationReason.CONSULTATION,
                pageIndex, sizePerPage,
                ConsultationTalkStatus.NURSE_SPEAK);
        return Response.ok(consultations).build();
    }

    @Path("/user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getConsultation(@Context HttpServletRequest request,
                                    @QueryParam("user_id") @DefaultValue("") String strUserId,
                                    @QueryParam("index") @DefaultValue("0") int pageIndex,
                                    @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        Long userId = VerifyUtil.isIds(strUserId) ? VerifyUtil.parseLongIds(strUserId).get(0) : null;

        List<UserConsultationBean> consultations = userConsultationService.getUserConsultation(userId, nurseId, ConsultationReason.CONSULTATION, pageIndex, sizePerPage, ConsultationTalkStatus.NURSE_SPEAK);
        return Response.ok(consultations).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getConsultationWithTalks(@Context HttpServletRequest request,
                                             @QueryParam("consultation_id") @DefaultValue("0") long consultationId
    ) {
        UserConsultationBean consultation = userConsultationService.getUserConsultationWithTalk(consultationId, ConsultationTalkStatus.NURSE_SPEAK);
        return Response.ok(consultation).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response editConsultation(@Context HttpServletRequest request,
                                     @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                     @FormParam("category_id") @DefaultValue("") String strCategoryId,
                                     @FormParam("completed") @DefaultValue("") String strCompleted/* YES , NO */
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        if (!nurseCanAnswerConsultation(nurseId, 0)) {
            throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
        }

        Long categoryId = VerifyUtil.isIds(strCategoryId) ? VerifyUtil.parseLongIds(strCategoryId).get(0) : null;
        YesNoEnum completed = YesNoEnum.parseString(strCompleted);
        UserConsultationBean bean = userConsultationService.updateConsultationStatus(null, consultationId, categoryId, null, null, completed);
        return Response.ok(bean).build();
    }


    //===================================================================================================
    //                         patient follow-up consultation
    //===================================================================================================

    @Path("/follow-up")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addConsultation(@Context HttpServletRequest request,
                                    @FormParam("user_id") @DefaultValue("0") long userId,
                                    @FormParam("patient_id") @DefaultValue("0") long patientId,
                                    @FormParam("follow_up_description") @DefaultValue("") String diseaseDescription
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        if (!nurseCanAnswerConsultation(nurseId, 0)) {
            throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
        }

        long consultationId = userConsultationService.addConsultation(
                0, nurseId, userId, patientId,
                diseaseDescription, "",
                ConsultationCreator.NURSE,
                ConsultationReason.PATIENT_FOLLOW_UP
        );


        Map<String, Long> retValue = new HashMap<>();
        retValue.put("id", consultationId);
        return Response.ok(retValue).build();
    }

    @Path("/follow-up/image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addConsultationImage(@Context HttpServletRequest request,
                                         @FormDataParam("consultation_id") @DefaultValue("0") long consultationId,
                                         @FormDataParam("image_name") @DefaultValue("") String imageName,
                                         @FormDataParam("image") InputStream image
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        if (!nurseCanAnswerConsultation(nurseId, consultationId)) {
            throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
        }

        Map<String, String> imageIdToUrl = userConsultationService.addConsultationImage(0, nurseId, consultationId, imageName, image);
        return Response.ok(imageIdToUrl).build();
    }


    //=================================================================================================================
    //                                           talk service
    //=================================================================================================================

    @Path("/talk")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response deleteConsultationTalk(@Context HttpServletRequest request,
                                           @FormParam("talk_id") @DefaultValue("0") long talkId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        UserConsultationTalkBean talk = userConsultationService.getTalkById(talkId);
        if (!nurseCanAnswerConsultation(nurseId, talk.getConsultationId())) {
            throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
        }

        List<Long> allIds = new ArrayList<>();
        allIds.add(talkId);
        allIds = userConsultationService.deleteTalk(allIds);
        return Response.ok(allIds).build();
    }

    @Path("/talk")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addConsultationTalk(@Context HttpServletRequest request,
                                        @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                        @FormParam("talk_content") @DefaultValue("") String talkContent
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        if (!nurseCanAnswerConsultation(nurseId, consultationId)) {
            throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
        }

        ConsultationTalkStatus talkStatus = ConsultationTalkStatus.NURSE_SPEAK;
        Map<String, Long> talkReturn = userConsultationService.addTalk(consultationId, nurseId, talkStatus, talkContent);

        Long userId = talkReturn.get(UserConsultationService.USER_ID);
        UserConsultationBean consultation = userConsultationService.getUserConsultationNoProperties(consultationId);

        if (ConsultationReason.PATIENT_FOLLOW_UP.equals(consultation.getReason())) {
            notifierForAllModule.followUpTalkAlertToGo2nurseUser(consultation.getUserId(), consultationId, talkStatus.name(), talkContent);
        }
        else {
            notifierForAllModule.consultationAlertToGo2nurseUser(userId, consultationId, talkStatus, talkContent);
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
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addConsultationTalkImage(@Context HttpServletRequest request,
                                             @FormDataParam("consultation_id") @DefaultValue("0") long consultationId,
                                             @FormDataParam("talk_id") @DefaultValue("0") long talkId,
                                             @FormDataParam("image_name") @DefaultValue("") String imageName,
                                             @FormDataParam("image") InputStream image
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        if (!nurseCanAnswerConsultation(nurseId, consultationId)) {
            throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
        }

        Map<String, String> imageIdToUrl = userConsultationService.addTalkImage(0, consultationId, talkId, imageName, image);
        return Response.ok(imageIdToUrl).build();
    }

    @Path("/talk/reading_status")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addConsultationTalkImage(@Context HttpServletRequest request,
                                             @FormParam("consultation_id") @DefaultValue("0") long consultationId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        if (!nurseCanAnswerConsultation(nurseId, consultationId)) {
            throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
        }

        consultationId = userConsultationService.updateConsultationUnreadTalkStatusToRead(consultationId, ConsultationTalkStatus.NURSE_SPEAK);

        Map<String, Long> retValue = new HashMap<>();
        retValue.put("consultation_id", consultationId);
        return Response.ok(retValue).build();
    }

    private boolean nurseCanAnswerConsultation(long nurseId, long consultationId) {
        NurseExtensionBean nurseExtension = nurseExtensionService.getExtensionByNurseId(nurseId);
        if (null==nurseExtension) {
            return false;
        }

        if (consultationId>0) {
            UserConsultationBean consultation = userConsultationService.getUserConsultation(consultationId, ConsultationTalkStatus.NONE);
            if (null == consultation) {
                return false;
            }
            if (consultation.getNurseId() != nurseId) {
                return false;
            }
        }

        return YesNoEnum.YES.equals(nurseExtension.getAnswerNursingQuestion());
    }
}
