package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.DiagnosticEnumerationBean;
import com.cooltoo.go2nurse.beans.UserHospitalizedRelationBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.patient.beans.UserHospitalizedCorusesBean;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import com.cooltoo.go2nurse.service.UserCourseRelationService;
import com.cooltoo.go2nurse.service.UserHospitalizedRelationService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/16.
 */
@Path("/hospitalized_relation")
public class UserHospitalizedAPI {

    @Autowired private CourseRelationManageService courseManageService;
    @Autowired private UserCourseRelationService userCourseService;
    @Autowired private UserHospitalizedRelationService relationService;

    @Path("/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getRelation(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserHospitalizedRelationBean> relations = relationService.getRelation(userId, CommonStatus.ENABLED.name());
        return Response.ok(relations).build();
    }

    @Path("/add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addRelation(@Context HttpServletRequest request,
                                            @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                            @FormParam("department_id") @DefaultValue("0") int departmentId
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserHospitalizedRelationBean relation = relationService.addRelation(userId, hospitalId, departmentId);
        return Response.ok(relation).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateRelation(@Context HttpServletRequest request,
                                            @FormParam("relation_id") @DefaultValue("0") long relationId,
                                            @FormParam("status") @DefaultValue("disabled") String status
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserHospitalizedRelationBean relation = relationService.updateRelation(relationId, true, userId, status);
        return Response.ok(relation).build();
    }

    @Path("/get_courses/{hospital_id}/{department_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCoursesInDepartment(@Context HttpServletRequest request,
                                           @PathParam("hospital_id") @DefaultValue("0") int hospitalId,
                                           @PathParam("department_id") @DefaultValue("0") int departmentId
    ) {
        Map<DiagnosticEnumerationBean, List<CourseBean>> courses = courseManageService.getDiagnosticToCoursesMapInDepartment(hospitalId, departmentId);
        return Response.ok(courses).build();
    }


    @Path("/get_courses/{hospital_department_unique_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCoursesInDepartment(@Context HttpServletRequest request,
                                           @PathParam("hospital_department_unique_id") @DefaultValue("0") String hospitalDepartmentUniqueId
    ) {
        List<String> uniqueIds = NumberUtil.parseRandomIdentity(hospitalDepartmentUniqueId);
        String hospitalUniqueId = uniqueIds.size()>=1 ? uniqueIds.get(0) : "";
        String departmentUniqueId = uniqueIds.size()>=2 ? uniqueIds.get(1) : "";
        Map<DiagnosticEnumerationBean, List<CourseBean>> courses = courseManageService.getDiagnosticToCoursesMapInDepartment(hospitalUniqueId, departmentUniqueId);
        return Response.ok(courses).build();
    }

    @Path("/get_courses")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public  Response getUserHospitalizedCourses(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Map<UserHospitalizedRelationBean, Map<DiagnosticEnumerationBean, List<CourseBean>>> courses
                = userCourseService.getUserCourses(userId, CommonStatus.ENABLED.name());
        List<UserHospitalizedCorusesBean> beans = new ArrayList<>();
        courses.values().forEach((element) -> {
            element.forEach((key, value) ->{
                UserHospitalizedCorusesBean bean = new UserHospitalizedCorusesBean();
                bean.setId(key.getId());
                bean.setName(key.getName());
                bean.setCourses(value);
                beans.add(bean);
            });
        });
        return Response.ok(beans).build();
    }

    @Path("/get_courses/extension_nursing")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserExtensionNursingCourses(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<CourseBean> courses = userCourseService.getUserCoursesInExtensionNursing(userId);
        return Response.ok(courses).build();
    }


}
