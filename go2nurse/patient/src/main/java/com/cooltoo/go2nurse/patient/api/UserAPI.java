package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.UserService;
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

/**
 * Created by hp on 2016/6/13.
 */
@Path("/user")
public class UserAPI {

    private static final Logger logger = LoggerFactory.getLogger(UserAPI.class.getName());

    @Autowired private UserService service;

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newUser(@Context HttpServletRequest request,
                             @FormParam("name") @DefaultValue("") String name,
                             @FormParam("gender") @DefaultValue("2") int gender,
                             @FormParam("birthday") @DefaultValue("") String birthday,
                             @FormParam("mobile") @DefaultValue("") String mobile,
                             @FormParam("password") @DefaultValue("") String password,
                             @FormParam("sms_code") @DefaultValue("") String smsCode
    ) {
        UserBean userBean = service.registerUser(name, gender, birthday, mobile, password, smsCode);
        return Response.ok(userBean).build();
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateUser(@Context HttpServletRequest request,
                                @FormParam("name") @DefaultValue("") String name,
                                @FormParam("gender") @DefaultValue("2") int gender,
                                @FormParam("birthday") @DefaultValue("") String birthday,
                                @FormParam("address") @DefaultValue("") String address
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserBean user = service.updateUser(userId, name, gender, birthday, -1, address);
        logger.info("update user is " + user);
        if (null == user) {
            return Response.ok().build();
        }
        return Response.ok(user).build();
    }

    @POST
    @Path("/edit_head_photo")
    @LoginAuthentication(requireUserLogin = true)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
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
    public Response verifySmsCode(@Context HttpServletRequest request,
                                  @FormParam("user_id") @DefaultValue("0") long userId,
                                  @FormParam("sms_code") @DefaultValue("") String smsCode,
                                  @FormParam("mobile") @DefaultValue("") String mobile
    ) {
        logger.info("verify sms code={}, mobile={}, userId={]", smsCode, mobile, userId);
        UserBean bean = service.validateMobile(userId, smsCode, mobile);
        logger.info("verify sms code, user is " + bean);
        return Response.ok(bean).build();
    }

    @Path("/modify/mobile_or_password")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateUser(@Context HttpServletRequest request,
                                @FormParam("new_mobile") @DefaultValue("") String newMobile,
                                @FormParam("new_password") @DefaultValue("") String newPassword
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserBean bean = service.updateMobilePassword(userId, newMobile, newPassword);
        logger.info("update user mobile and password is " + bean);
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