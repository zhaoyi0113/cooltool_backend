package com.cooltoo.nurse360.admin.api;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.beans.Nurse360NotificationBean;
import com.cooltoo.nurse360.service.NotificationHospitalRelationServiceForNurse360;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/admin/notification")
public class NotificationManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(NotificationManageAPI.class);

    @Autowired private NotificationServiceForNurse360 notificationService;
    @Autowired private NotificationHospitalRelationServiceForNurse360 notificationHospitalRelationService;


    @Path("/{notification_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationById(@Context HttpServletRequest request,
                                          @PathParam("notification_id") @DefaultValue("0") long notificationId
    ) {
        logger.info("get notification by notification id");
        Nurse360NotificationBean notification = notificationService.getNotificationById(notificationId);
        List<HospitalBean> hospitals = notificationHospitalRelationService.getHospitalByNotificationId(notificationId, CommonStatus.ENABLED.name());
        List<HospitalDepartmentBean> departments = notificationHospitalRelationService.getDepartmentByNotificationId(notificationId, CommonStatus.ENABLED.name());
        Map<String, Object> retVal = new HashMap<>();
        retVal.put("notification", notification);
        retVal.put("hospital", hospitals);
        retVal.put("department", departments);
        return Response.ok(retVal).build();
    }

    // status ==> all/enabled/disabled/deleted
    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countNotification(@Context HttpServletRequest request,
                                      @QueryParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                      @QueryParam("department_id") @DefaultValue("") String strDepartmentId,
                                      @QueryParam("status") @DefaultValue("ALL") String status
    ) {
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        List<Long> notificationIds = notificationHospitalRelationService.getNotificationInHospitalAndDepartment(hospitalId, departmentId, status);
        notificationIds = notificationService.getNotificationIdByStatusAndIds(status, notificationIds);
        int count = VerifyUtil.isListEmpty(notificationIds) ? 0 : notificationIds.size();
        logger.info("count = {}", count);
        return Response.ok(count).build();
    }

    // status ==> all/enabled/disabled/deleted
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationByStatus(@Context HttpServletRequest request,
                                            @QueryParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                            @QueryParam("department_id") @DefaultValue("") String strDepartmentId,
                                            @QueryParam("status") @DefaultValue("ALL") String status,
                                            @QueryParam("index")  @DefaultValue("0") int index,
                                            @QueryParam("number") @DefaultValue("10") int number
    ) {
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        List<Long> notificationIds = notificationHospitalRelationService.getNotificationInHospitalAndDepartment(hospitalId, departmentId, status);
        notificationIds = notificationService.getNotificationIdByStatusAndIds(status, notificationIds);
        List<Nurse360NotificationBean> notifications = notificationService.getNotificationByIds(notificationIds, index, number);
        return Response.ok(notifications).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNotification(@Context HttpServletRequest request,
                                    @FormParam("title") @DefaultValue("") String title,
                                    @FormParam("introduction") @DefaultValue("") String introduction,
                                    @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                    @FormParam("department_ids") @DefaultValue("") String strDepartmentIds,
                                    @FormParam("significance") @DefaultValue("") String strSignificance /* YES, NO */
    ) {
        logger.info("new notification");
        Nurse360NotificationBean notification = notificationService.addNotification(title, introduction, strSignificance);
        logger.info("notification is {}", notification);
        if (null!=notification) {
            long notificationId = notification.getId();
            List<Integer> departmentIds = VerifyUtil.parseIntIds(strDepartmentIds);
            notificationHospitalRelationService.setNotificationToHospital(notificationId, hospitalId, departmentIds);
        }
        if (null!=notification) {
            long notificationId = notification.getId();
            List<HospitalBean> hospitals = notificationHospitalRelationService.getHospitalByNotificationId(notificationId, CommonStatus.ENABLED.name());
            List<HospitalDepartmentBean> departments = notificationHospitalRelationService.getDepartmentByNotificationId(notificationId, CommonStatus.ENABLED.name());
            Map<String, Object> retVal = new HashMap<>();
            retVal.put("notification", notification);
            retVal.put("hospital", hospitals);
            retVal.put("department", departments);
            return Response.ok(retVal).build();
        }
        return Response.ok(notification).build();
    }

    @Path("/edit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateNotification(@Context HttpServletRequest request,
                                       @FormParam("notification_id") @DefaultValue("0") long notificationId,
                                       @FormParam("title") @DefaultValue("") String title,
                                       @FormParam("introduction") @DefaultValue("") String introduction,
                                       @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                       @FormParam("department_ids") @DefaultValue("") String strDepartmentIds,
                                       @FormParam("significance") @DefaultValue("") String strSignificance /* yes, no */,
                                       @FormParam("status") @DefaultValue("disabled") String status /* enabled, disabled, deleted */
    ) {
        logger.info("update notification");
        Nurse360NotificationBean notification = notificationService.updateNotification(notificationId, title, introduction, null, strSignificance, status);
        logger.info("notification is {}", notification);
        if (null!=notification) {
            List<Integer> departmentIds = VerifyUtil.parseIntIds(strDepartmentIds);
            notificationHospitalRelationService.setNotificationToHospital(notificationId, hospitalId, departmentIds);
        }
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
        Nurse360NotificationBean notification = notificationService.updateNotification(notificationId, null, null, content, null, null);
        return Response.ok(notification).build();
    }
}