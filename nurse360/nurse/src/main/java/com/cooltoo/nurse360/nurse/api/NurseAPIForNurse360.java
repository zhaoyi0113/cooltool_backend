package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.nurse360.service.NurseServiceForNurse360;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Created by zhaolisong on 16/9/28.
 */
@Path("/nurse")
public class NurseAPIForNurse360 {

    @Autowired private NurseServiceForNurse360 nurseServiceForNurse360;

    @Path("/register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response newUser(@Context HttpServletRequest request,
                            @FormParam("real_name") @DefaultValue("") String realName,
                            @FormParam("birthday") @DefaultValue("0") int age,
                            @FormParam("gender") @DefaultValue("2") int gender,
                            @FormParam("mobile") @DefaultValue("") String mobile,
                            @FormParam("password") @DefaultValue("") String password,
                            @FormParam("sms_code") @DefaultValue("") String smsCode,
                            @FormParam("hospital_id")  int hospitalId,
                            @FormParam("department_id") int departmentId,
                            @FormParam("job_title") String jobTitle

    ) {
        long nurseId = nurseServiceForNurse360.addNurse(realName, age, gender, mobile, password, smsCode);
        nurseServiceForNurse360.setHospitalDepartmentAndJobTitle(nurseId, hospitalId, departmentId, jobTitle);
        return Response.ok(nurseId).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getNurseInfomation(@Context HttpServletRequest request) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean nurse = nurseServiceForNurse360.getNurseById(nurseId);
        return Response.ok(nurse).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response editNurseInformation(@Context HttpServletRequest request,
                                         @FormParam("real_name") @DefaultValue("") String realName,
                                         @FormParam("birthday") @DefaultValue("0") int age,
                                         @FormParam("gender") @DefaultValue("2") int gender,
                                         @FormParam("hospital_id")  int hospitalId,
                                         @FormParam("department_id") int departmentId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        nurseServiceForNurse360.editNurse(nurseId, realName, age, gender, null, null, null, null);
        nurseServiceForNurse360.setHospitalDepartmentAndJobTitle(nurseId, hospitalId, departmentId, null);
        NurseBean bean = nurseServiceForNurse360.getNurseById(nurseId);
        return Response.ok(bean).build();
    }

    @Path("/head_image")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response editNurseHeadImage(@Context HttpServletRequest request,
                                       @FormDataParam("image_name") @DefaultValue("") String imageName,
                                       @FormDataParam("image") InputStream image,
                                       @FormDataParam("image")FormDataContentDisposition disp
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean bean = nurseServiceForNurse360.editNurse(nurseId, null, -1, -1, imageName, image, null, null);
        return Response.ok(bean).build();
    }

    @Path("/back_image")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response editNurseBackImage(@Context HttpServletRequest request,
                                       @FormDataParam("image_name") @DefaultValue("") String imageName,
                                       @FormDataParam("image") InputStream image,
                                       @FormDataParam("image")FormDataContentDisposition disp
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean bean = nurseServiceForNurse360.editNurse(nurseId, null, -1, -1, null, null, imageName, image);
        return Response.ok(bean).build();
    }

    @Path("/mobile")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response updateMobile(@Context HttpServletRequest request,
                                 @FormParam("smscode") String smsCode,
                                 @FormParam("mobile") String newMobile) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean bean = nurseServiceForNurse360.modifyMobile(nurseId, smsCode, newMobile);
        return Response.ok(bean).build();
    }

    @Path("/password")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response updatePassword(@Context HttpServletRequest request,
                                   @FormParam("password") String password,
                                   @FormParam("new_password") String newPassword) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean bean = nurseServiceForNurse360.modifyPassword(nurseId, password, newPassword);
        return Response.ok(bean).build();
    }

    @Path("/password/reset")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(@Context HttpServletRequest request,
                                  @FormParam("smscode") String smsCode,
                                  @FormParam("mobile") String mobile,
                                  @FormParam("new_password") String newPassword) {
        NurseBean bean = nurseServiceForNurse360.resetPassword(smsCode, mobile, newPassword);
        return Response.ok(bean).build();
    }
}
