package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CoursesGroupBean;
import com.cooltoo.go2nurse.beans.NursePushCourseBean;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import com.cooltoo.go2nurse.service.CourseService;
import com.cooltoo.go2nurse.service.NursePushCourseService;
import com.cooltoo.go2nurse.service.notification.NotifierForAllModule;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.nurse360.service.NurseServiceForNurse360;
import com.cooltoo.nurse360.util.Nurse360Utility;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/4.
 */
@Path("/nurse/push/course/user")
public class NursePushCourseToUserAPI {

    @Autowired private NursePushCourseService pushCourseService;
    @Autowired private CourseRelationManageService courseRelationManageService;
    @Autowired private NurseServiceForNurse360 nurseService;
    @Autowired private CourseService go2nurseCourseService;
    @Autowired private Nurse360Utility utility;
    @Autowired private NotifierForAllModule notifierForAllModule;

    @Path("/category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getCourseCategories(@Context HttpServletRequest request) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean nurse = nurseService.getNurseById(nurseId);
        NurseHospitalRelationBean hospitalDepartment = (NurseHospitalRelationBean) nurse.getProperty(NurseBean.HOSPITAL_DEPARTMENT);

        List<CoursesGroupBean> group = new ArrayList<>();
        if (null==hospitalDepartment) {

        }
        else {
            group = courseRelationManageService.getHospitalCoursesGroupByCategory(
                    null, hospitalDepartment.getHospitalId(), hospitalDepartment.getDepartmentId()
            );
        }
        return Response.ok(group).build();
    }

    @Path("/category/{category_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getCategoryCourses(@Context HttpServletRequest request,
                                       @PathParam("category_id") @DefaultValue("0") long categoryId
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean nurse = nurseService.getNurseById(nurseId);
        NurseHospitalRelationBean hospitalDepartment = (NurseHospitalRelationBean) nurse.getProperty(NurseBean.HOSPITAL_DEPARTMENT);

        Object courses = new ArrayList<>();
        if (null!=hospitalDepartment) {
            List<CoursesGroupBean> groups = courseRelationManageService.getHospitalCoursesGroupByCategory(
                    null, hospitalDepartment.getHospitalId(), hospitalDepartment.getDepartmentId()
            );
            for (CoursesGroupBean tmp : groups) {
                if (tmp.getId() == categoryId) {
                    courses = tmp.getCourses();
                }
            }
        }
        return Response.ok(courses).build();
    }

    @Path("/category/course/{course_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getCourseDetail(@Context HttpServletRequest request,
                                    @PathParam("course_id") @DefaultValue("0") long courseId
    ) {
        CourseBean course = go2nurseCourseService.getCourseById(courseId, utility.getHttpPrefix());
        return Response.ok(course).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getCoursesPushed(@Context HttpServletRequest request,
                                     @QueryParam("user_id") @DefaultValue("0") long userId,
                                     @QueryParam("patient_id") @DefaultValue("0") long patientId,
                                     @QueryParam("index") @DefaultValue("0") int pageIndex,
                                     @QueryParam("number") @DefaultValue("0") int sizePerPage
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NursePushCourseBean> coursesPushed = pushCourseService.getCoursePushed(nurseId, userId, patientId, pageIndex, sizePerPage, true);
        return Response.ok(coursesPushed).build();
    }

    @Path("/all")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response deleteAllHistory(@Context HttpServletRequest request) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<Long> pushedRecordIds = pushCourseService.deletePushedCourseReadStatus(nurseId, null, null);
        return Response.ok(pushedRecordIds).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response deleteHistory(@Context HttpServletRequest request,
                                  @FormParam("push_record_id") @DefaultValue("0") long recordId
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        boolean success = pushCourseService.deletePushCourseReadStatus(nurseId, recordId);
        return Response.ok(success ? "ok" : "failed").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response pushCourseToUser(@Context HttpServletRequest request,
                                     @FormParam("user_id") @DefaultValue("0") long userId,
                                     @FormParam("patient_id") @DefaultValue("0") long patientId,
                                     @FormParam("course_id") @DefaultValue("0") long courseId
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NursePushCourseBean push = pushCourseService.pushCourseToUser(nurseId, userId, patientId, courseId);
        notifierForAllModule.pushCourseAlertToGo2nurseUser(userId, courseId, push.getRead(), "course is pushed to patient");
        return Response.ok(push).build();
    }

}
