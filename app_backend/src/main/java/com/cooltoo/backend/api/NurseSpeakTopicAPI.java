package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakTopicBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseSpeakTopicService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.UserAuthority;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

/**
 * Created by hp on 2016/6/6.
 */
@Path("/nurse/speak/topic")
public class NurseSpeakTopicAPI {

    @Autowired private NurseSpeakTopicService topicService;

    @Path("/speak")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpeaksInTopic(@Context HttpServletRequest request,
                                     @QueryParam("topic_id") @DefaultValue("0") long topicId,
                                     @QueryParam("index") @DefaultValue("0") int pageIndex,
                                     @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<NurseSpeakBean> speaks = topicService.getSpeaksInTopic(topicId, CommonStatus.ENABLED.name(), pageIndex, sizePerPage);
        return Response.ok(speaks).build();
    }

    @Path("/user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersInTopic(@Context HttpServletRequest request,
                             @QueryParam("topic_id") @DefaultValue("0") long topicId,
                             @QueryParam("index") @DefaultValue("0") int pageIndex,
                             @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<NurseBean> nurses = topicService.getUsersInTopic(topicId, UserAuthority.AGREE_ALL.name(), pageIndex, sizePerPage);
        return Response.ok(nurses).build();
    }

    @Path("/edit_image")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response editTopic(@Context HttpServletRequest request,
                              @FormDataParam("topic_id") @DefaultValue("0") long topicId,
                              @FormDataParam("head_image_name") @DefaultValue("") String headImageName,
                              @FormDataParam("head_image") InputStream headImage,
                              @FormDataParam("background_image_name") @DefaultValue("") String backgroundImageName,
                              @FormDataParam("background_image") InputStream backgroundImage
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakTopicBean topic = topicService.updateTopicPhoto(topicId, true, userId, headImageName, headImage, backgroundImageName, backgroundImage);
        return Response.ok(topic).build();
    }

    @Path("/add_click_number")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response editTopic(@Context HttpServletRequest request,
                              @FormParam("topic_id") @DefaultValue("0") long topicId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakTopicBean topic = topicService.updateTopic(topicId, 0, "", "", "", "", 0, "", 1);
        return Response.ok(topic).build();
    }
}
