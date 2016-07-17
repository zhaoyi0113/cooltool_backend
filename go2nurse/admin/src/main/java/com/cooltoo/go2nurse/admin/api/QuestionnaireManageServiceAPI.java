package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.QuestionBean;
import com.cooltoo.go2nurse.beans.QuestionnaireBean;
import com.cooltoo.go2nurse.beans.QuestionnaireCategoryBean;
import com.cooltoo.go2nurse.beans.QuestionnaireStatisticsBean;
import com.cooltoo.go2nurse.service.QuestionnaireService;
import com.cooltoo.go2nurse.service.UserQuestionnaireAnswerService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.DeclareRoles;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

/**
 * Created by zhaolisong on 16/6/28.
 */
@Path("/admin/questionnaire")
public class QuestionnaireManageServiceAPI {

    @Autowired private QuestionnaireService questionnaireService;
    @Autowired private UserQuestionnaireAnswerService userQuestionnaireAnswerService;

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
        long count = questionnaireService.getQuestionnaireCount();
        return Response.ok(count).build();
    }

    @Path("/questionnaire_category/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaireCategoryCount(@Context HttpServletRequest request) {
        long count = questionnaireService.getCategoryCount();
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

    @Path("/questionnaire_category/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaireCategoryByPage(@Context HttpServletRequest request,
                                                   @PathParam("index")  @DefaultValue("0")  int index,
                                                   @PathParam("number") @DefaultValue("10") int number
    ) {
        List<QuestionnaireCategoryBean> questionnaires = questionnaireService.getCategoryByPage(index, number);
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

    @Path("/questionnaire/by_hospital_id")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaireByHospitalId(@Context HttpServletRequest request,
                                                 @QueryParam("hospital_id") @DefaultValue("0") int hospitalId,
                                                 @QueryParam("index") @DefaultValue("0") int pageIndex,
                                                 @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<QuestionnaireBean> questionnaires = questionnaireService.getQuestionnaireByHospitalId(hospitalId, pageIndex, sizePerPage);
        return Response.ok(questionnaires).build();
    }

    @Path("/questionnaire/statistics")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaireStatistics(@Context HttpServletRequest request,
                                               @QueryParam("questionnaire_id") @DefaultValue("0") long questionnaireId
    ) {
        List<QuestionnaireBean> questionnaires = questionnaireService.getQuestionnaireWithQuestionsByIds(questionnaireId+"");
        userQuestionnaireAnswerService.getQuestionnaireStatistics(questionnaires);
        return Response.ok(questionnaires).build();
    }

    @Path("/questionnaire/statistics/by_hospital_id/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countQuestionnaireStatisticsByHospitalId(@Context HttpServletRequest request,
                                                             @QueryParam("hospital_id") @DefaultValue("0") int hospitalId
    ){
        long questionnairesCount = questionnaireService.countQuestionnaireByHospitalId(hospitalId);
        return Response.ok(questionnairesCount).build();
    }

    @Path("/questionnaire/statistics/by_hospital_id")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaireStatisticsByHospitalId(@Context HttpServletRequest request,
                                                           @QueryParam("hospital_id") @DefaultValue("0") int hospitalId
    ) {
        List<QuestionnaireBean> questionnaires = questionnaireService.getQuestionnaireWithQuestionsByHospitalId(hospitalId);
        userQuestionnaireAnswerService.getQuestionnaireStatistics(questionnaires);
        return Response.ok(questionnaires).build();
    }

    @Path("/questionnaire_category/with_questionnaire")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionnaireCategoryWithQuestionnaire(@Context HttpServletRequest request,
                                                              @QueryParam("category_ids") String categoryIds
    ) {
        List<QuestionnaireCategoryBean> categories = questionnaireService.getCategoryWithQuestionnaireByIds(categoryIds);
        return Response.ok(categories).build();
    }

    //=======================================================================
    //    update
    //=======================================================================
    @Path("/update/question_image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateQuestionImage(@Context HttpServletRequest request,
                                        @FormDataParam("id") @DefaultValue("0") long id,
                                        @FormDataParam("image_name") @DefaultValue("") String imageName,
                                        @FormDataParam("image") InputStream image,
                                        @FormDataParam("image") FormDataContentDisposition disposition
    ) {
        QuestionBean bean = questionnaireService.updateQuestionImage(id, imageName, image);
        return Response.ok(bean).build();
    }

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
                                        @FormParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                        @FormParam("category_id") @DefaultValue("0") long categoryId
    ) {
        QuestionnaireBean bean = questionnaireService.updateQuestionnaire(id, title, description, conclusion, hospitalId, categoryId);
        return Response.ok(bean).build();
    }

    @Path("/update/questionnaire_category")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateQuestionnaireCategory(@Context HttpServletRequest request,
                                                @FormParam("id") long id,
                                                @FormParam("name") @DefaultValue("") String name,
                                                @FormParam("introduction") @DefaultValue("") String introduction
    ) {
        QuestionnaireCategoryBean bean = questionnaireService.updateCategory(id, name, introduction);
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

    @Path("/questionnaire_category")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteQuestionnaireCategory(@Context HttpServletRequest request,
                                                @FormParam("ids") @DefaultValue("") String ids
    ) {
        String deleteIds = questionnaireService.deleteCategoryByIds(ids);
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
                                     @FormParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                     @FormParam("category_id") @DefaultValue("0") long categoryId
    ) {
        QuestionnaireBean bean = questionnaireService.addQuestionnaire(title, description, conclusion, hospitalId, categoryId);
        return Response.ok(bean).build();
    }

    @Path("/questionnaire_category/add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addQuestionnaireCategory(@Context HttpServletRequest request,
                                             @FormParam("name") @DefaultValue("") String name,
                                             @FormParam("introduction") @DefaultValue("") String introduction
    ) {
        QuestionnaireCategoryBean bean = questionnaireService.addCategory(name, introduction);
        return Response.ok(bean).build();
    }
}
