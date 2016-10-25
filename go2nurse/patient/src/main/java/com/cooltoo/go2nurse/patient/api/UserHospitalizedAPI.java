package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.beans.UserHospitalizedRelationBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.constants.UserHospitalizedStatus;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.beans.CoursesGroupBean;
import com.cooltoo.go2nurse.service.*;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/16.
 */
@Path("/hospitalized_relation")
public class UserHospitalizedAPI {

    private static final Logger logger = LoggerFactory.getLogger(UserHospitalizedAPI.class);

    @Autowired private UserService userService;
    @Autowired private UserCourseRelationService userCourseService;
    @Autowired private CourseRelationManageService courseManageService;
    @Autowired private UserHospitalizedRelationService hospitalRelationService;
    @Autowired private UserDiagnosticPointRelationService diagnosticRelationService;

    @Path("/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getRelation(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long currentDiagnosticGroupId = diagnosticRelationService.getUserCurrentGroupId(userId);
        List<UserHospitalizedRelationBean> relations = hospitalRelationService.getUserHospitalizedRelationByGroupId(userId, currentDiagnosticGroupId);
        return Response.ok(relations).build();
    }

    @Path("/add/by_hospital_department_unique_id")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addRelationByUniqueId(@Context HttpServletRequest request,
                                          @FormParam("hospital_department_unique_id") @DefaultValue("") String hospitalAndDepartmentUniqueId
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        if (VerifyUtil.isStringEmpty(hospitalAndDepartmentUniqueId) || hospitalAndDepartmentUniqueId.length() != 12) {
            logger.error("hospital department unique ids={} is invalid", hospitalAndDepartmentUniqueId);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        String hospitalUniqueId = hospitalAndDepartmentUniqueId.substring(0, 6);
        String departmentUniqueId = hospitalAndDepartmentUniqueId.substring(6);
        UserHospitalizedRelationBean relation = hospitalRelationService.addRelation(userId, hospitalUniqueId, departmentUniqueId);
        return Response.ok(relation).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateRelation(@Context HttpServletRequest request,
                                   @FormParam("relation_id") @DefaultValue("0") long relationId,
                                   @FormParam("has_leave") @DefaultValue("no") String hasLeave,
                                   @FormParam("status") @DefaultValue("disabled") String status
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserHospitalizedRelationBean relation = hospitalRelationService.updateRelation(relationId, true, userId, hasLeave, status);
        return Response.ok(relation).build();
    }

    @Path("/edit/by_hospital_department_id")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateRelation(@Context HttpServletRequest request,
                                   @FormParam("group_id") @DefaultValue("0") long groupId,
                                   @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                   @FormParam("department_id") @DefaultValue("0") int departmentId,
                                   @FormParam("has_leave") @DefaultValue("no") String hasLeave,
                                   @FormParam("status") @DefaultValue("disabled") String status
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserHospitalizedRelationBean relation = hospitalRelationService.updateRelation(groupId, hospitalId, departmentId, userId, hasLeave, status);
        return Response.ok(relation).build();
    }

    @Path("/get_courses/{hospital_department_unique_id}/{category_ids}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseUnderCategories(@Context HttpServletRequest request,
                                             @PathParam("hospital_department_unique_id") @DefaultValue("0") String hospitalDepartmentUniqueId,
                                             @PathParam("category_ids") String strCategoryIds
    ){
        hospitalDepartmentUniqueId += "";
        String hospitalUniqueId   = hospitalDepartmentUniqueId.length()>=6 ? hospitalDepartmentUniqueId.substring(0, 6) : "";
        String departmentUniqueId = hospitalDepartmentUniqueId.length()>=12? hospitalDepartmentUniqueId.substring(6, 12) : "";
        Integer[] hospitalDepartmentId = courseManageService.getHospitalDepartmentId(hospitalUniqueId, departmentUniqueId);

        strCategoryIds = null!=strCategoryIds ? strCategoryIds.replace('_', ',') : null;
        List<Long> categoryIds = VerifyUtil.parseLongIds(strCategoryIds);

        List<CoursesGroupBean> categoryGroup = courseManageService.getHospitalCoursesGroupByCategory(null, hospitalDepartmentId[0], hospitalDepartmentId[1], categoryIds);
        return Response.ok(categoryGroup).build();
    }

    @Path("/get_courses/{hospital_department_unique_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCoursesInDepartment(@Context HttpServletRequest request,
                                           @PathParam("hospital_department_unique_id") @DefaultValue("0") String hospitalDepartmentUniqueId
    ) {
        hospitalDepartmentUniqueId += "";
        String hospitalUniqueId   = hospitalDepartmentUniqueId.length()>=6 ? hospitalDepartmentUniqueId.substring(0, 6) : "";
        String departmentUniqueId = hospitalDepartmentUniqueId.length()>=12? hospitalDepartmentUniqueId.substring(6, 12) : "";
        Integer[] hospitalDepartmentId = courseManageService.getHospitalDepartmentId(hospitalUniqueId, departmentUniqueId);
        List<CoursesGroupBean> coursesGroups = courseManageService.getHospitalCoursesGroupByDiagnostic(null, hospitalDepartmentId[0], hospitalDepartmentId[1]);
        return Response.ok(coursesGroups).build();
    }

    @Path("/get_courses")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserHospitalizedCourses(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);

        boolean hasCourses = false;
        List<CoursesGroupBean> coursesGroups = new ArrayList<>();

        Integer[] hospitalDepartment = getHospitalDepartmentThatUserIn(userId);
        if (-1!=hospitalDepartment[0]) {
            coursesGroups = courseManageService.getHospitalCoursesGroupByDiagnostic(userId, hospitalDepartment[0], hospitalDepartment[1]);
            hasCourses = CoursesGroupBean.isCoursesGroupHasCourses(coursesGroups);
        }

        logger.info("has_courses={}", hasCourses);
        if (!hasCourses) {
            coursesGroups = courseManageService.getHospitalCoursesGroupByCategory(userId, -1, 0);
        }
        return Response.ok(coursesGroups).build();
    }

    @Path("/is_select_department")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response isSelectDepartment(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        boolean userHasSelectedCourses = userCourseService.isUserSelectedHospitalCoursesNow(userId);
        Map<String, Boolean> ret = new HashMap<>();
        ret.put("select_department", userHasSelectedCourses);
        return Response.ok(ret).build();
    }

    @GET
    @Path("/diagnostic/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserDiagnosticCourses(@Context HttpServletRequest request,
                                             @PathParam("id") int diagnosticId) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);

