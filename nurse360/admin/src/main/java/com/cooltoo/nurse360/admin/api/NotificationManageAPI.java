package com.cooltoo.nurse360.admin.api;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.notification.NotifierForAllModule;
import com.cooltoo.nurse360.beans.Nurse360NotificationBean;
import com.cooltoo.nurse360.service.NotificationServiceForNurse360;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.services.CommonNurseHospitalRelationService;
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

@Path("/admin/notification")
public class NotificationManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(NotificationManageAPI.class);

    @Autowired private NotificationServiceForNurse360 notificationService;
    @Autowired private CommonNurseHospitalRelationService nurseHospitalRelationService;
    @Autowired private NotifierForAllModule notifierForAllModule;
    @Autowired private Nurse360Utility utility;

    private List<CommonStatus> getStatuses(String status) {
        List<CommonStatus> statuses = new ArrayList<>();
        CommonStatus eStatus = CommonStatus.parseString(status);
        if (null!=eStatus) {
            statuses.add(eStatus);
        }
        else if ("ALL".equalsIgnoreCase(status)) {
            statuses = CommonStatus.getAll();
            statuses.remove(CommonStatus.DELETED);
        }
        return statuses;
    }

    @Path("/alert/{notification_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response pushNotification(@Context HttpServletRequest request,
                                     @PathParam("notification_id") @DefaultValue("0") long notificationId
    ) {
        Nurse360NotificationBean notification = notificationService.getNotificationById(notificationId, utility.getHttpPrefix());
        if (null!=notification) {
            if (ServiceVendorType.HOSPITAL.equals(notification.getVendorType())) {
                List<Integer> departmentIds = Arrays.asList(new Integer[]{(int)notification.getDepartId()});
                List<Long> nurseIds = nurseHospitalRelationService.getNurseIdByHospitalAndDepartIds((int)notification.getVendorId(), departmentIds);
                notifierForAllModule.newNotificationAlertToNurse360(nurseIds, notificationId, "new", notification.getTitle());
            }
        }
        return Response.ok().build();
    }

    @Path("/{notification_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationById(@Context HttpServletRequest request,
                                        @PathParam("notification_id") @DefaultValue("0") long notificationId
    ) {
        logger.info("get notification by notification id");
        Nurse360NotificationBean notification = notificationService.getNotificationById(notificationId, utility.getHttpPrefix());
        List<HospitalBean> hospitals = notificationService.getHospitalByNotificationId(notificationId);
        List<HospitalDepartmentBean> departments = notificationService.getDepartmentByNotificationId(notificationId);
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
                                      @QueryParam("hospital_id") @DefaultValue("0") int hospitalId,
                                      @QueryParam("department_id") @DefaultValue("") String departmentId,
                                      @QueryParam("status") @DefaultValue("ALL") String status
    ) {
        List<CommonStatus> statuses = getStatuses(status);

        Long lDepartmentId = VerifyUtil.isIds(departmentId) ? VerifyUtil.parseLongIds(departmentId).get(0) : null;
        long count = notificationService.countNotificationByConditions(null, statuses, ServiceVendorType.HOSPITAL, new Long(hospitalId), lDepartmentId);
        logger.info("count = {}", count);
        return Response.ok(count).build();
    }

    // status ==> all/enabled/disabled/deleted
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationByStatus(@Context HttpServletRequest request,
                                            @QueryParam("hospital_id") @DefaultValue("0") int hospitalId,
                                            @QueryParam("department_id") @DefaultValue("") String departmentId,
                                            @QueryParam("status") @DefaultValue("ALL") String status,
                                            @QueryParam("index")  @DefaultValue("0") int index,
                                            @QueryParam("number") @DefaultValue("10") int number
    ) {
        List<CommonStatus> statuses = getStatuses(status);

        Long lDepartmentId = VerifyUtil.isIds(departmentId) ? VerifyUtil.parseLongIds(departmentId).get(0) : null;
        List<Nurse360NotificationBean> notifications = notificationService.getNotificationByConditions(
                null, statuses,
                ServiceVendorType.HOSPITAL, new Long(hospitalId), lDepartmentId,
                index, number
        );
        return Response.ok(notifications).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNotification(@Context HttpServletRequest request,
                                    @FormParam("title") @DefaultValue("") String title,
                                    @FormParam("introduction") @DefaultValue("") String introduction,
                                    @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                    @FormParam("department_ids") @DefaultValue("0") int departmentId,
                                    @FormParam("significance") @DefaultValue("") String strSignificance /* YES, NO */
    ) {
        logger.info("new notification");
        Nurse360NotificationBean notification = notificationService.addNotification(title, introduction, strSignificance, ServiceVendorType.HOSPITAL, hospitalId, departmentId);
        Map<String, Object> retVal = new HashMap<>();
        retVal.put("notification", notification);

        if (null!=notification) {
            long notificationId = notification.getId();

            List<HospitalBean> hospitals = notificationService.getHospitalByNotificationId(notificationId);
            List<HospitalDepartmentBean> departments = notificationService.getDepartmentByNotificationId(notificationId);
            retVal.put("hospital", hospitals);
            retVal.put("department", departments);

            return Response.ok(retVal).build();
        }

        return Response.ok(retVal).build();
    }

    @Path("/edit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateNotification(@Context HttpServletRequest request,
                                       @FormParam("notification_id") @DefaultValue("0") long notificationId,
                                       @FormParam("title") @DefaultValue("") String title,
                                       @FormParam("introduction") @DefaultValue("") String introduction,
                                       @FormParam("hospital_id") @DefaultValue("") String strHospitalId,
                                       @FormParam("department_ids") @DefaultValue("") String strDepartmentIds,
                                       @FormParam("significance") @DefaultValue("") String strSignificance /* yes, no */,
                                       @FormParam("status") @DefaultValue("disabled") String status /* enabled, disabled, deleted */
    ) {
        logger.info("update notification");
        Long vendorId = VerifyUtil.isIds(strHospitalId)   ? VerifyUtil.parseLongIds(strHospitalId).get(0)   : null;
        Long departId = VerifyUtil.isIds(strDepartmentIds)? VerifyUtil.parseLongIds(strDepartmentIds).get(0): null;
        Nurse360NotificationBean notification = notificationService.updateNotification(
                notificationId, title, introduction, null, strSignificance, status,
                ServiceVendorType.HOSPITAL, vendorId, departId);
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
        Nurse360NotificationBean notification = notificationService.updateNotification(notificationId, null, null, content, null, null, null, null, null);
        return Response.ok(notification).build();
    }
}