package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.QuestionBean;
import com.cooltoo.go2nurse.beans.QuestionnaireBean;
import com.cooltoo.go2nurse.service.QuestionnaireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/6/28.
 */
@Path("/admin/questionnaire")
public class QuestionnaireServiceAPI {

    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireServiceAPI.class);

    @Autowired private QuestionnaireService questionnaireService;

    //=======================================================================
    //    get
    //=======================================================================

    @Path("/question/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionCount(@Context HttpServletRequest request) {
        logger.info("get all question count");
        long count = questionnaireService.getQuestionCount();
        logger.info("get all question count is {}", count);
        return Response.ok(count).build();
    }

    @Path("/questionnaire/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaireCount(@Context HttpServletRequest request) {
        logger.info("get all questionnaire count");
        long count = questionnaireService.getQuestionCount();
        logger.info("get all questionnaire count is {}", count);
        return Response.ok(count).build();
    }

    @Path("/question/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionByPage(@Context HttpServletRequest request,
                                 @PathParam("index")  @DefaultValue("0")  int index,
                                 @PathParam("number") @DefaultValue("10") int number
    ) {
        logger.info("get all question at page {} numberOfPage {}", index, number);
        List<QuestionBean> questions = questionnaireService.getQuestionByPage(index, number);
        logger.info("get all question at page {} numberOfPage {}, count is ", index, number, questions.size());
        return Response.ok(questions).build();
    }

    @Path("/questionnaire/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategoryByPage(@Context HttpServletRequest request,
                                      @PathParam("index")  @DefaultValue("0")  int index,
                                      @PathParam("number") @DefaultValue("10") int number
    ) {
        logger.info("get all questionnaire at page {} numberOfPage {}", index, number);
        List<QuestionnaireBean> questionnaires = questionnaireService.getQuestionnaireByPage(index, number);
        logger.info("get all questionnaire at page {} numberOfPage {}, count is ", index, number, questionnaires.size());
        return Response.ok(questionnaires).build();
    }

    @Path("/question")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestions(@Context HttpServletRequest request,
                                 @QueryParam("question_ids")  @DefaultValue("") String questionIds
    ) {
        logger.info("get question by ids={}", questionIds);
        List<QuestionBean> questions = questionnaireService.getQuestionByIds(questionIds);
        return Response.ok(questions).build();
    }

    @Path("/question/without_questionnaire")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionsWithoutCategoryId(@Context HttpServletRequest request) {
        logger.info("get question with no questionnaire belong");
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
        logger.info("get questionnaire by ids={}", questionnaireIds);
        List<QuestionnaireBean> questionnaires = questionnaireService.getQuestionnaireByIds(questionnaireIds);
        return Response.ok(questionnaires).build();
    }

    @Path("/questionnaire/with_question")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaireWithQuestion(@Context HttpServletRequest request,
                                                 @QueryParam("questionnaire_ids") String questionnaireIds
    ) {
        logger.info("get questionnaire by ids={}", questionnaireIds);
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
                                   @FormParam("type") @DefaultValue("SINGLE_SELECTION") String type
    ) {
        logger.info("update question id={} questionnaireId={} content={}, options={} type={}",
                id, questionnaireId, content, options, type);
        QuestionBean bean = questionnaireService.updateQuestion(id, questionnaireId, content, options, type);
        return Response.ok(bean).build();
    }

    @Path("/update/questionnaire")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateQuestionnaire(@Context HttpServletRequest request,
                                   @FormParam("id") long id,
                                   @FormParam("title") @DefaultValue("") String title,
                                   @FormParam("description") @DefaultValue("") String description,
                                   @FormParam("hospital_id") @DefaultValue("0") int hospitalId
    ) {
        logger.info("update questionnaire id={}  title={}  description={}  hospitalId={}",
                id, title, description, hospitalId);
        QuestionnaireBean bean = questionnaireService.updateQuestionnaire(id, title, description, hospitalId);
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
                                @FormParam("type") @DefaultValue("SINGLE_SELECTION") String type
    ) {
        logger.info("add question with questionnaireId={} content={} options={} type={}.",
                questionnaireId, content, options, type);
        QuestionBean bean = questionnaireService.addQuestion(questionnaireId, content, options, type);
        return Response.ok(bean).build();
    }

    @Path("/questionnaire/add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addQuestionnaire(@Context HttpServletRequest request,
                                     @FormParam("title") @DefaultValue("") String title,
                                     @FormParam("description") @DefaultValue("") String description,
                                     @FormParam("hospital_id") @DefaultValue("0") int hospitalId
    ) {
        logger.info("add questionnaire title={} description={} hospital_id={}",
                title, description, hospitalId);
        QuestionnaireBean bean = questionnaireService.addQuestionnaire(title, description, hospitalId);
        return Response.ok(bean).build();
    }
}
