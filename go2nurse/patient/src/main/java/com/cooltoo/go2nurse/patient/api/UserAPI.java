package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.beans.UserDiagnosticPointRelationBean;
import com.cooltoo.go2nurse.beans.UserHospitalizedRelationBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.constants.ProcessStatus;
import com.cooltoo.go2nurse.constants.UserHospitalizedStatus;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.*;
import com.cooltoo.util.VerifyUtil;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/6/13.
 */
@Path("/user")
public class UserAPI {

    private static final Logger logger = LoggerFactory.getLogger(UserAPI.class.getName());

    @Autowired private UserService service;
    @Autowired private UserPatientRelationService userPatientRelationService;
    @Autowired private PatientService patientService;
    @Autowired private UserDiagnosticPointRelationService diagnosticRelationService;
    @Autowired private UserReExaminationDateService reExaminationService;
    @Autowired private UserHospitalizedRelationService userHospitalizedRelationService;

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newUser(@Context HttpServletRequest request,
                            @FormParam("name") @DefaultValue("") String name,
                            @FormParam("gender") @DefaultValue("2") int gender,
                            @FormParam("birthday") @DefaultValue("") String birthday,
                            @FormParam("mobile") @DefaultValue("") String mobile,
                            @FormParam("password") @DefaultValue("") String password,
                            @FormParam("sms_code") @DefaultValue("") String smsCode,
                            @FormParam("channel")  String channel,
                            @FormParam("channelid") String channelid,
                            @FormParam("openid") String openid,
                            @FormParam("has_decide") String hasDecide

    ) {
        UserBean userBean = service.registerUser(name, gender, birthday, mobile, password, smsCode, hasDecide, channel, channelid, openid);

        // add default patient
        PatientBean patientBean = patientService.create(name, gender, userBean.getBirthday(), "", mobile, YesNoEnum.YES, YesNoEnum.YES);
        userPatientRelationService.addPatientToUser(patientBean.getId(), userBean.getId());

        // add HOSPITALIZED_DATE diagnostic point datetime when hasDecide is IN_HOSPITAL
        if (UserHospitalizedStatus.IN_HOSPITAL.equals(userBean.getHasDecide())) {
            List<DiagnosticEnumeration> diagnosticPoints = new ArrayList<>();
            List<Date> pointTimes = new ArrayList<>();
            diagnosticPoints.add(DiagnosticEnumeration.HOSPITALIZED_DATE);
            pointTimes.add(new Date());
            long groupId = System.currentTimeMillis();
            diagnosticRelationService.addUserDiagnosticRelation(userBean.getId(), groupId, diagnosticPoints, pointTimes, false);
        }
        return Response.ok(userBean).build();
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateUser(@Context HttpServletRequest request,
                               @FormParam("name") @DefaultValue("") String name,
                               @FormParam("gender") @DefaultValue("-1") int gender,
                               @FormParam("birthday") @DefaultValue("") String birthday,
                               @FormParam("address") @DefaultValue("") String address,
                               @FormParam("has_decide") @DefaultValue("") String hasDecide
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserBean user = service.updateUser(userId, name, gender, birthday, -1, address, hasDecide);
        logger.info("update user is " + user);
        if (null == user) {
            return Response.ok().build();
        }
        if (user.getHasDecide()!= UserHospitalizedStatus.IN_HOSPITAL) {
            Long groupId = diagnosticRelationService.getUserCurrentGroupId(userId);
            List<UserDiagnosticPointRelationBean> relations = diagnosticRelationService.updateProcessStatusByUserAndGroup(userId, groupId, ProcessStatus.COMPLETED);
            reExaminationService.addReExaminationByDiagnosticDates(userId, relations);

            List<UserHospitalizedRelationBean> hospitalizedRelations = userHospitalizedRelationService.getUserHospitalizedRelationByGroupId(userId, groupId);
            if (!VerifyUtil.isListEmpty(hospitalizedRelations)) {
                for (UserHospitalizedRelationBean tmp : hospitalizedRelations) {
                    userHospitalizedRelationService.updateRelation(tmp.getId(), false, userId, YesNoEnum.YES.name(), null);
                }
            }
        }
        return Response.ok(user).build();
    }

    @Path("/edit_head_photo")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addHeadPhoto(@Context HttpServletRequest request,
                                 @FormDataParam("image_name") @DefaultValue("") String imageName,
                                 @FormDataParam("file") InputStream image,
                                 @FormDataParam("file") FormDataContentDisposition disposition
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserBean user = service.updateProfilePhoto(userId, imageName, image);
        logger.info("upload successfully");
        return Response.ok(user).build();
    }

    @Path("/verify")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response verifySmsCode(@Context HttpServletRequest request,
                                  @FormParam("sms_code") @DefaultValue("") String smsCode,
                                  @FormParam("mobile") @DefaultValue("") String mobile
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        logger.info("verify sms code={}, mobile={}, userId={]", smsCode, mobile, userId);
        UserBean bean = service.validateMobile(userId, smsCode, mobile);
        logger.info("verify sms code, user is " + bean);
        return Response.ok(bean).build();
    }

    @Path("/modify/mobile")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateMobile(@Context HttpServletRequest request,
                                 @FormParam("sms_code") @DefaultValue("") String smsCode,
                                 @FormParam("new_mobile") @DefaultValue("") String newMobile
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserBean bean = service.updateMobile(userId, smsCode, newMobile);
        logger.info("update user mobile is " + bean);
        return Response.ok(bean).build();
    }

    @Path("/modify/password")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updatePassword(@Context HttpServletRequest request,
                                   @FormParam("password") @DefaultValue("") String password,
                                   @FormParam("new_password") @DefaultValue("") String newPassword
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserBean bean = service.updatePassword(userId, password, newPassword);
        logger.info("update user password is " + bean);
        return Response.ok(bean).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUser(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserBean user = service.getUser(userId);
        logger.info("get user is " + user);
        return Response.ok(user).build();
    }
}