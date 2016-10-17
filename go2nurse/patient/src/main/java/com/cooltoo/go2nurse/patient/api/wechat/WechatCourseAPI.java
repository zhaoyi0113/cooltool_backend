package com.cooltoo.go2nurse.patient.api.wechat;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.openapp.WeChatAccountService;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.patient.beans.UserHospitalizedCoursesBean;
import com.cooltoo.go2nurse.service.*;
import com.cooltoo.util.VerifyUtil;
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
@Path("/wechat/courses")
public class WeChatCourseAPI {

    @Autowired private CourseRelationManageService courseManageService;
    @Autowired private UserCourseRelationService userCourseService;
    @Autowired private WeChatService weChatService;
    @Autowired private WeChatAccountService weChatAccountService;


    @Path("/category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCategoryCoursesInDepartment(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        String appId = weChatService.getAppIdByUserId(userId);
        WeChatAccountBean weChatAccount = weChatAccountService.getWeChatAccountByAppId(appId);
        List<UserHospitalizedCoursesBean> returnValue = new ArrayList<>();
        if (null==weChatAccount) {
            return Response.ok(returnValue).build();
        }
        List<CourseBean> allCourseInHospital = courseManageService.getAllCourseByHospitalOrDepartmentId(weChatAccount.getHospitalId(), weChatAccount.getDepartmentId());
        Map<CourseCategoryBean, List<CourseBean>> diagnosticToCourses = userCourseService.getAllCategoryToCoursesByCourses(allCourseInHospital);
        returnValue = parseObjectToBean(diagnosticToCourses, false);
        return Response.ok(returnValue).build();
    }


    @Path("/diagnostic")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getDiagnosticCoursesInDepartment(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        String appId = weChatService.getAppIdByUserId(userId);
        WeChatAccountBean weChatAccount = weChatAccountService.getWeChatAccountByAppId(appId);
        List<UserHospitalizedCoursesBean> returnValue = new ArrayList<>();
        Map<DiagnosticEnumeration, List<CourseBean>> diagnosticToCourses = new HashMap<>();
        if (null==weChatAccount) {
        }
        else {
            diagnosticToCourses = courseManageService.getDiagnosticToCoursesMapInDepartment(weChatAccount.getHospitalId(), weChatAccount.getDepartmentId());
            if (!VerifyUtil.isMapEmpty(diagnosticToCourses)) {
                returnValue = parseObjectToBean(diagnosticToCourses, true);
                for (UserHospitalizedCoursesBean tmp : returnValue) {
                    if (DiagnosticEnumeration.EXTENSION_NURSING.ordinal() == tmp.getId()) {
                        UserHospitalizedCoursesBean extensionNursingBean = tmp;
                        Map<CourseCategoryBean, List<CourseBean>> tmpCourses = userCourseService.getAllCategoryToCoursesByCourses((List<CourseBean>) tmp.getCourses());
                        List<UserHospitalizedCoursesBean> extensionNursingCourses = parseObjectToBean(tmpCourses, false);
                        sortCourseArrays(extensionNursingCourses);
                        extensionNursingBean.setCourses(extensionNursingCourses);
                    }
                }
            }
        }
        return Response.ok(returnValue).build();
    }

    private WeChatAccountBean getWeChatAccount(long userId) {
        String appId = weChatService.getAppIdByUserId(userId);
        WeChatAccountBean weChatAccount = weChatAccountService.getWeChatAccountByAppId(appId);
        return weChatAccount;
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