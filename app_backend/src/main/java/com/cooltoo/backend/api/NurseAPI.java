package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseService;
import com.cooltoo.constants.ContextKeys;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Path("/nurse")
//@LoginAuthentication(requireNurseLogin = true)
public class NurseAPI {

    private static final Logger logger = Logger.getLogger(NurseAPI.class.getName());

    @Autowired
    NurseService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllNurses() {
        List<NurseBean> all = service.getAll();
        return Response.ok(all).build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newNurse(
            @FormParam("name") String name,
            @FormParam("age") int age,
            @FormParam("gender") int gender,
            @DefaultValue("") @FormParam("mobile") String mobile,
            @FormParam("password") String password,
            @FormParam("sms_code") String smsCode
    ) {
        long id = -1;
//        try {
            id = service.registerNurse(name, age, gender, mobile, password, smsCode);
//        }catch(BadRequestException e){
//            e.printStackTrace();
//            logger.severe(e.getMessage());
//        }
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
        service.addHeadPhoto(userId, fileName, fileInputStream);
        return Response.ok().build();
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
        service.addBackgroundImage(userId, fileName, fileInputStream);
        return Response.ok().build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNurse(@DefaultValue("-1") @PathParam("id") long id) {
        NurseBean one = service.getNurse(id);
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
    public Response updateNurse(
            @Context HttpServletRequest request,
            @FormParam("name") String name,
            @FormParam("age") int age,
            @FormParam("gender") int gender,
            @DefaultValue("") @FormParam("mobile") String mobile,
            @DefaultValue("") @FormParam("identificateId") String identificateId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean one = service.updateNurse(userId, identificateId, name, age, gender, mobile);
        logger.info("update nurse is " + one);
        if (null == one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }
}
