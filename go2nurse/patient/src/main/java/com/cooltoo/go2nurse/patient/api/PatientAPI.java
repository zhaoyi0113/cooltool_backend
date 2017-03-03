package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.service.PatientService;
import com.cooltoo.go2nurse.service.PatientSymptomsService;
import com.cooltoo.go2nurse.service.QuestionnaireService;
import com.cooltoo.go2nurse.service.UserPatientRelationService;
import com.cooltoo.util.JSONUtil;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
import java.util.*;

/**
 * Created by yzzhao on 2/29/16.
 */
@Path("/patient")
public class PatientAPI {

    private static final Logger logger = LoggerFactory.getLogger(PatientAPI.class);

    @Autowired private PatientService service;
    @Autowired private UserPatientRelationService userPatientRelation;
    @Autowired private PatientSymptomsService patientSymptomsService;
    @Autowired private QuestionnaireService questionnaireService;

    @Autowired private WeChatService weChatService;

    @Path("/get_patient")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getPatientWithUserId(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<Long> patientIds = userPatientRelation.getPatientByUser(userId, CommonStatus.ENABLED.name());
        List<PatientBean> patients = service.getAllByStatusAndIds(patientIds, CommonStatus.ENABLED);
        return Response.ok(patients).build();
    }

    @Path("/get_by_ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getByPatientsId(@Context HttpServletRequest request,
                                    @QueryParam("patients_id") @DefaultValue("") String strPatientsId
    ) {
        List<Long> patientsId = VerifyUtil.parseLongIds(strPatientsId);
        List<PatientBean> beans;
        if (!VerifyUtil.isListEmpty(patientsId)) {
            beans = service.getAllByStatusAndIds(patientsId, CommonStatus.ENABLED);
        } else {
            beans = new ArrayList<>();
        }
        logger.info("patient count is {}", beans.size());
        return Response.ok(beans).build();
    }

