package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.NursePushCourseBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.NursePushCourseService;
import com.cooltoo.go2nurse.service.UserCourseRelationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/21.
 */
@Path("/user")
public class NursePushCourseAPI {

    @Autowired private NursePushCourseService nursePushCourseService;
    @Autowired private UserCourseRelationService userCourseRelationService;

    @Path("/course/pushed")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCoursesPushed(@Context HttpServletRequest request,
                                     @QueryParam("nurse_id") @DefaultValue("0") long nurseId,
                                     @QueryParam("index")    @DefaultValue("0") int pageIndex,
                                     @QueryParam("number")   @DefaultValue("0") int sizePerPage
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<NursePushCourseBean> coursesPushed = nursePushCourseService.getCoursePushed(nurseId, userId, null, pageIndex, sizePerPage, true);
        List<CourseBean> allCourse = new ArrayList<>();
        for (NursePushCourseBean tmp : coursesPushed) {
            allCourse.add(tmp.getCourse());
        }
        userCourseRelationService.setCourseReadStatus(userId, allCourse);
        return Response.ok(coursesPushed).build();
    }

    @Path("/all")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response deleteAllHistory(@Context HttpServletRequest request) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<Long> pushedRecordIds = nursePushCourseService.deletePushedCourseReadStatus(nurseId, null, null);
        return Response.ok(pushedRecordIds).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response deleteHistory(@Context HttpServletRequest request,
                                  @FormParam("push_record_id") @DefaultValue("0") long recordId
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        boolean success = nursePushCourseService.deletePushCourseReadStatus(nurseId, recordId);
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
        NursePushCourseBean push = nursePushCourseService.pushCourseToUser(nurseId, userId, patientId, courseId);
        notifierForAllModule.pushCourseAlertToPatient(userId, courseId, push.getRead(), "course is pushed to patient");
        return Response.ok(push).build();
    }
}
