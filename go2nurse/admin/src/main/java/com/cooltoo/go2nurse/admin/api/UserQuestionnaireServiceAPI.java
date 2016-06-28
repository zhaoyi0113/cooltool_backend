package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.QuestionnaireBean;
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
@Path("/admin/user/questionnaire")
public class UserQuestionnaireServiceAPI {

    @Autowired private UserQuestionnaireAnswerService userAnswerService;

    @Path("/all/{user_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersAllQuestionnaire(@Context HttpServletRequest request,
                                             @PathParam("user_id") @DefaultValue("0") long userId
    ) {
        List<QuestionnaireBean> usersQuestionnaires = userAnswerService.getUserQuestionnaire(userId);
        return Response.ok(usersQuestionnaires).build();
    }

    @Path("/with_answer/{user_id}/{questionnaire_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersQuestionnaire(@Context HttpServletRequest request,
                                          @PathParam("user_id") @DefaultValue("0") long userId,
                                          @PathParam("questionnaire_id") @DefaultValue("0") long questionnaireId
    ) {
        QuestionnaireBean usersQuestionnaires = userAnswerService.getUserQuestionnaireWithAnswer(userId, questionnaireId);
        return Response.ok(usersQuestionnaires).build();
    }
}
