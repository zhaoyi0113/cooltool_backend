package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.SpeakTypeBean;
import com.cooltoo.backend.services.SpeakTypeService;
import com.cooltoo.constants.ContextKeys;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.security.interfaces.RSAKey;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Path("/admin/speak_type")
public class SpeakTypeAPI {

    private static final Logger logger = Logger.getLogger(SpeakTypeAPI.class.getName());

    @Autowired
    private SpeakTypeService speakTypeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllSpeakTyps(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user : " + userId +" get all speak type");
        List<SpeakTypeBean> speakTypes = speakTypeService.getAllSpeakType();
        return Response.ok(speakTypes).build();
    }

    @POST
    @Path("/edit")
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editBaseInformation(@Context HttpServletRequest request,
                                        @FormParam("id") @DefaultValue("-1") int id,
                                        @FormParam("name") String name,
                                        @FormParam("factor") int factor) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user : " + userId +" edit speak type name : " + name + "  factor: " + factor);
        speakTypeService.updateSpeakType(id, name, factor, null, null);
        return Response.ok().build();
    }

    @POST
    @Path("/edit_image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editImage(@Context HttpServletRequest request,
                              @FormDataParam("id") @DefaultValue("-1") int id,
                              @FormDataParam("image") InputStream image) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user : " + userId +" edit speak type enable image : " + (image!=null));
        speakTypeService.updateSpeakType(id, null, -1, image, null);
        return Response.ok().build();
    }

    @POST
    @Path("/edit_disable_image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editDisableImage(@Context HttpServletRequest request,
                                        @FormDataParam("id") @DefaultValue("-1") int id,
                                        @FormDataParam("image") InputStream image) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user : " + userId +" edit speak type disable image: " + (image!=null));
        speakTypeService.updateSpeakType(id, null, -1, null, image);
        return Response.ok().build();
    }
}
