package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.ImagesInSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.SpeakType;
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
import java.util.List;

/**
 * Created by hp on 2016/4/22.
 */
@Path("/admin/speak")
public class OfficialSpeakAPI {

    private static final Logger logger = LoggerFactory.getLogger(OfficialSpeakAPI.class.getName());

    @Autowired
    private NurseSpeakService speakService;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countOfficialSpeak(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("admin {} get official speak count", userId);
        long count = speakService.countSpeak(true, -1, SpeakType.OFFICIAL.name());
        logger.info("count is {}", count);
        return Response.ok(count).build();
    }

    @Path("/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getOfficialSpeak(@Context HttpServletRequest request,
                                     @PathParam("index")  @DefaultValue("0") int index,
                                     @PathParam("number") @DefaultValue("10") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("admin {} get official speak at page {} {}/page", userId, index, number);
        List<NurseSpeakBean> speaks = speakService.getSpeak(true, -1, SpeakType.OFFICIAL.name(), index, number);
        logger.info("speak count is {}", speaks.size());
        return Response.ok(speaks).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response createOfficialSpeak(@Context HttpServletRequest request,
                                        @FormDataParam("content") String content,
                                        @FormDataParam("image_name") String imageName,
                                        @FormDataParam("image") InputStream image,
                                        @FormDataParam("image") FormDataContentDisposition disp
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("admin {} publish official speak: content={} imageName={} image={}, disp={}", userId, content, imageName, image, disp);
        if (VerifyUtil.isStringEmpty(imageName)) {
            imageName = null==disp ? null : disp.getFileName();
        }
        NurseSpeakBean speak = speakService.addOfficial(-1, content, imageName, image);
        logger.info("speak is {}", speak);
        return Response.ok(speak).build();
    }

    @Path("/add_image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateOfficialSpeak(@Context HttpServletRequest request,
                                        @FormDataParam("speak_id") long speakId,
                                        @FormDataParam("image_name") String imageName,
                                        @FormDataParam("image") InputStream image,
                                        @FormDataParam("image") FormDataContentDisposition disp
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("admin {} add image to speak {} : imageName={} image={}, disp={}", userId, speakId, imageName, image, disp);
        if (VerifyUtil.isStringEmpty(imageName)) {
            imageName = null==disp ? null : disp.getFileName();
        }
        ImagesInSpeakBean speakImage = speakService.addImage(-1, speakId, imageName, image);
        logger.info("speak image is {}", speakImage);
        return Response.ok(speakImage).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteOfficialSpeak(@Context HttpServletRequest request,
                                        @FormParam("speak_ids") String speakIds
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("admin {} delete official speak by ids {}", userId, speakIds);
        List<NurseSpeakBean> speakDeleted = speakService.deleteByIds(-1, speakIds);
        logger.info("delete count is {}", speakDeleted.size());
        return Response.ok(speakDeleted).build();
    }
}
