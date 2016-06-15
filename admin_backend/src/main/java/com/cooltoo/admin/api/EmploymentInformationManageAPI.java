package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.beans.EmploymentInformationBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.services.EmploymentInformationService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

/**
 * Created by hp on 2016/4/21.
 */
@Path("/admin/employment_information")
public class EmploymentInformationManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(EmploymentInformationManageAPI.class.getName());

    @Autowired
    private EmploymentInformationService employmentInfoService;

    // status ==> all/enabled/disabled/deleted
    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countEmploymentInfo(@Context HttpServletRequest request,
                                        @QueryParam("employment_type") @DefaultValue("ALL") String employmentType,
                                        @QueryParam("status") @DefaultValue("ALL") String status
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get employment information count by status={}", userId, status);
        long count = employmentInfoService.countEmploymentInfoByStatus(status, employmentType);
        logger.info("count = {}", count);
        return Response.ok(count).build();
    }


    // status ==> all/enabled/disabled/deleted
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getEmploymentInfoByStatus(@Context HttpServletRequest request,
                                              @QueryParam("employment_type") @DefaultValue("ALL") String employmentType,
                                              @QueryParam("status") @DefaultValue("ALL") String status,
                                              @QueryParam("index")  @DefaultValue("0") int index,
                                              @QueryParam("number") @DefaultValue("10") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get employment information by status={} at page={}, {}/page", userId, status, index, number);
        List<EmploymentInformationBean> activities = employmentInfoService.getEmploymentInfoByStatus(status, employmentType, index, number);
        logger.info("count = {}", activities.size());
        return Response.ok(activities).build();
    }

    @Path("/get_by_ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getEmploymentInfoByIds(@Context HttpServletRequest request,
                                           @QueryParam("ids") @DefaultValue("") String ids
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get employment information by ids={}", userId, ids);
        List<EmploymentInformationBean> activities = employmentInfoService.getEmploymentInfoByIds(ids);
        logger.info("count = {}", activities.size());
        return Response.ok(activities).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteEmploymentInfoByIds(@Context HttpServletRequest request,
                                              @FormParam("ids") @DefaultValue("") String ids
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} delete employment information by ids={}", ids);
        String deleteIds = employmentInfoService.deleteByIds(ids);
        logger.info("ids={}", ids);
        return Response.ok(deleteIds).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response createEmploymentInfo(@Context HttpServletRequest request,
                                         @FormParam("title") @DefaultValue("") String title,
                                         @FormParam("url") @DefaultValue("") String url,
                                         @FormParam("employment_type") @DefaultValue("") String employmentType,
                                         @FormParam("grade") @DefaultValue("0") int grade

    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} create employment information", userId);
        EmploymentInformationBean bean = employmentInfoService.createEmploymentInfo(title, url, grade, employmentType);
        logger.info("employment information is {}", bean);
        return Response.ok(bean).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateEmploymentInfoBasicInfo(@Context HttpServletRequest request,
                                                  @FormParam("employment_info_id") @DefaultValue("0") long employmentInfoId,
                                                  @FormParam("title") @DefaultValue("") String title,
                                                  @FormParam("url") @DefaultValue("") String url,
                                                  @FormParam("grade") @DefaultValue("-1") int grade,
                                                  @FormParam("employment_type") @DefaultValue("") String employmentType

    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update employment information", userId);
        EmploymentInformationBean bean = employmentInfoService.updateEmploymentInfo(employmentInfoId, title, url, grade, employmentType, null, null, null);
        logger.info("employment information is {}", bean);
        return Response.ok(bean).build();
    }

    @Path("/edit/front_cover")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateEmploymentInfoFrontCover(@Context HttpServletRequest request,
                                                   @FormDataParam("employment_info_id") @DefaultValue("0") long employmentInfoId,
                                                   @FormDataParam("file_name") @DefaultValue("") String imageName,
                                                   @FormDataParam("file") InputStream image,
                                                   @FormDataParam("file") FormDataContentDisposition disp

    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update employment information front cover", userId);
        EmploymentInformationBean frontCover = employmentInfoService.updateEmploymentInfo(employmentInfoId, null, null, -1, null, null, imageName, image);
        logger.info("employment information is {}", frontCover);
        return Response.ok(frontCover).build();
    }

    @Path("/edit/status")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateEmploymentInfoStatus(@Context HttpServletRequest request,
                                               @FormParam("employment_info_id") @DefaultValue("-1") long employmentInfoId,
                                               @FormParam("status") @DefaultValue("") String status

    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update employment information status", userId);
        EmploymentInformationBean bean = employmentInfoService.updateEmploymentInfo(employmentInfoId, null, null, -1, null, status, null, null);
        logger.info("employment information is {}", bean);
        return Response.ok(bean).build();
    }
}
