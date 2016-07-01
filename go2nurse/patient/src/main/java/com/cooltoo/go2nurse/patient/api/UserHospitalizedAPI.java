package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.constants.UserHospitalizedStatus;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.patient.beans.UserHospitalizedCorusesBean;
import com.cooltoo.go2nurse.service.*;
import com.cooltoo.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/16.
 */
@Path("/hospitalized_relation")
public class UserHospitalizedAPI {

    @Autowired private UserService userService;
    @Autowired private CourseRelationManageService courseManageService;
    @Autowired private UserCourseRelationService userCourseService;
    @Autowired private UserHospitalizedRelationService relationService;
    @Autowired private UserDiagnosticPointRelationService diagnosticRelationService;

    @Path("/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getRelation(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long currentDiagnosticGroupId =diagnosticRelationService.getUserCurrentGroupId(userId, System.currentTimeMillis());
        List<UserHospitalizedRelationBean> relations = relationService.getUserHospitalizedRelationByGroupId(userId, currentDiagnosticGroupId);
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
        String[] hospital_department = hospitalAndDepartmentUniqueId.split("_");
        if (hospital_department.length!=2) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        UserHospitalizedRelationBean relation = relationService.addRelation(userId, hospital_department[0].trim(), hospital_department[1].trim());
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
        UserHospitalizedRelationBean relation = relationService.updateRelation(relationId, true, userId, hasLeave, status);
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
        UserHospitalizedRelationBean relation = relationService.updateRelation(groupId, hospitalId, departmentId, userId, hasLeave, status);
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
        Map<DiagnosticEnumeration, List<CourseBean>> courses = courseManageService.getDiagnosticToCoursesMapInDepartment(hospitalId, departmentId);
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
        Map<DiagnosticEnumeration, List<CourseBean>> courses = courseManageService.getDiagnosticToCoursesMapInDepartment(hospitalUniqueId, departmentUniqueId);
        return Response.ok(courses).build();
    }

    @Path("/get_courses")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public  Response getUserHospitalizedCourses(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserBean user = userService.getUser(userId);

        List<UserHospitalizedCorusesBean> beans = new ArrayList<>();

        if (UserHospitalizedStatus.IN_HOSPITAL.equals(user.getHasDecide())) {
            Map<DiagnosticEnumeration, List<CourseBean>> courses
                    = userCourseService.getUserCurrentCoursesWithExtensionNursingOfHospital(userId);
            courses.forEach((key, value) -> {
                UserHospitalizedCorusesBean bean = new UserHospitalizedCorusesBean();
                bean.setId(key.ordinal());
                bean.setType(key.name());
                bean.setCourses(value);
                beans.add(bean);
            });
        }
        else {
            Map<CourseCategoryBean, List<CourseBean>> courses
                    = userCourseService.getAllPublicExtensionNursingCourses();
            courses.forEach((key, value) -> {
                UserHospitalizedCorusesBean bean = new UserHospitalizedCorusesBean();
                bean.setId(key.getId());
                bean.setType(key.getName());
                bean.setDescription(key.getIntroduction());
                bean.setCourses(value);
                beans.add(bean);
            });
        }
        return Response.ok(beans).build();
    }

}
