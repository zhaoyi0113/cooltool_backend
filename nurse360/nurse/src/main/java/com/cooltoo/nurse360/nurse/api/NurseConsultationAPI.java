package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.ConsultationCategoryBean;
import com.cooltoo.go2nurse.beans.UserConsultationBean;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.service.ConsultationCategoryService;
import com.cooltoo.go2nurse.service.UserConsultationService;
import com.cooltoo.go2nurse.service.UserConsultationTalkService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
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
    @Autowired private UserConsultationTalkService talkService;

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
                                    @QueryParam("content") @DefaultValue("") String content,
                                    @QueryParam("category_id") @DefaultValue("0") long lCategoryId,
                                    @QueryParam("index") @DefaultValue("0") int pageIndex,
                                    @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        Long categoryId = 0==lCategoryId ? null : new Long(lCategoryId);
        List<UserConsultationBean> consultations = userConsultationService.getUserConsultationByCondition(null, null, nurseId, categoryId, content, pageIndex, sizePerPage);
        return Response.ok(consultations).build();
    }

    @Path("/user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getConsultation(@Context HttpServletRequest request,
                                    @QueryParam("user_id") @DefaultValue("0") long userId,
                                    @QueryParam("index") @DefaultValue("0") int pageIndex,
                                    @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<UserConsultationBean> consultations = userConsultationService.getUserConsultation(userId, nurseId, pageIndex, sizePerPage);
        return Response.ok(consultations).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getConsultationWithTalks(@Context HttpServletRequest request,
                                             @QueryParam("consultation_id") @DefaultValue("0") long consultationId
    ) {
        UserConsultationBean consultation = userConsultationService.getUserConsultationWithTalk(consultationId);
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
        Long categoryId = VerifyUtil.isIds(strCategoryId) ? VerifyUtil.parseLongIds(strCategoryId).get(0) : null;
        YesNoEnum completed = YesNoEnum.parseString(strCompleted);
        UserConsultationBean bean = userConsultationService.updateConsultationStatus(null, consultationId, categoryId, null, null, completed);
        return Response.ok(bean).build();
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
        ConsultationTalkStatus talkStatus = ConsultationTalkStatus.NURSE_SPEAK;
        long talkId = userConsultationService.addTalk(consultationId, nurseId, talkStatus, talkContent);
        Map<String, Long> returnValue = new HashMap<>();
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
        consultationId = userConsultationService.updateConsultationUnreadTalkStatusToRead(consultationId, ConsultationTalkStatus.NURSE_SPEAK);

        Map<String, Long> retValue = new HashMap<>();
        retValue.put("consultation_id", consultationId);
        return Response.ok(retValue).build();
    }
}