    @Path("/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response create(@Context HttpServletRequest request,
                           @FormParam("name") @DefaultValue("") String name,
                           @FormParam("gender") @DefaultValue("2") int gender,
                           @FormParam("birthday") @DefaultValue("") String strBirthday,
                           @FormParam("identityCard") @DefaultValue("") String identityCard,
                           @FormParam("mobile") @DefaultValue("") String mobile
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Long time = NumberUtil.getTime(strBirthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        Date date = null==time ? null : new Date(time);

        List<Long> usersPatient = userPatientRelation.getPatientByUser(userId, CommonStatus.ENABLED.name());
        PatientBean patient = service.create(name, gender, date, identityCard, mobile, VerifyUtil.isListEmpty(usersPatient) ? YesNoEnum.YES : null, null);
        if (null != patient && patient.getId() > 0) {
            UserPatientRelationBean relation = userPatientRelation.addPatientToUser(patient.getId(), userId);
            logger.info("user patient relation is {}", relation);
        }
        return Response.ok(patient).build();
    }

    @Path("/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response update(@Context HttpServletRequest request,
                           @FormParam("id") @DefaultValue("-1") long patientId,
                           @FormParam("name") @DefaultValue("") String name,
                           @FormParam("gender") @DefaultValue("-1") int gender,
                           @FormParam("birthday") @DefaultValue("") String strBirthday,
                           @FormParam("identityCard") @DefaultValue("") String identityCard,
                           @FormParam("mobile") @DefaultValue("") String mobile,
                           @FormParam("is_default") @DefaultValue("") String strIsDefault,
                           @FormParam("is_self") @DefaultValue("") String strIsSelf,
                           @FormParam("status") @DefaultValue("") String status
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Long time = NumberUtil.getTime(strBirthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        Date birthday = null==time ? null : new Date(time);
        YesNoEnum isDefault = YesNoEnum.parseString(strIsDefault);
        YesNoEnum isSelf = YesNoEnum.parseString(strIsSelf);
        PatientBean one = service.update(userId, patientId, name, gender, birthday, identityCard, mobile, isDefault, isSelf, status);
        return Response.ok(one).build();
    }

    @Path("/header_image")
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addHeadPhoto(@Context HttpServletRequest request,
                                 @FormDataParam("patient_id") @DefaultValue("0") String strPatientId,
                                 @FormDataParam("image_name") @DefaultValue("") String imageName,
                                 @FormDataParam("file") InputStream image,
                                 @FormDataParam("file") FormDataContentDisposition disposition
    ) {
        long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : 0L;
        PatientBean patient = service.updateHeaderImage(patientId, imageName, image);
        logger.info("upload successfully");
        return Response.ok(patient).build();
    }

    @Path("/header_image_wx")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addHeadPhotoFromWx(@Context HttpServletRequest request,
                                       @FormParam("patient_id") @DefaultValue("0") String strPatientId,
                                       @FormParam("image_name") @DefaultValue("") String imageName,
                                       @FormParam("image_id") String imageId,
                                       @FormParam("app_id") String appId
    ) {
        long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : 0L;
        InputStream image = weChatService.downloadImageFromWxWithAppid(imageId, appId);
        PatientBean patient = service.updateHeaderImage(patientId, imageName, image);
        logger.info("upload successfully");
        return Response.ok(patient).build();
    }

    //====================================================================================
    //
    //                   symptoms
    //
    //====================================================================================
    @Path("/symptoms")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addPatientSymptoms(@Context HttpServletRequest request,
                                       @FormParam("patient_id")           @DefaultValue("0") String strPatientId,
                                       @FormParam("symptoms")             @DefaultValue("") String symptoms,
                                       @FormParam("symptoms_description") @DefaultValue("") String symptomsDesc
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : 0L;
        PatientSymptomsBean symptomsBean = patientSymptomsService.addPatientSymptoms(userId, patientId, symptoms, symptomsDesc);
        return Response.ok(symptomsBean).build();
    }

    @Path("/symptoms")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updatePatientSymptoms(@Context HttpServletRequest request,
                                          @FormParam("patient_symptoms_id")  @DefaultValue("0") String strSymptomsId,
                                          @FormParam("symptoms")             @DefaultValue("") String symptoms,
                                          @FormParam("symptoms_description") @DefaultValue("") String symptomsDesc,
                                          @FormParam("questionnaire")        @DefaultValue("") String questionnaire
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long symptomsId = VerifyUtil.isIds(strSymptomsId) ? VerifyUtil.parseLongIds(strSymptomsId).get(0) : 0L;

        JSONUtil jsonUtil = JSONUtil.newInstance();
        List<ADLSubmitBean> adlSubmits = jsonUtil.parseJsonList(questionnaire, ADLSubmitBean.class);
        fillADLSubmit(adlSubmits);
        questionnaire = jsonUtil.toJsonString(adlSubmits);

        PatientSymptomsBean symptomsBean = patientSymptomsService.updatePatientSymptoms(
                true, userId, symptomsId, symptoms, symptomsDesc, questionnaire);
        return Response.ok(symptomsBean).build();
    }

    @Path("/symptoms/image")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @LoginAuthentication(requireUserLogin = true)
    public Response addPatientSymptoms(@Context HttpServletRequest request,
                                       @FormDataParam("patient_symptoms_id")  @DefaultValue("0") String strSymptomsId,
                                       @FormDataParam("image") InputStream inputStream
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long symptomsId = VerifyUtil.isIds(strSymptomsId) ? VerifyUtil.parseLongIds(strSymptomsId).get(0) : 0L;
        PatientSymptomsBean symptomsBean = patientSymptomsService.addSymptomsImage(true, userId, symptomsId, inputStream);
        return Response.ok(symptomsBean).build();
    }

    @Path("/symptoms/order")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response bindPatientSymptomsWithOrder(@Context HttpServletRequest request,
                                                 @FormParam("patient_symptoms_id")  @DefaultValue("0") String strSymptomsId,
                                                 @FormParam("order_id")             @DefaultValue("0") String strOrderId
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long symptomsId = VerifyUtil.isIds(strSymptomsId) ? VerifyUtil.parseLongIds(strSymptomsId).get(0) : 0L;
        long orderId    = VerifyUtil.isIds(strOrderId)    ? VerifyUtil.parseLongIds(strOrderId).get(0)    : 0L;
        PatientSymptomsBean symptomsBean = patientSymptomsService.bindWithOrder(true, userId, orderId, symptomsId);
        return Response.ok(symptomsBean).build();
    }


    private void fillADLSubmit(List<ADLSubmitBean> adlSubmits) {
        if (VerifyUtil.isListEmpty(adlSubmits)) {
            return;
        }
        JSONUtil jsonUtil = JSONUtil.newInstance();

        long questionnaireId = questionnaireService.getQuestionnaireIdByQuestionId(adlSubmits.get(0).getQuestionId());
        QuestionnaireBean questionnaire = questionnaireService.getQuestionnaireWithQuestions(questionnaireId);
        List<QuestionnaireConclusionBean> conclusions = jsonUtil.parseJsonList(questionnaire.getConclusion(), QuestionnaireConclusionBean.class);

        Map<Long, QuestionBean> questionIdToBean = new HashMap<>();
        List<QuestionBean> questions = questionnaire.getQuestions();
        for (QuestionBean tmp : questions) {
            questionIdToBean.put(tmp.getId(), tmp);
        }


        int score = 0;
        for (ADLSubmitBean tmp : adlSubmits) {
            score += tmp.getScore();
        }

        QuestionnaireConclusionBean adlConclusion = null;
        for (QuestionnaireConclusionBean conclusion : conclusions) {
            if (conclusion.isThisConclusion(score)) {
                adlConclusion = conclusion;
                break;
            }
        }


        for (ADLSubmitBean tmp : adlSubmits) {
            tmp.setQuestionnaireId(questionnaireId);
            tmp.setQuestionnaireTitle(questionnaire.getTitle());
            tmp.setConclusionItem(adlConclusion.getItem());
            tmp.setConclusionInterval(adlConclusion.getInterval());
            tmp.setConclusionScore(score);
            QuestionBean question = questionIdToBean.get(tmp.getQuestionId());
            tmp.setQuestionContent(null==question ? "" : question.getContent());
        }
    }
}
