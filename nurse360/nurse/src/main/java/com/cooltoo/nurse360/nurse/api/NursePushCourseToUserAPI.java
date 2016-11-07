package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.CoursesGroupBean;
import com.cooltoo.go2nurse.beans.NursePushCourseBean;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import com.cooltoo.go2nurse.service.NursePushCourseService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.nurse360.service.NurseServiceForNurse360;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getCoursesPushed(@Context HttpServletRequest request,
                                     @QueryParam("index") @DefaultValue("0") int pageIndex,
                                     @QueryParam("number") @DefaultValue("0") int sizePerPage
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NursePushCourseBean> coursesPushed = pushCourseService.getCoursePushed(nurseId, null, null, pageIndex, sizePerPage, true);
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
        return Response.ok(push).build();
    }

}
