package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CourseCategoryBean;
import com.cooltoo.go2nurse.beans.UserCourseRelationBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import com.cooltoo.go2nurse.service.UserCourseRelationService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/16.
 */
@Path("/user_course_relation")
public class UserCourseAPI {

    @Autowired private UserCourseRelationService userCourseRelation;
    @Autowired private CourseRelationManageService courseRelationManage;

    @Path("/get/categories")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCourseCategroy(@Context HttpServletRequest request,
                                      @QueryParam("read_statuses") @DefaultValue("unread,read") String readingStatus
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<CourseCategoryBean> categories = userCourseRelation.getCourseCategory(userId, readingStatus, CommonStatus.ENABLED.name());
        return Response.ok(categories).build();
    }

    @Path("/get/course")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCourse(@Context HttpServletRequest request,
                              @QueryParam("hospital_id") @DefaultValue("0") int hospitalId,
                              @QueryParam("department_id") @DefaultValue("0") int departmentId,
                              @QueryParam("diagnostic_id") @DefaultValue("0") long diagnosticId,
                              @QueryParam("read_statuses") @DefaultValue("unread,read") String readingStatus
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<Long> coursesIds = userCourseRelation.getRelationCourseId(userId, readingStatus, CommonStatus.ENABLED.name());
        Map<String, List<CourseBean>> hospitalDepartmentDiagnostic2Course = courseRelationManage.getCoursesByConditions(
                coursesIds, hospitalId, departmentId, diagnosticId, CommonStatus.ENABLED.name(), CommonStatus.ENABLED.name());
        return Response.ok(hospitalDepartmentDiagnostic2Course).build();
    }

    @Path("/add_courses")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addCourse(@Context HttpServletRequest request,
                              @FormParam("course_ids") @DefaultValue("") String strCourseIds
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<Long> courseIds = VerifyUtil.parseLongIds(strCourseIds);
        List<UserCourseRelationBean> courses = userCourseRelation.addUserCourseRelation(userId, courseIds);
        return Response.ok(courses).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response editCourseRelation(@Context HttpServletRequest request,
                                       @FormParam("relation_id") @DefaultValue("0") long relationId,
                                       @FormParam("read_status") @DefaultValue("") String readingStatus
                                       ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserCourseRelationBean relation = userCourseRelation.updateUserCourseRelation(relationId, true, userId, readingStatus, "");
        return Response.ok(relation).build();
    }
}
