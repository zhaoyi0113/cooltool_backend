package com.cooltoo.nurse360.admin.api;

import com.cooltoo.nurse360.beans.Nurse360NotificationBean;
import com.cooltoo.nurse360.service.NotificationServiceForNurse360;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/admin/notification")
public class NotificationManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(NotificationManageAPI.class);

    @Autowired private NotificationServiceForNurse360 notificationService;


    @Path("/{notification_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationById(@Context HttpServletRequest request,
                                          @PathParam("notification_id") @DefaultValue("0") long notificationId
    ) {
        logger.info("get notification by notification id");
        Nurse360NotificationBean notification = notificationService.getNotificationById(notificationId);
        return Response.ok(notification).build();
    }

    // status ==> all/enabled/disabled/deleted
    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countNotification(@Context HttpServletRequest request,
                                      @QueryParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                      @QueryParam("department_id") @DefaultValue("0") int departmentId,
                                      @QueryParam("status") @DefaultValue("") String status
    ) {
        logger.info("get notification count by status={}", status);
        long count = notificationService.countByHospitalDepartmentStatus(hospitalId, departmentId, status);
        logger.info("count = {}", count);
        return Response.ok(count).build();
    }

    // status ==> all/enabled/disabled/deleted
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationByStatus(@Context HttpServletRequest request,
                                            @QueryParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                            @QueryParam("department_id") @DefaultValue("0") int departmentId,
                                            @QueryParam("status") @DefaultValue("") String status,
                                            @QueryParam("index")  @DefaultValue("0") int index,
                                            @QueryParam("number") @DefaultValue("10") int number
    ) {
        logger.info("get notification by status={} at page={}, {}/page", status, index, number);
        List<Nurse360NotificationBean> categories = notificationService.getNotificationByHospitalDepartmentStatus(hospitalId, departmentId, status, index, number);
        logger.info("count = {}", categories.size());
        return Response.ok(categories).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNotification(@Context HttpServletRequest request,
                                    @FormParam("title") @DefaultValue("") String title,
                                    @FormParam("introduction") @DefaultValue("") String introduction,
                                    @FormParam("hospital_id") @DefaultValue("") String strHospitalId,
                                    @FormParam("department_id") @DefaultValue("") String strDepartmentId,
                                    @FormParam("significance") @DefaultValue("") String strSignificance /* YES, NO */
    ) {
        logger.info("new notification");
        Integer hospitalId = VerifyUtil.isIds(strHospitalId) ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        Nurse360NotificationBean notification = notificationService.addNotification(title, introduction, hospitalId, departmentId, strSignificance);
        return Response.ok(notification).build();
    }

    @Path("/edit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateNotification(@Context HttpServletRequest request,
                                       @FormParam("notification_id") @DefaultValue("0") long notificationId,
                                       @FormParam("title") @DefaultValue("") String title,
                                       @FormParam("introduction") @DefaultValue("") String introduction,
                                       @FormParam("hospital_id") @DefaultValue("") String strHospitalId,
                                       @FormParam("department_id") @DefaultValue("") String strDepartmentId,
                                       @FormParam("significance") @DefaultValue("") String strSignificance /* yes, no */,
                                       @FormParam("status") @DefaultValue("disabled") String status /* enabled, disabled, deleted */
    ) {
        logger.info("update notification");
        Integer hospitalId = VerifyUtil.isIds(strHospitalId) ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        Nurse360NotificationBean notification = notificationService.updateNotification(notificationId, title, introduction, null, hospitalId, departmentId, strSignificance, status);
        return Response.ok(notification).build();
    }

    @Path("/edit/content")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateNotification(@Context HttpServletRequest request,
                                       @FormParam("notification_id") @DefaultValue("0") long notificationId,
                                       @FormParam("content") @DefaultValue("") String content
    ) {
        logger.info("update notification content");
        Nurse360NotificationBean notification = notificationService.updateNotification(notificationId, null, null, content, null, null, null, null);
        return Response.ok(notification).build();
    }
}