package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.UserHospitalizedStatus;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/6/28.
 */
@Path("/user/questionnaire")
public class UserQuestionnaireAnswerServiceAPI {

    @Autowired private UserDiagnosticPointRelationService userDiagnosticService;
    @Autowired private UserHospitalizedRelationService userHospitalizedService;
    @Autowired private QuestionnaireService questionnaireService;
    @Autowired private UserQuestionnaireAnswerService userAnswerService;
    @Autowired private UserService userService;

    @Path("/questionnaire_of_hospital_or_not")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getQuestionnaireOfHospitalUserHospitalizedIn(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserBean user = userService.getUser(userId);
        List<UserHospitalizedRelationBean> userHospitalizedBeans = null;
        if (UserHospitalizedStatus.IN_HOSPITAL.equals(user.getHasDecide())) {
            long groupId = userDiagnosticService.getUserCurrentGroupId(userId, System.currentTimeMillis());
            userHospitalizedBeans = userHospitalizedService.getUserHospitalizedRelationByGroupId(userId, groupId);
        }
        List<QuestionnaireCategoryBean> returnValue = questionnaireService.getCategoryWithQuestionnaireByUserHospitalizedBean(userHospitalizedBeans);
        return Response.ok(returnValue).build();
    }

    @Path("/questionnaire_with_question")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getQuestionnaireWithQuestion(@Context HttpServletRequest request,
                                                 @QueryParam("questionnaire_ids") String questionnaireIds
    ) {
        List<QuestionnaireBean> questionnaires = questionnaireService.getQuestionnaireWithQuestionsByIds(questionnaireIds);
        return Response.ok(questionnaires).build();
    }

    @Path("/questionnaire_answered")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUsersQuestionnaireByGroupId(@Context HttpServletRequest request,
                                                   @QueryParam("group_id") @DefaultValue("0") long groupId) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        QuestionnaireBean usersQuestionnaire = userAnswerService.getUserQuestionnaireWithAnswer(userId, groupId);
        return Response.ok(usersQuestionnaire).build();
    }


    @Path("/all_questionnaire_answered")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUsersAllQuestionnaire(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<QuestionnaireBean> usersQuestionnaires = userAnswerService.getUserQuestionnaire(userId);
        return Response.ok(usersQuestionnaires).build();
    }

    @Path("/new_user_questionnaire_group_id")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response newUserQuestionnaireGroupId(@Context HttpServletRequest request) {
        long newGroupId = userAnswerService.newQuestionnaireGroupId();
        return Response.ok(newGroupId).build();
    }

    //============================================================================
    //                          delete
    //============================================================================

    @Path("/answer")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response deleteQuestionAnswers(@Context HttpServletRequest request,
                                          @FormParam("question_ids") @DefaultValue("0") String questionIds
    ){
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserQuestionnaireAnswerBean> userAnswer = userAnswerService.deleteByUserIdAndQuestionIds(userId, questionIds);
        return Response.ok(userAnswer).build();
    }

    @Path("/answer/group_id")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response deleteQuestionnaireAnswers(@Context HttpServletRequest request,
                                        @FormParam("group_id") @DefaultValue("0") long groupId
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserQuestionnaireAnswerBean> userAnswer = userAnswerService.deleteByUserIdAndGroupId(userId, groupId);
        return Response.ok(userAnswer).build();
    }

    //============================================================================
    //                          update
    //============================================================================

    @Path("/answer/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response editQuestionAnswer(@Context HttpServletRequest request,
                                       @FormParam("group_id") @DefaultValue("0") long groupId,
                                       @FormParam("question_id") @DefaultValue("0") long questionId,
                                       @FormParam("answer") @DefaultValue("") String answer
    ){
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserQuestionnaireAnswerBean userAnswer = userAnswerService.updateAnswer(userId, groupId, questionId, answer, "");
        return Response.ok(userAnswer).build();
    }

    //============================================================================
    //                          add
    //============================================================================

    @Path("/answer")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response answerQuestion(@Context HttpServletRequest request,
                                   @FormParam("group_id") @DefaultValue("0") long groupId,
                                   @FormParam("patient_id") @DefaultValue("0") long patientId,
                                   @FormParam("question_id") @DefaultValue("0") long questionId,
                                   @FormParam("answer") @DefaultValue("") String answer
    ){
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserQuestionnaireAnswerBean userAnswer = userAnswerService.addAnswer(userId, patientId, groupId, questionId, answer);
        return Response.ok(userAnswer).build();
    }
}
