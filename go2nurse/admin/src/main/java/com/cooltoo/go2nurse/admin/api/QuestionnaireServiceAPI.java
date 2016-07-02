package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.QuestionBean;
import com.cooltoo.go2nurse.beans.QuestionnaireBean;
import com.cooltoo.go2nurse.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/6/28.
 */
@Path("/admin/questionnaire")
public class QuestionnaireServiceAPI {

    @Autowired private QuestionnaireService questionnaireService;

    //=======================================================================
    //    get
    //=======================================================================

    @Path("/question/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionCount(@Context HttpServletRequest request) {
        long count = questionnaireService.getQuestionCount();
        return Response.ok(count).build();
    }

    @Path("/questionnaire/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaireCount(@Context HttpServletRequest request) {
        long count = questionnaireService.getQuestionCount();
        return Response.ok(count).build();
    }

    @Path("/question/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionByPage(@Context HttpServletRequest request,
                                 @PathParam("index")  @DefaultValue("0")  int index,
                                 @PathParam("number") @DefaultValue("10") int number
    ) {
        List<QuestionBean> questions = questionnaireService.getQuestionByPage(index, number);
        return Response.ok(questions).build();
    }

    @Path("/questionnaire/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaireByPage(@Context HttpServletRequest request,
                                           @PathParam("index")  @DefaultValue("0")  int index,
                                           @PathParam("number") @DefaultValue("10") int number
    ) {
        List<QuestionnaireBean> questionnaires = questionnaireService.getQuestionnaireByPage(index, number);
        return Response.ok(questionnaires).build();
    }

    @Path("/question")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestions(@Context HttpServletRequest request,
                                 @QueryParam("question_ids")  @DefaultValue("") String questionIds
    ) {
        List<QuestionBean> questions = questionnaireService.getQuestionByIds(questionIds);
        return Response.ok(questions).build();
    }

    @Path("/question/without_questionnaire")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionsBelongNoQuestionnaire(@Context HttpServletRequest request) {
        List<QuestionBean> questions = questionnaireService.getQuestionByQuestionnaireId(0L);
        return Response.ok(questions).build();
    }


    @Path("/question/{questionnaire_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionsByQuestionnaireId(@Context HttpServletRequest request,
                                                  @PathParam("questionnaire_id") @DefaultValue("0") long questionnaireId
    ) {
        List<QuestionBean> questions = questionnaireService.getQuestionByQuestionnaireId(questionnaireId);
        return Response.ok(questions).build();
    }

    @Path("/questionnaire")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaire(@Context HttpServletRequest request,
                                     @QueryParam("questionnaire_ids") String questionnaireIds
    ) {
        List<QuestionnaireBean> questionnaires = questionnaireService.getQuestionnaireByIds(questionnaireIds);
        return Response.ok(questionnaires).build();
    }

    @Path("/questionnaire/with_question")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaireWithQuestion(@Context HttpServletRequest request,
                                                 @QueryParam("questionnaire_ids") String questionnaireIds
    ) {
        List<QuestionnaireBean> questionnaires = questionnaireService.getQuestionnaireWithQuestionsByIds(questionnaireIds);
        return Response.ok(questionnaires).build();
    }

    //=======================================================================
    //    update
    //=======================================================================

    @Path("/update/question")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateQuestion(@Context HttpServletRequest request,
                                   @FormParam("id") @DefaultValue("0") long id,
                                   @FormParam("questionnaire_id") @DefaultValue("0") long questionnaireId,
                                   @FormParam("content") @DefaultValue("") String content,
                                   @FormParam("options") @DefaultValue("") String options,
                                   @FormParam("type") @DefaultValue("SINGLE_SELECTION") String type,
                                   @FormParam("grade") @DefaultValue("-1") int grade
                                   ) {
        QuestionBean bean = questionnaireService.updateQuestion(id, questionnaireId, content, options, type, grade);
        return Response.ok(bean).build();
    }

    @Path("/update/questionnaire")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateQuestionnaire(@Context HttpServletRequest request,
                                        @FormParam("id") long id,
                                        @FormParam("title") @DefaultValue("") String title,
                                        @FormParam("description") @DefaultValue("") String description,
                                        @FormParam("conclusion") @DefaultValue("") String conclusion,
                                        @FormParam("hospital_id") @DefaultValue("0") int hospitalId
    ) {
        QuestionnaireBean bean = questionnaireService.updateQuestionnaire(id, title, description, conclusion, hospitalId);
        return Response.ok(bean).build();
    }

    //=======================================================================
    //    delete
    //=======================================================================

    @Path("/question")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteQuestion(@Context HttpServletRequest request,
                                   @FormParam("ids") @DefaultValue("") String ids
    ) {
        String deleteIds = questionnaireService.deleteQuestionByIds(ids);
        return Response.ok(deleteIds).build();
    }

    @Path("/questionnaire")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteQuestionnaire(@Context HttpServletRequest request,
                                        @FormParam("ids") @DefaultValue("") String ids
    ) {
        String deleteIds = questionnaireService.deleteQuestionnaireByIds(ids);
        return Response.ok(deleteIds).build();
    }

    @Path("/questionnaire/with_question")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteQuestionnaireAndQuestion(@Context HttpServletRequest request,
                                                   @FormParam("ids") @DefaultValue("") String ids
    ) {
        String deleteIds = questionnaireService.deleteQuestionnaireAndQuestionByIds(ids);
        return Response.ok(deleteIds).build();
    }

    //=======================================================================
    //    add
    //=======================================================================

    @Path("/question/add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addQuestion(@Context HttpServletRequest request,
                                @FormParam("questionnaire_id") @DefaultValue("0") long questionnaireId,
                                @FormParam("content") @DefaultValue("") String content,
                                @FormParam("options") @DefaultValue("") String options,
                                @FormParam("type") @DefaultValue("SINGLE_SELECTION") String type,
                                @FormParam("grade") @DefaultValue("-1") int grade
    ) {
        QuestionBean bean = questionnaireService.addQuestion(questionnaireId, content, options, type, grade);
        return Response.ok(bean).build();
    }

    @Path("/questionnaire/add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addQuestionnaire(@Context HttpServletRequest request,
                                     @FormParam("title") @DefaultValue("") String title,
                                     @FormParam("description") @DefaultValue("") String description,
                                     @FormParam("conclusion") @DefaultValue("") String conclusion,
                                     @FormParam("hospital_id") @DefaultValue("0") int hospitalId
    ) {
        QuestionnaireBean bean = questionnaireService.addQuestionnaire(title, description, conclusion, hospitalId);
        return Response.ok(bean).build();
    }
}
