package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseRelationshipBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseRelationshipService;
import com.cooltoo.backend.services.NurseService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Path("/nurse")
public class NurseAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseAPI.class.getName());

    @Autowired NurseService service;
    @Autowired NurseRelationshipService relationService;

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newNurse(
            @FormParam("name") String name,
            @FormParam("age") int age,
            @DefaultValue("-1") @FormParam("gender") int gender,
            @DefaultValue("")   @FormParam("mobile") String mobile,
            @FormParam("password") String password,
            @FormParam("sms_code") String smsCode
    ) {
        long id = service.registerNurse(name, age, gender, mobile, password, smsCode);
        return Response.ok(id).build();
    }

    @POST
    @Path("/add_head_photo")
    @LoginAuthentication(requireNurseLogin = true)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addHeadPhoto(
            @Context HttpServletRequest request,
            @FormDataParam("file_name") String fileName,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition disposition) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        String path = service.updateHeadPhoto(userId, fileName, fileInputStream);
        logger.info("upload successfully");
        return Response.ok(path).build();
    }

    @POST
    @Path("/add_background_image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addBackgroundImage(
            @Context HttpServletRequest request,
            @FormDataParam("file_name") String fileName,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition disposition) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        String path = service.updateBackgroundImage(userId, fileName, fileInputStream);
        logger.info("return background path "+path);
        return Response.ok(path).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getNurse(@Context HttpServletRequest request,
                             @DefaultValue("-1") @PathParam("id") long id) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean one = service.getNurse(id);
        List<NurseRelationshipBean> relations = relationService.getRelation(false, id, userId, "", CommonStatus.ENABLED.name());
        one.setRelationshipToRequester(relations);
        logger.info("get nurse is " + one);
        return Response.ok(one).build();
    }

    @GET
    @Path("/information")
    @LoginAuthentication(requireNurseLogin = true)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNurseInformation(@Context HttpServletRequest request){
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean one = service.getNurse(userId);
        logger.info("get nurse is " + one);
        
        return Response.ok(one).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteNurse(@DefaultValue("-1") @FormParam("id") long id) {
        NurseBean one = service.deleteNurse(id);
        logger.info("delete nurse is " + one);
        if (null == one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response updateNurse(
            @Context HttpServletRequest request,
            @FormParam("name") String name,
            @FormParam("age") int age,
            @DefaultValue("-1") @FormParam("gender") int gender
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean one = service.updateNurse(userId, name, age, gender);
        logger.info("update nurse is " + one);
        if (null == one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @POST
    @Path("/update/mobile_password")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response updateNurse(@Context HttpServletRequest request,
                                @FormParam("smscode") String smsCode,
                                @FormParam("mobile") String newMobile,
                                @FormParam("password") String password,
                                @FormParam("new_password") String newPassword) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean bean = service.updateMobilePassword(userId, smsCode, null, newMobile, password, newPassword);
        logger.info("update nurse mobile and password is " + bean);
        return Response.ok(bean).build();
    }

    @Path("/update/mobile")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response updateMobile(@Context HttpServletRequest request,
                                 @FormParam("smscode") String smsCode,
                                 @FormParam("mobile") String newMobile) {
        logger.info("update nurse mobile is {}.", newMobile);
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean bean = service.updateMobilePassword(userId, smsCode, null, newMobile, null, null);
        logger.info("update nurse mobile is {}.", bean);
        return Response.ok(bean).build();
    }

    @Path("/update/password")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response updatePassword(@Context HttpServletRequest request,
                                   @FormParam("smscode") String smsCode,
                                   @FormParam("password") String password,
                                   @FormParam("new_password") String newPassword) {
        logger.info("update nurse password is {}.", newPassword);
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean bean = service.updateMobilePassword(userId, smsCode, null, null, password, newPassword);
        logger.info("update nurse password is {}.", bean);
        return Response.ok(bean).build();
    }

    @Path("/update/reset_password")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(@Context HttpServletRequest request,
                                  @FormParam("smscode") String smsCode,
                                  @FormParam("mobile") String mobile,
                                  @FormParam("new_password") String newPassword) {
        logger.info("reset nurse password is {}.", newPassword);
        NurseBean bean = service.updateMobilePassword(-1, smsCode, mobile, null, null, newPassword);
        logger.info("reset nurse password is {}.", bean);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/short_note")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response setShortNote(@Context HttpServletRequest request,
                                 @FormParam("short_note") String shortNote) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean one = service.updateShortNote(userId, shortNote);
        logger.info("set short note " + one);
        if (null == one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @POST
    @Path("/realname_identification")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response setRealNameAndIdentification(@Context HttpServletRequest request,
                                                 @FormParam("realName") String realName,
                                                 @FormParam("identification") String identification
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean one = service.updateRealNameAndIdentification(userId, realName, identification);
        logger.info("set real name and identification " + one);
        if (null == one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }
}
