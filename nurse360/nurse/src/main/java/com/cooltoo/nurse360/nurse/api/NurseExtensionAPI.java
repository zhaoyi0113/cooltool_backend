package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.nurse360.beans.Nurse360CourseBean;
import com.cooltoo.nurse360.beans.Nurse360NotificationBean;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.nurse360.service.NurseExtensionServiceForNurse360;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/10/11.
 */
@Path("/nurse/extension")
public class NurseExtensionAPI {

    @Autowired private NurseExtensionServiceForNurse360 nurseExtensionService;

    @Path("/course/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getCourseOfNurse(@Context HttpServletRequest request,
                                     @PathParam("index") @DefaultValue("0") int pageIndex,
                                     @PathParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<Nurse360CourseBean> courses = nurseExtensionService.getCourseByNurseId(nurseId, pageIndex, sizePerPage);
        return Response.ok(courses).build();
    }

    @Path("/course/{course_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getCourseById(@Context HttpServletRequest request,
                                  @PathParam("course_id") @DefaultValue("0") int courseId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        Nurse360CourseBean course = nurseExtensionService.getCourseById(nurseId, courseId);
        return Response.ok(course).build();
    }


    @Path("/notification/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getNotificationOfNurse(@Context HttpServletRequest request,
                                           @PathParam("index") @DefaultValue("0") int pageIndex,
                                           @PathParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<Nurse360NotificationBean> notification = nurseExtensionService.getNotificationByNurseId(nurseId, pageIndex, sizePerPage);
        return Response.ok(notification).build();
    }

    @Path("/notification/{notification_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getNotificationById(@Context HttpServletRequest request,
                                        @PathParam("notification_id") @DefaultValue("0") long notificationId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        Nurse360NotificationBean notification = nurseExtensionService.getNotificationById(nurseId, notificationId);
        return Response.ok(notification).build();
    }


    @Path("/course")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response readCourse(@Context HttpServletRequest request,
                               @FormParam("course_id") @DefaultValue("0") long courseId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        nurseExtensionService.readCourse(nurseId, courseId);
        Map<String, Long> retVal = new HashMap<>();
        retVal.put("courseId", courseId);
        return Response.ok(retVal).build();
    }


    @Path("/notification")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response readNotification(@Context HttpServletRequest request,
                                     @FormParam("notification_id") @DefaultValue("0") long notificationId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        nurseExtensionService.readNotification(nurseId, notificationId);
        Map<String, Long> retVal = new HashMap<>();
        retVal.put("notificationId", notificationId);
        return Response.ok(retVal).build();
    }
}
