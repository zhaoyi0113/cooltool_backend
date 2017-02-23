package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.beans.*;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.notification.NotifierForAllModule;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.beans.Nurse360NotificationBean;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import com.cooltoo.nurse360.service.NotificationServiceForNurse360;
import com.cooltoo.nurse360.service.NurseServiceForNurse360;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import com.cooltoo.util.HtmlParser;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 05/01/2017.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalNotificationAPI {

    @Autowired private NurseServiceForNurse360 nurseService;
    @Autowired private NotificationServiceForNurse360 notificationService;
    @Autowired private CommonNurseHospitalRelationService nurseHospitalRelationService;
    @Autowired private NotifierForAllModule notifierForAllModule;
    @Autowired private Nurse360Utility utility;


    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    @RequestMapping(path = "/notification/creators", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<NurseBean> getNotification(HttpServletRequest request) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (canModifyNotification(userDetails)) {
            Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment("", "", userDetails);
            Integer hospitalId   = tmp[0];
            Integer departmentId = tmp[1];
            List<NurseBean> nurses = nurseService.getNurseInDepartment(hospitalId, departmentId);
            nurses = filterNurseCanModifyNotification(nurses);
            return nurses;
        }
        throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
    }

    @RequestMapping(path = "/notification/{notification_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public Map<String, Object> getNotification(HttpServletRequest request,
                                               @PathVariable long notification_id
    ) {
        Nurse360NotificationBean notification = notificationService.getNotificationById(notification_id, utility.getHttpPrefix());
        List<HospitalBean> hospitals = notificationService.getHospitalByNotificationId(notification_id);
        List<HospitalDepartmentBean> departments = notificationService.getDepartmentByNotificationId(notification_id);
        Map<String, Object> retVal = new HashMap<>();
        retVal.put("notification", notification);
        retVal.put("hospital", hospitals);
        retVal.put("department", departments);
        return retVal;
    }


    @RequestMapping(path = "/notification/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countNotifications(HttpServletRequest request) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        List<CommonStatus> statuses = Arrays.asList(new CommonStatus[]{CommonStatus.DISABLED, CommonStatus.ENABLED});
        long count = notificationService.countNotificationByConditions(null, statuses, ServiceVendorType.HOSPITAL, hospitalId, departmentId);
        return count;
    }


    @RequestMapping(path = "/notification", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<Nurse360NotificationBean> getNotifications(HttpServletRequest request,
                                                           @RequestParam("index")  int pageIndex,
                                                           @RequestParam("number") int pageNumber
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        List<CommonStatus> statuses = Arrays.asList(new CommonStatus[]{CommonStatus.DISABLED, CommonStatus.ENABLED});
        List<Nurse360NotificationBean> result = notificationService.getNotificationByConditions(null, statuses, ServiceVendorType.HOSPITAL, hospitalId, departmentId, pageIndex, pageNumber);
        return result;
    }


    @RequestMapping(path = "/notification/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Map<String, Object> addNotification(HttpServletRequest request,
                                               @RequestParam(defaultValue = "",  name = "title")        String title,
                                               @RequestParam(defaultValue = "",  name = "introduction") String introduction,
                                               @RequestParam(defaultValue = "NO",name = "significance") String strSignificance, /* YES, NO */
                                               @RequestParam(defaultValue = "",  name = "creator_id")   String creatorId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        if (canModifyNotification(userDetails)) {
            Long lCreatorId = VerifyUtil.isIds(creatorId) ? VerifyUtil.parseLongIds(creatorId).get(0) : userDetails.getId();
            Nurse360NotificationBean notification = notificationService.addNotification(
                    title, introduction, strSignificance,
                    ServiceVendorType.HOSPITAL,
                    hospitalId, departmentId, lCreatorId);
            Map<String, Object> retVal = new HashMap<>();
            retVal.put("notification", notification);
            if (null!=notification) {
                long notificationId = notification.getId();

                List<HospitalBean> hospitals = notificationService.getHospitalByNotificationId(notificationId);
                List<HospitalDepartmentBean> departments = notificationService.getDepartmentByNotificationId(notificationId);
                retVal.put("hospital", hospitals);
                retVal.put("department", departments);

                return retVal;
            }
        }
        throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
    }


    @RequestMapping(path = "/notification/edit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public Nurse360NotificationBean updateNotification(HttpServletRequest request,
                                                       @RequestParam(defaultValue = "0",name = "notification_id") long notificationId,
                                                       @RequestParam(defaultValue = "", name = "title")  String title,
                                                       @RequestParam(defaultValue = "", name = "introduction")  String introduction,
                                                       @RequestParam(defaultValue = "", name = "significance") String strSignificance /* yes, no */,
                                                       @RequestParam(defaultValue = "", name = "status") String status /* enabled, disabled, deleted */
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        if (canModifyNotification(userDetails)) {
            Nurse360NotificationBean notification = notificationService.getNotificationById(notificationId, "");
            if (ServiceVendorType.HOSPITAL.equals(notification.getVendorType())
                    && notification.getVendorId() == hospitalId
                    && notification.getDepartId() == departmentId) {
                notification = notificationService.updateNotification(
                        notificationId, title, introduction, null, strSignificance, status,
                        ServiceVendorType.HOSPITAL, hospitalId, departmentId);
                return notification;
            }
            throw new BadRequestException(ErrorCode.NURSE360_NOTIFICATION_NOT_BELONG_TO_YOU);
        }
        throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
    }


    // 课程为 editing 时, 向课程中添加图片，图片缓存在临时文件夹
    @RequestMapping(path = "/notification/content/add/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.MULTIPART_FORM_DATA)
    public String addImage2Temporary(HttpServletRequest request,
                                       @RequestParam(defaultValue = "0", name = "notification_id")  long          notificationId,
                                       @RequestParam(defaultValue = "",  name = "image_name")       String        imageName,
                                       @RequestPart(required = true,     name = "image")            MultipartFile image

    ) throws IOException {
        String relativePath = notificationService.createTemporaryFile(notificationId, imageName, image.getInputStream());
        int errorNo = 0;
        if (VerifyUtil.isStringEmpty(relativePath)) {
            errorNo = -1;
        }
        relativePath = HtmlParser.constructUrl(utility.getHttpPrefix(), relativePath);
        StringBuilder retVal = new StringBuilder();
        retVal.append("{")
                .append("\"error\":").append(errorNo).append(",")
                .append("\"url\":\"").append(relativePath).append("\"")
                .append("}");
        return retVal.toString();
    }


    @RequestMapping(path = "/notification/content/edit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public Nurse360NotificationBean updateNotification(HttpServletRequest request,
                                                       @RequestParam(defaultValue = "0",name = "notification_id") long notificationId,
                                                       @RequestParam(defaultValue = "", name = "content")       String content
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        if (canModifyNotification(userDetails)) {
            Nurse360NotificationBean notification = notificationService.getNotificationById(notificationId, "");
            if (ServiceVendorType.HOSPITAL.equals(notification.getVendorType())
             && notification.getVendorId() == hospitalId
             && notification.getDepartId() == departmentId) {
                notification = notificationService.updateNotificationContent(notificationId, content);
                return notification;
            }
            throw new BadRequestException(ErrorCode.NURSE360_NOTIFICATION_NOT_BELONG_TO_YOU);
        }
        throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
    }


    @RequestMapping(path = "/notification/alert/{notification_id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public void pushNotification(HttpServletRequest request,
                                     @PathVariable long notification_id
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        if (canModifyNotification(userDetails)) {
            Nurse360NotificationBean notification = notificationService.getNotificationById(notification_id, "");
            if (ServiceVendorType.HOSPITAL.equals(notification.getVendorType())
                    && notification.getVendorId() == hospitalId
                    && notification.getDepartId() == departmentId) {
                List<Integer> departmentIds = Arrays.asList(new Integer[]{(int) notification.getDepartId()});
                List<Long> nurseIds = nurseHospitalRelationService.getNurseIdByHospitalAndDepartIds((int) notification.getVendorId(), departmentIds);
                notifierForAllModule.newNotificationAlertToNurse360(nurseIds, notification_id, "new", notification.getTitle());
            }
        }
        return;
    }

    private boolean canModifyNotification(HospitalAdminUserDetails userDetails) {
        if (null==userDetails) { return false; }
        if (userDetails.isAdmin()) { return false; }

        NurseBean nurse = (NurseBean) userDetails.getUserBean();
        NurseAuthorizationBean authorization = (NurseAuthorizationBean) nurse.getProperty(NurseBean.AUTHORIZATION);
        if (userDetails.isNurseManager() || (userDetails.isNurse()
                && null != authorization
                && YesNoEnum.YES.equals(authorization.getAuthNotificationHeadNurse()))) {
            return true;
        }
        return false;
    }

    private List<NurseBean> filterNurseCanModifyNotification(List<NurseBean> nurses) {
        if (null==nurses) { return nurses; }
        if (nurses.isEmpty()) { return nurses; }

        for (int i = 0; i < nurses.size(); i ++) {
            NurseBean nurse = nurses.get(i);

            UserAuthority authority = nurse.getAuthority();
            if (!UserAuthority.AGREE_ALL.equals(authority)) {
                nurses.remove(i);
                i--;
                continue;
            }

            NurseExtensionBean extension = (NurseExtensionBean) nurse.getProperty(NurseBean.INFO_EXTENSION);
            boolean isNurseManager = (null!=extension && YesNoEnum.YES.equals(extension.getIsManager()));
            if (isNurseManager) {
                continue;
            }

            NurseAuthorizationBean authorization = (NurseAuthorizationBean) nurse.getProperty(NurseBean.AUTHORIZATION);
            if (null != authorization && YesNoEnum.YES.equals(authorization.getAuthNotificationHeadNurse())) {
                continue;
            }

            nurses.remove(i);
            i--;
        }

        return nurses;
    }
}
