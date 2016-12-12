package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.UserPatientRelationBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.service.PatientService;
import com.cooltoo.go2nurse.service.UserPatientRelationService;
import com.cooltoo.util.NumberUtil;
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
 * Created by yzzhao on 2/29/16.
 */
@Path("/patient")
public class PatientAPI {

    private static final Logger logger = LoggerFactory.getLogger(PatientAPI.class);

    @Autowired
    private PatientService service;
    @Autowired
    private UserPatientRelationService userPatientRelation;

    @Autowired
    private WeChatService weChatService;

    @Path("/get_patient")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getPatientWithUserId(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<Long> patientIds = userPatientRelation.getPatientByUser(userId, CommonStatus.ENABLED.name());
        List<PatientBean> patients = service.getAllByStatusAndIds(patientIds, CommonStatus.ENABLED);
        return Response.ok(patients).build();
    }

    @Path("/get_by_ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getByPatientsId(@Context HttpServletRequest request,
                                    @QueryParam("patients_id") @DefaultValue("") String strPatientsId
    ) {
        List<Long> patientsId = VerifyUtil.parseLongIds(strPatientsId);
        List<PatientBean> beans;
        if (!VerifyUtil.isListEmpty(patientsId)) {
            beans = service.getAllByStatusAndIds(patientsId, CommonStatus.ENABLED);
        } else {
            beans = new ArrayList<>();
        }
        logger.info("patient count is {}", beans.size());
        return Response.ok(beans).build();
    }

    @Path("/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response create(@Context HttpServletRequest request,
                           @FormParam("name") @DefaultValue("") String name,
                           @FormParam("gender") @DefaultValue("2") int gender,
                           @FormParam("birthday") @DefaultValue("") String strBirthday,
                           @FormParam("identityCard") @DefaultValue("") String identityCard,
                           @FormParam("mobile") @DefaultValue("") String mobile
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Date date = null;
        long time = NumberUtil.getTime(strBirthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (time > 0) {
            date = new Date(time);
        }

        List<Long> usersPatient = userPatientRelation.getPatientByUser(userId, CommonStatus.ENABLED.name());
        PatientBean patient = service.create(name, gender, date, identityCard, mobile, VerifyUtil.isListEmpty(usersPatient) ? YesNoEnum.YES : null, null);
        if (null != patient && patient.getId() > 0) {
            UserPatientRelationBean relation = userPatientRelation.addPatientToUser(patient.getId(), userId);
            logger.info("user patient relation is {}", relation);
        }
        return Response.ok(patient).build();
    }

    @Path("/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response update(@Context HttpServletRequest request,
                           @FormParam("id") @DefaultValue("-1") long patientId,
                           @FormParam("name") @DefaultValue("") String name,
                           @FormParam("gender") @DefaultValue("-1") int gender,
                           @FormParam("birthday") @DefaultValue("") String strBirthday,
                           @FormParam("identityCard") @DefaultValue("") String identityCard,
                           @FormParam("mobile") @DefaultValue("") String mobile,
                           @FormParam("is_default") @DefaultValue("") String strIsDefault,
                           @FormParam("is_self") @DefaultValue("") String strIsSelf,
                           @FormParam("status") @DefaultValue("") String status
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Date birthday = null;
        long time = NumberUtil.getTime(strBirthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (time > 0) {
            birthday = new Date(time);
        }
        YesNoEnum isDefault = YesNoEnum.parseString(strIsDefault);
        YesNoEnum isSelf = YesNoEnum.parseString(strIsSelf);
        PatientBean one = service.update(userId, patientId, name, gender, birthday, identityCard, mobile, isDefault, isSelf, status);
        return Response.ok(one).build();
    }

    @Path("/header_image")
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addHeadPhoto(@Context HttpServletRequest request,
                                 @FormDataParam("patient_id") @DefaultValue("0") String strPatientId,
                                 @FormDataParam("image_name") @DefaultValue("") String imageName,
                                 @FormDataParam("file") InputStream image,
                                 @FormDataParam("file") FormDataContentDisposition disposition
    ) {
        long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : 0L;
        PatientBean patient = service.updateHeaderImage(patientId, imageName, image);
        logger.info("upload successfully");
        return Response.ok(patient).build();
    }

    @Path("/header_image_wx")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addHeadPhotoFromWx(@Context HttpServletRequest request,
                                       @FormParam("patient_id") @DefaultValue("0") String strPatientId,
                                       @FormParam("image_name") @DefaultValue("") String imageName,
                                       @FormParam("image_id") String imageId,
                                       @FormParam("app_id") String appId
    ) {
        long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : 0L;
        InputStream image = weChatService.downloadImageFromWxWithAppid(appId, imageId);
        PatientBean patient = service.updateHeaderImage(patientId, imageName, image);
        logger.info("upload successfully");
        return Response.ok(patient).build();
    }

}
