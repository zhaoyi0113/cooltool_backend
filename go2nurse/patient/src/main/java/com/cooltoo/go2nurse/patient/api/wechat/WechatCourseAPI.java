package com.cooltoo.go2nurse.patient.api.wechat;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.CoursesGroupBean;
import com.cooltoo.go2nurse.beans.WeChatAccountBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.openapp.WeChatAccountService;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import com.cooltoo.go2nurse.service.UserCourseRelationService;
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
import java.util.List;

/**
 * Created by hp on 2016/6/16.
 */
@Path("/wechat/courses")
public class WechatCourseAPI {

    private static final Logger logger = LoggerFactory.getLogger(WechatCourseAPI.class);

    @Autowired private CourseRelationManageService courseManageService;
    @Autowired private UserCourseRelationService userCourseService;
    @Autowired private WeChatService weChatService;
    @Autowired private WeChatAccountService weChatAccountService;

    @Path("/{hospital_department_unique_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCoursesInDepartment(@Context HttpServletRequest request,
                                           @PathParam("hospital_department_unique_id") @DefaultValue("") String hospitalDepartmentUniqueId
    ) {
        hospitalDepartmentUniqueId += "";
        String hospitalUniqueId   = hospitalDepartmentUniqueId.length()>=6 ? hospitalDepartmentUniqueId.substring(0, 6) : "";
        String departmentUniqueId = hospitalDepartmentUniqueId.length()>=12? hospitalDepartmentUniqueId.substring(6, 12) : "";
        Integer[] hospitalDepartmentId = courseManageService.getHospitalDepartmentId(hospitalUniqueId, departmentUniqueId);

        List<CoursesGroupBean> diagnosticGroup = courseManageService.getHospitalCoursesGroupByDiagnostic(null, hospitalDepartmentId[0], hospitalDepartmentId[1]);
        return Response.ok(diagnosticGroup).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCoursesInDepartment(@Context HttpServletRequest request

    ) {
        String depUniqueId = (String) request.getAttribute(ContextKeys.DEPARTMENT_UNIQUE_ID);
        String hosUniqueId = (String) request.getAttribute(ContextKeys.HOSPITAL_UNIQUE_ID);
        logger.debug("get hospital/department id "+hosUniqueId+"/"+depUniqueId);
        Integer[] hospitalDepartmentId = courseManageService.getHospitalDepartmentId(hosUniqueId, depUniqueId);

        List<CoursesGroupBean> diagnosticGroup = courseManageService.getHospitalCoursesGroupByDiagnostic(null, hospitalDepartmentId[0], hospitalDepartmentId[1]);
        return Response.ok(diagnosticGroup).build();
    }

    @Path("/{hospital_department_unique_id}/{category_ids}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCoursesInDepartment(@Context HttpServletRequest request,
                                           @PathParam("hospital_department_unique_id") @DefaultValue("0") String hospitalDepartmentUniqueId,
                                           @PathParam("category_ids") @DefaultValue("") String strCategoryIds
    ) {
        hospitalDepartmentUniqueId += "";
        String hospitalUniqueId   = hospitalDepartmentUniqueId.length()>=6 ? hospitalDepartmentUniqueId.substring(0, 6) : "";
        String departmentUniqueId = hospitalDepartmentUniqueId.length()>=12? hospitalDepartmentUniqueId.substring(6, 12) : "";
        Integer[] hospitalDepartmentId = courseManageService.getHospitalDepartmentId(hospitalUniqueId, departmentUniqueId);

        strCategoryIds = null!=strCategoryIds ? strCategoryIds.replace('_', ',') : null;
        List<Long> categoryIds = VerifyUtil.parseLongIds(strCategoryIds);

        List<CoursesGroupBean> categoryGroup = courseManageService.getHospitalCoursesGroupByCategory(null, hospitalDepartmentId[0], hospitalDepartmentId[1], categoryIds);
        return Response.ok(categoryGroup).build();
    }

    @Path("/category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCategoryCoursesInDepartment(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);

        WeChatAccountBean weChatAccount = getWeChatAccount(userId);
        if (null==weChatAccount) {
            return Response.ok(new ArrayList<>()).build();
        }

        List<CoursesGroupBean> categoryGroup = courseManageService.getHospitalCoursesGroupByCategory(userId, weChatAccount.getHospitalId(), weChatAccount.getDepartmentId());
        return Response.ok(categoryGroup).build();
    }


    @Path("/diagnostic")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getDiagnosticCoursesInDepartment(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);

        WeChatAccountBean weChatAccount = getWeChatAccount(userId);
        if (null==weChatAccount) {
            return Response.ok(new ArrayList<>()).build();
        }

        List<CoursesGroupBean> diagnosticGroup = courseManageService.getHospitalCoursesGroupByDiagnostic(userId, weChatAccount.getHospitalId(), weChatAccount.getDepartmentId());
        return Response.ok(diagnosticGroup).build();
    }

    private WeChatAccountBean getWeChatAccount(long userId) {
        String appId = weChatService.getAppIdByUserId(userId);
        WeChatAccountBean weChatAccount = weChatAccountService.getWeChatAccountByAppId(appId);
        return weChatAccount;
    }
}