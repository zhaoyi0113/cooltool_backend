package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.constants.UserHospitalizedStatus;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.patient.beans.UserHospitalizedCoursesBean;
import com.cooltoo.go2nurse.service.*;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.*;

/**
 * Created by hp on 2016/6/16.
 */
@Path("/hospitalized_relation")
public class UserHospitalizedAPI {

    private static final Logger logger = LoggerFactory.getLogger(UserHospitalizedAPI.class);

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
        if (VerifyUtil.isStringEmpty(hospitalAndDepartmentUniqueId) || hospitalAndDepartmentUniqueId.length()!=12) {
            logger.error("hospital department unique ids={} is invalid", hospitalAndDepartmentUniqueId);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        String hospitalUniqueId = hospitalAndDepartmentUniqueId.substring(0, 6);
        String departmentUniqueId = hospitalAndDepartmentUniqueId.substring(6);
        UserHospitalizedRelationBean relation = relationService.addRelation(userId, hospitalUniqueId, departmentUniqueId);
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
        boolean userHasSelectedCourses = userCourseService.isUserSelectedHospitalCoursesNow(userId);

        boolean hasCourses = false;
        List<UserHospitalizedCoursesBean> beans = new ArrayList<>();
        if (UserHospitalizedStatus.IN_HOSPITAL.equals(user.getHasDecide()) && userHasSelectedCourses) {
            Map<DiagnosticEnumeration, List<CourseBean>> courses
                    = userCourseService.getUserCurrentCoursesWithExtensionNursingOfHospital(userId);
            List<CourseBean> extensionNursingCourses=null;
            Set<DiagnosticEnumeration> keys = courses.keySet();
            for (DiagnosticEnumeration key : keys) {
                List<CourseBean> value = courses.get(key);
                UserHospitalizedCoursesBean bean = new UserHospitalizedCoursesBean();
                bean.setId(key.ordinal());
                bean.setType(key.name());
                bean.setName(key.name());
                bean.setDescription("");
                bean.setImageUrl("");
                bean.setCourses(value);
                beans.add(bean);
                if (DiagnosticEnumeration.EXTENSION_NURSING.equals(key)) {
                    extensionNursingCourses = value;
                }
                if (!hasCourses) {
                    hasCourses = !VerifyUtil.isListEmpty(value);
                }
            }
            if (!VerifyUtil.isListEmpty(extensionNursingCourses)) {
                Map<CourseCategoryBean, List<CourseBean>> extensionNursingMap
                        = userCourseService.getAllCategoryToCoursesByCourses(extensionNursingCourses);
                Set<CourseCategoryBean> keySet = extensionNursingMap.keySet();
                for (CourseCategoryBean key : keySet) {
                    List<CourseBean> value = extensionNursingMap.get(key);
                    UserHospitalizedCoursesBean bean = new UserHospitalizedCoursesBean();
                    bean.setId(key.getId());
                    bean.setType(key.getName());
                    bean.setName(key.getName());
                    bean.setDescription(key.getIntroduction());
                    bean.setImageUrl(key.getImageUrl());
                    bean.setCourses(value);
                    beans.add(bean);
                    if (!hasCourses) {
                        hasCourses = !VerifyUtil.isListEmpty(value);
                    }
                }
            }
        }
        if (!hasCourses) {
            Map<CourseCategoryBean, List<CourseBean>> courses
                    = userCourseService.getAllPublicExtensionNursingCourses(userId);
            courses.forEach((key, value) -> {
                UserHospitalizedCoursesBean bean = new UserHospitalizedCoursesBean();
                bean.setId(key.getId());
                bean.setType(key.getName());
                bean.setName(key.getName());
                bean.setDescription(key.getIntroduction());
                bean.setImageUrl(key.getImageUrl());
                bean.setCourses(value);
                beans.add(bean);
            });
        }
        return Response.ok(beans).build();
    }

    @Path("/is_select_department")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response isSelectDepartment(@Context HttpServletRequest request){
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        boolean userHasSelectedCourses = userCourseService.isUserSelectedHospitalCoursesNow(userId);
        Map<String, Boolean> ret = new HashMap<>();
        ret.put("select_department", userHasSelectedCourses);
        return Response.ok(ret).build();
    }
}