        DiagnosticEnumeration diagnostic = DiagnosticEnumeration.parseInt(diagnosticId);
        Integer[] hospitalDepartment = getHospitalDepartmentThatUserIn(userId);
        if (null!=diagnostic && -1!=hospitalDepartment[0]) {
            Long diagnosticLongId = Long.valueOf(diagnosticId);
            CoursesGroupBean bean = courseManageService.getHospitalCoursesGroupByDiagnostic(userId, hospitalDepartment[0], hospitalDepartment[1], diagnosticLongId);
            return Response.ok(bean).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/diagnostic")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserDiagnosticCourses(@Context HttpServletRequest request,
                                             @QueryParam("diagnostic_name") String diagnosticName
    ) {
        DiagnosticEnumeration diagnostic = DiagnosticEnumeration.parseString(diagnosticName);
        int diagnosticId = null==diagnostic ? Integer.MIN_VALUE : diagnostic.ordinal();
        return getUserDiagnosticCourses(request, diagnosticId);
    }

    @GET
    @Path("/category/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserCategoryCourses(@Context HttpServletRequest request,
                                           @PathParam("id") long lCtegoryId) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Integer[] hospitalDepartment = getHospitalDepartmentThatUserIn(userId);
        if (-1!=hospitalDepartment[0]) {
            List<Long> categoryId = VerifyUtil.parseLongIds(""+lCtegoryId);
            List<CoursesGroupBean> categoryGroup = courseManageService.getHospitalCoursesGroupByCategory(userId, hospitalDepartment[0], hospitalDepartment[1], categoryId);
            if (!VerifyUtil.isListEmpty(categoryGroup)) {
                return Response.ok(categoryGroup.get(0)).build();
            }
        }
        return Response.ok().build();
    }

    private Integer[] getHospitalDepartmentThatUserIn(long userId) {
        Integer[] returnVal = new Integer[]{-1, 0}; // cooltoo's courses
        UserBean user = userService.getUser(userId);
        logger.info("user decide={}", user.getHasDecide());
        if (!UserHospitalizedStatus.IN_HOSPITAL.equals(user.getHasDecide())) {
            return returnVal;
        }
        long currentGroupId = diagnosticRelationService.getUserCurrentGroupId(userId);
        logger.info("user currentGroupId={}", currentGroupId);
        if (currentGroupId<0) {
            return returnVal;
        }
        List<UserHospitalizedRelationBean> hospitalized = hospitalRelationService.getUserHospitalizedRelationByGroupId(userId, currentGroupId);
        logger.info("user hospitalized={}", hospitalized);
        for (UserHospitalizedRelationBean tmp : hospitalized) {
            if (!YesNoEnum.YES.equals(tmp.getHasLeave())) {
                returnVal = new Integer[]{tmp.getHospitalId(), tmp.getDepartmentId()};
                break;
            }
        }
        return returnVal;
    }
}