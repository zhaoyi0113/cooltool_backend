package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.QuestionnaireBean;
import com.cooltoo.go2nurse.beans.UserQuestionnaireAnswerBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.QuestionnaireService;
import com.cooltoo.go2nurse.service.UserQuestionnaireAnswerService;
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

    @Autowired private QuestionnaireService questionnaireService;
    @Autowired private UserQuestionnaireAnswerService userAnswerService;

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUsersAllQuestionnaire(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<QuestionnaireBean> usersQuestionnaires = userAnswerService.getUserQuestionnaire(userId);
        return Response.ok(usersQuestionnaires).build();
    }

    @Path("/with_answer/{questionnaire_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUsersQuestionnaire(@Context HttpServletRequest request,
                                          @PathParam("questionnaire_id") @DefaultValue("0") long questionnaireId
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        QuestionnaireBean usersQuestionnaires = userAnswerService.getUserQuestionnaireWithAnswer(userId, questionnaireId);
        return Response.ok(usersQuestionnaires).build();
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

    @Path("/answer/questionnaire")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response deleteQuestionnaireAnswers(@Context HttpServletRequest request,
                                        @FormParam("questionnaire_id") @DefaultValue("0") long questionnaireId
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserQuestionnaireAnswerBean> userAnswer = userAnswerService.deleteByUserIdAndQuestionnaireId(userId, questionnaireId);
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
                                       @FormParam("question_id") @DefaultValue("0") long questionId,
                                       @FormParam("answer") @DefaultValue("") String answer
    ){
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserQuestionnaireAnswerBean userAnswer = userAnswerService.updateAnswer(userId, questionId, answer, "");
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
                                   @FormParam("question_id") @DefaultValue("0") long questionId,
                                   @FormParam("answer") @DefaultValue("") String answer
    ){
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserQuestionnaireAnswerBean userAnswer = userAnswerService.addAnswer(userId, questionId, answer);
        return Response.ok(userAnswer).build();
    }
}
