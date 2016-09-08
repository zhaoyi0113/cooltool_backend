package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.services.NurseHospitalRelationService;
import com.cooltoo.backend.services.NurseQualificationService;
import com.cooltoo.backend.services.NurseRelationshipService;
import com.cooltoo.backend.services.NurseService;
import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseRelationshipBean;
import com.cooltoo.constants.*;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.services.CommonNurseService;
import com.cooltoo.services.NurseExtensionService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/12.
 */
@Path("/admin/nurse")
public class NurseManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseManageAPI.class);

    @Autowired private NurseHospitalRelationService hospitalRelationService;
    @Autowired private CommonNurseService commonNurseService;
    @Autowired private NurseService nurseService;
    @Autowired private NurseRelationshipService nurseRelationshipService;
    @Autowired private NurseQualificationService nurseQualificationService;
    @Autowired private NurseExtensionService nurseExtensionService;

    @Path("/{nurse_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getNurseById(@Context HttpServletRequest request,
                                 @PathParam("nurse_id") @DefaultValue("0") long nurseId) {
        logger.info("get nurse information by nurse id={}", nurseId);
        NurseBean nurse = nurseService.getNurse(nurseId);
        logger.info("nurse is {}", nurse);
        return Response.ok(nurse).build();
    }

    @Path("/authority_type")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAuthorityType(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get authority type", userId);
        List<String> allType = UserAuthority.getUserAuthority();
        logger.info("user {} get authority type {}", userId, allType);
        return Response.ok(allType).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countNurseByAuthorityAndName(@Context HttpServletRequest request,
                                                 @QueryParam("authority") @DefaultValue("") String authority,
                                                 @QueryParam("fuzzy_name") @DefaultValue("") String fuzzyName,
                                                 @QueryParam("can_answer_nursing_question") @DefaultValue("") String canAnswerNursingQuestion
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get nurse record count by authority {} fuzzyName={}", userId, authority, fuzzyName);
        long count = nurseService.countByAuthorityAndFuzzyName(authority, fuzzyName, canAnswerNursingQuestion);
        logger.info("count={}", count);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getNurseByAuthorityAndName(@Context HttpServletRequest request,
                                               @QueryParam("authority") @DefaultValue("") String strAuthority,
                                               @QueryParam("fuzzy_name") @DefaultValue("") String fuzzyName,
                                               @QueryParam("can_answer_nursing_question") @DefaultValue("") String canAnswerNursingQuestion,
                                               @QueryParam("index")  @DefaultValue("0")  int index,
                                               @QueryParam("number") @DefaultValue("10") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get nurse record count by authority {} fuzzyName{} at page {} with {} record/page",
                userId, strAuthority, fuzzyName, index, number);
        List<NurseBean> nurses = nurseService.getAllByAuthorityAndFuzzyName(strAuthority, fuzzyName, canAnswerNursingQuestion, index, number);
        logger.info("count={}", userId, strAuthority, fuzzyName, index, number, nurses.size());
        return Response.ok(nurses).build();
    }

    @Path("/update/authority")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateAuthority(@Context HttpServletRequest request,
                                    @FormParam("nurse_ids") String nurseIds,
                                    @FormParam("authority") String authority
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update nurse {} 's authority property to {}", userId, nurseIds, authority);
        List<NurseBean> nurses = nurseService.updateAuthority(nurseIds, authority);
        logger.info("user {} update nurse {} 's authority property to {}, count={}", userId, nurseIds, authority, nurses.size());
        return Response.ok(nurses).build();
    }

    //==================================================================
    //                           创建编辑
    //==================================================================
    @Path("/create_nurse")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response createNurse(@Context HttpServletRequest request,
                                @FormParam("name") @DefaultValue("") String name,
                                @FormParam("age") @DefaultValue("0") int age,
                                @FormParam("gender") @DefaultValue("SECRET") String strGender,
                                @FormParam("mobile") @DefaultValue("") String mobile,
                                @FormParam("password") @DefaultValue("") String password,
                                @FormParam("identification") @DefaultValue("") String identification,
                                @FormParam("real_name") @DefaultValue("") String realName,
                                @FormParam("short_name") @DefaultValue("") String shortNote,
                                @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                @FormParam("department_id") @DefaultValue("0") int departmentId,
                                @FormParam("can_answer_nursing_question") @DefaultValue("YES") String strCanAnswerNursingQuestion,
                                @FormParam("good_at") @DefaultValue("") String goodAt,
                                @FormParam("job_title") @DefaultValue("") String jobTitle
    ) {
        long nurseId = createNurse(name, age, strGender, mobile, password, identification, realName, shortNote,
                strCanAnswerNursingQuestion, goodAt, jobTitle,
                hospitalId, departmentId);
        return Response.ok(nurseId).build();
    }

    @Path("/edit_nurse")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editNurseBasicInfo(@Context HttpServletRequest request,
                                       @FormParam("nurse_id") @DefaultValue("0") long nurseId,
                                       @FormParam("name") @DefaultValue("") String name,
                                       @FormParam("age") @DefaultValue("-1") int age,
                                       @FormParam("gender") @DefaultValue("") String strGender,
                                       @FormParam("mobile") @DefaultValue("") String mobile,
                                       @FormParam("password") @DefaultValue("") String password,
                                       @FormParam("identification") @DefaultValue("") String identification,
                                       @FormParam("real_name") @DefaultValue("") String realName,
                                       @FormParam("short_name") @DefaultValue("") String shortNote,
                                       @FormParam("authority") @DefaultValue("") String strAuthority,
                                       @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                       @FormParam("department_id") @DefaultValue("0") int departmentId,
                                       @FormParam("can_answer_nursing_question") @DefaultValue("") String strCanAnswerNursingQuestion,
                                       @FormParam("good_at") @DefaultValue("") String goodAt,
                                       @FormParam("job_title") @DefaultValue("") String jobTitle
    ) {
        NurseBean nurseBean = editNurse(nurseId, name, age, strGender, mobile, password, identification, realName, shortNote, strAuthority,
                strCanAnswerNursingQuestion, goodAt, jobTitle,
                hospitalId, departmentId);
        return Response.ok(nurseBean).build();
    }


    @Path("/edit_nurse/add_head_photo")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editNurseHeadPhoto(@Context HttpServletRequest request,
                                       @FormDataParam("nurse_id") @DefaultValue("0") long nurseId,
                                       @FormDataParam("image_name") String imageName,
                                       @FormDataParam("image") InputStream image,
                                       @FormDataParam("image") FormDataContentDisposition disposition
    ) {
        String path = nurseService.updateHeadPhoto(nurseId, imageName, image);
        logger.info("upload successfully");
        return Response.ok(path).build();
    }

    @Path("/edit_nurse/add_background_image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editNurseBackgroundImage(@Context HttpServletRequest request,
                                             @FormDataParam("nurse_id") @DefaultValue("0") long nurseId,
                                             @FormDataParam("image_name") String imageName,
                                             @FormDataParam("image") InputStream image,
                                             @FormDataParam("image") FormDataContentDisposition disposition
    ) {
        String path = nurseService.updateBackgroundImage(nurseId, imageName, image);
        logger.info("return background path "+path);
        return Response.ok(path).build();
    }

    //============================================================================================
    //                    用户关系管理
    //============================================================================================
    // 所有条件是 与 的关系
    @Path("/relation/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countNurseRelation(@Context HttpServletRequest request,
                                  @QueryParam("user_id") @DefaultValue("0") long userId,
                                  @QueryParam("relative_user_id") @DefaultValue("0") long relativeUserId,
                                  @QueryParam("relation_type") @DefaultValue("") String relationType,
                                  @QueryParam("status") @DefaultValue("") String status
    ) {
        long adminUserId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("admin user {} count nurse relation by condition userId={} relativeUserId={}, relationType={} status={}",
                adminUserId, userId, relativeUserId, relationType, status
        );
        long count = nurseRelationshipService.countCondition(userId, relativeUserId, relationType, status);
        return Response.ok(count).build();
    }

    @Path("/relation")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getNurseRelation(@Context HttpServletRequest request,
                                     @QueryParam("user_id") @DefaultValue("0") long userId,
                                     @QueryParam("relative_user_id") @DefaultValue("0") long relativeUserId,
                                     @QueryParam("relation_type") @DefaultValue("") String relationType,
                                     @QueryParam("status") @DefaultValue("") String status,
                                     @QueryParam("index") @DefaultValue("0") int pageIndex,
                                     @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long adminUserId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("admin user {} count nurse relation by condition userId={} relativeUserId={}, relationType={} status={}",
                adminUserId, userId, relativeUserId, relationType, status
        );
        List<NurseRelationshipBean> relation = nurseRelationshipService.getRelation(userId, relativeUserId, relationType, status, pageIndex, sizePerPage);
        return Response.ok(relation).build();
    }

    @Path("/relation/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateRelationship(@Context HttpServletRequest request,
                                       @FormParam("user_id") @DefaultValue("0") long userId,
                                       @FormParam("relative_user_id") @DefaultValue("0") long relativeUserId,
                                       @FormParam("relation_type") @DefaultValue("") String relationType,
                                       @FormParam("status") @DefaultValue("0") int status
    ) {
        CommonStatus commonStatus = CommonStatus.parseInt(status);
        String strStatus = null==commonStatus ? "" : commonStatus.name();
        NurseRelationshipBean relationship = nurseRelationshipService.updateRelationStatus(userId, relativeUserId, relationType, strStatus);
        return Response.ok(relationship).build();
    }

    @Path("/relation/edit_by_id")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateRelationship(@Context HttpServletRequest request,
                                       @FormParam("relation_id") @DefaultValue("0") long relationId,
                                       @FormParam("status") @DefaultValue("0") int status
    ) {
        CommonStatus commonStatus = CommonStatus.parseInt(status);
        String strStatus = null==commonStatus ? "" : commonStatus.name();
        NurseRelationshipBean relationship = nurseRelationshipService.updateRelationStatus(relationId, strStatus);
        return Response.ok(relationship).build();
    }

    @Transactional
    private long createNurse(String name, int age, String strGender,
                             String mobile, String password, String identification,
                             String realName, String shortNote,
                             String strCanAnswerNursingQuestion, String beGoodAt, String jobTitle,
                             int hospitalId, int departmentId
    ) {
        GenderType gender = GenderType.parseString(strGender);
        YesNoEnum canAnswerNursingQuestion = YesNoEnum.parseString(strCanAnswerNursingQuestion);
        NurseEntity nurse = commonNurseService.registerNurse(name, age, gender, mobile, password, identification, realName, shortNote);
        if (hospitalId>0 || departmentId>0) {
            hospitalRelationService.newOne(nurse.getId(), hospitalId, departmentId);
            nurseQualificationService.createQualificationByAdmin(nurse.getId());
            if (YesNoEnum.YES.equals(canAnswerNursingQuestion)) {
                nurseExtensionService.setExtension(nurse.getId(), canAnswerNursingQuestion, beGoodAt, jobTitle);
            }
        }
        return nurse.getId();
    }

    @Transactional
    private NurseBean editNurse(long nurseId, String name, int age, String strGender,
                                String mobile, String password, String identification,
                                String realName, String shortNote, String strAuthority,
                                String strCanAnswerNursingQuestion, String beGoodAt, String jobTitle,
                                int hospitalId, int departmentId) {
        GenderType gender = GenderType.parseString(strGender);
        UserAuthority authority = UserAuthority.parseString(strAuthority);
        YesNoEnum canAnswerNursingQuestion = YesNoEnum.parseString(strCanAnswerNursingQuestion);
        commonNurseService.updateBasicInfo(nurseId, name, age, gender, mobile, password, identification, realName, shortNote, authority);
        if (hospitalId>0 || departmentId>0) {
            hospitalRelationService.newOne(nurseId, hospitalId, departmentId);
            nurseExtensionService.setExtension(nurseId, canAnswerNursingQuestion, beGoodAt, jobTitle);
        }
        NurseBean nurseBean = nurseService.getNurse(nurseId);
        return nurseBean;
    }
}
