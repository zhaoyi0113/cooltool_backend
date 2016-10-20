package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CourseCategoryBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.beans.UserHospitalizedRelationBean;
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
import javax.ws.rs.*;
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

    @Autowired
    private UserService userService;
    @Autowired
    private CourseRelationManageService courseManageService;
    @Autowired
    private UserCourseRelationService userCourseService;
    @Autowired
    private UserHospitalizedRelationService relationService;
    @Autowired
    private UserDiagnosticPointRelationService diagnosticRelationService;

    @Path("/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getRelation(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long currentDiagnosticGroupId = diagnosticRelationService.getUserCurrentGroupId(userId);
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
        if (VerifyUtil.isStringEmpty(hospitalAndDepartmentUniqueId) || hospitalAndDepartmentUniqueId.length() != 12) {
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
        String hospitalUniqueId = uniqueIds.size() >= 1 ? uniqueIds.get(0) : "";
        String departmentUniqueId = uniqueIds.size() >= 2 ? uniqueIds.get(1) : "";
        Map<DiagnosticEnumeration, List<CourseBean>> courses = courseManageService.getDiagnosticToCoursesMapInDepartment(hospitalUniqueId, departmentUniqueId);
        return Response.ok(courses).build();
    }

    @Path("/get_courses")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserHospitalizedCourses(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserBean user = userService.getUser(userId);
        boolean userHasSelectedCourses = userCourseService.isUserSelectedHospitalCoursesNow(userId);
        boolean hasCourses = false;
        logger.info("user has selected courses={}, user decide={}", userHasSelectedCourses, user.getHasDecide());
        List<UserHospitalizedCoursesBean> returnValue = new ArrayList<>();
        UserHospitalizedCoursesBean extensionNursingBean = null;

        if (UserHospitalizedStatus.IN_HOSPITAL.equals(user.getHasDecide()) && userHasSelectedCourses) {
            Map<DiagnosticEnumeration, List<CourseBean>> courses = userCourseService.getUserCurrentCoursesWithExtensionNursingOfHospital(userId);
            returnValue = parseObjectToBean(courses, true);
            for (UserHospitalizedCoursesBean tmp : returnValue) {
                if (DiagnosticEnumeration.EXTENSION_NURSING.ordinal()==tmp.getId()) {
                    extensionNursingBean = tmp;
                    Map<CourseCategoryBean, List<CourseBean>> tmpCourses = userCourseService.getAllCategoryToCoursesByCourses((List<CourseBean>) tmp.getCourses());
                    List<UserHospitalizedCoursesBean> extensionNursingCourses = parseObjectToBean(tmpCourses, false);
                    sortCourseArrays(extensionNursingCourses);
                    extensionNursingBean.setCourses(extensionNursingCourses);
                }
                if (!hasCourses) {
                    hasCourses = tmp.getCourseSize()>0;
                }
            }
        }

        logger.info("has_courses={}", hasCourses);
        if (!hasCourses) {
            Map<CourseCategoryBean, List<CourseBean>> courses = userCourseService.getAllPublicExtensionNursingCourses(userId);
            returnValue = parseObjectToBean(courses, false);
            sortCourseArrays(returnValue);
        }
        return Response.ok(returnValue).build();
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
        UserBean user = userService.getUser(userId);
        boolean userHasSelectedCourses = userCourseService.isUserSelectedHospitalCoursesNow(userId);
        UserHospitalizedCoursesBean bean = null;
        if (UserHospitalizedStatus.IN_HOSPITAL.equals(user.getHasDecide()) && userHasSelectedCourses) {
            Map<DiagnosticEnumeration, List<CourseBean>> courses = userCourseService.getUserCurrentCoursesWithExtensionNursingOfHospital(userId);
            List<UserHospitalizedCoursesBean> beans = parseObjectToBean(courses, true);
            for (UserHospitalizedCoursesBean tmp : beans) {
                if (tmp.getId() == diagnosticId) {
                    bean = tmp;
                    break;
                }
            }
            // extension_nursing course should return courses group by category
            if (null!=bean && DiagnosticEnumeration.EXTENSION_NURSING.ordinal()==bean.getId()) {
                Map<CourseCategoryBean, List<CourseBean>> tmpCourses = userCourseService.getAllCategoryToCoursesByCourses((List<CourseBean>) bean.getCourses());
                List<UserHospitalizedCoursesBean> extensionNursingCourses = parseObjectToBean(tmpCourses, false);
                sortCourseArrays(extensionNursingCourses);
                bean.setCourses(extensionNursingCourses);
            }
        }
        if(bean == null){
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        return Response.ok(bean).build();
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
                                           @PathParam("id") int categoryId) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Map<CourseCategoryBean, List<CourseBean>> courses = userCourseService.getAllPublicExtensionNursingCourses(userId);
        UserHospitalizedCoursesBean bean = null;
        List<UserHospitalizedCoursesBean> beans = parseObjectToBean(courses, false);
        if (!beans.isEmpty()) {
            for (UserHospitalizedCoursesBean tmp : beans) {
                if (categoryId==tmp.getId()) {
                    bean = tmp;
                    break;
                }
            }
        }

        if (null==bean) {
            return Response.ok().build();
        }
        return Response.ok(bean).build();
    }

    @GET
    @Path("/hospital_and_category")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCoursesByHospitalIdAndCategoryId(@Context HttpServletRequest request,
                                                        @QueryParam("hospital_id") @DefaultValue("0") int hospitalId,
                                                        @QueryParam("category_id") @DefaultValue("0") long categoryId
    ) {
        List<CourseBean> courses = courseManageService.getCoursesByHospitalAndCategory(hospitalId, categoryId);
        return Response.ok(courses).build();
    }

    private List<UserHospitalizedCoursesBean> parseObjectToBean(Object objMap, boolean diagnosticCourses) {
        List<UserHospitalizedCoursesBean> retVal = new ArrayList<>();
        if (!(objMap instanceof Map)) {
            return retVal;
        }

        List<DiagnosticEnumeration> remainedDiagnostic = null;
        if (diagnosticCourses) {
            remainedDiagnostic = DiagnosticEnumeration.getAllDiagnostic();
        }

        UserHospitalizedCoursesBean bean = null;
        Map map = (Map)objMap;
        Set keys = map.keySet();
        for (Object obj : keys) {
            bean = new UserHospitalizedCoursesBean();
            if (obj instanceof CourseCategoryBean) {
                CourseCategoryBean key = (CourseCategoryBean)obj;
                Object value = map.get(key);

                String name = key.getName();
                if (CourseCategoryService.category_all.equals(key.getName())) {
                    name = DiagnosticEnumeration.EXTENSION_NURSING.name();
                }
                bean.setId(key.getId());
                bean.setType(name);
                bean.setName(name);
                bean.setDescription(key.getIntroduction());
                bean.setImageUrl(key.getImageUrl());
                bean.setCourses(value);
            }
            else if (obj instanceof DiagnosticEnumeration) {
                DiagnosticEnumeration key = (DiagnosticEnumeration)obj;
                Object value = map.get(key);

                if (null == value) {
                    value = new ArrayList<>();
                }

                remainedDiagnostic.remove(key);

                bean.setId(key.ordinal());
                bean.setType(key.name());
                bean.setName(key.name());
                bean.setDescription("");
                bean.setImageUrl("");
                bean.setCourses(value);
            }
            retVal.add(bean);
        }

        if (null!=remainedDiagnostic && !remainedDiagnostic.isEmpty()) {
            for (DiagnosticEnumeration key : remainedDiagnostic) {
                bean = new UserHospitalizedCoursesBean();
                bean.setId(key.ordinal());
                bean.setType(key.name());
                bean.setName(key.name());
                bean.setDescription("");
                bean.setImageUrl("");
                bean.setCourses(new ArrayList<>());
                retVal.add(bean);
            }
        }

        return retVal;
    }


    private void sortCourseArrays(List<UserHospitalizedCoursesBean> beans) {
        Collections.sort(beans, new Comparator<UserHospitalizedCoursesBean>() {
            @Override
            public int compare(UserHospitalizedCoursesBean o1, UserHospitalizedCoursesBean o2) {
                if (null != o1 && null != o2) {
                    long delta = (o1.getId() - o2.getId());
                    if (o1.getId() < 0 && o2.getId() < 0) {
                        return delta > 0 ? 1 : (delta < 0 ? -1 : 0);
                    } else {
                        return delta > 0 ? -1 : (delta < 0 ? 1 : 0);
                    }
                }
                if (o1 == null || o1.getId() < 0) {
                    return 1;
                }
                if (o2 == null || o2.getId() < 0) {
                    return -1;
                }
                return 0;
            }
        });
    }
}