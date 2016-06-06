package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakTopicBean;
import com.cooltoo.backend.services.NurseSpeakTopicService;
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
@Path("/admin/user_speak/topic")
public class UserSpeakTopicManagerAPI {

    @Autowired
    private NurseSpeakTopicService topicService;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countTopic(@Context HttpServletRequest request,
                               @QueryParam("title") @DefaultValue("") String titleLike,
                               @QueryParam("status") @DefaultValue("") String status) {
        long count = topicService.countTopic(titleLike, status);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getTopic(@Context HttpServletRequest request,
                             @QueryParam("title") @DefaultValue("") String titleLike,
                             @QueryParam("status") @DefaultValue("") String status,
                             @QueryParam("index") @DefaultValue("0") int pageIndex,
                             @QueryParam("number") @DefaultValue("10") int sizePerPage) {
        List<NurseSpeakTopicBean> topics = topicService.getTopic(titleLike, status, pageIndex, sizePerPage);
        return Response.ok(topics).build();
    }

    @Path("/speak/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countSpeaksInTopic(@Context HttpServletRequest request,
                                       @QueryParam("topic_id") @DefaultValue("0") long topicId,
                                       @QueryParam("speak_status") @DefaultValue("ENABLED") String status
    ) {
        long speaksCount = topicService.countSpeaksInTopic(topicId, status);
        return Response.ok(speaksCount).build();
    }

    @Path("/speak")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getSpeaksInTopic(@Context HttpServletRequest request,
                                     @QueryParam("topic_id") @DefaultValue("0") long topicId,
                                     @QueryParam("status") @DefaultValue("ENABLED") String status,
                                     @QueryParam("index") @DefaultValue("0") int pageIndex,
                                     @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<NurseSpeakBean> speaks = topicService.getSpeaksInTopic(topicId, status, pageIndex, sizePerPage);
        return Response.ok(speaks).build();
    }

    @Path("/user/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countUsersInTopic(@Context HttpServletRequest request,
                                      @QueryParam("topic_id") @DefaultValue("0") long topicId,
                                      @QueryParam("user_authority") @DefaultValue("AGREE_ALL") String userAuthority
    ) {
        long nursesCount = topicService.countUsersInTopic(topicId, userAuthority);
        return Response.ok(nursesCount).build();
    }

    @Path("/user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
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
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editTopic(@Context HttpServletRequest request,
                              @FormDataParam("topic_id") @DefaultValue("0") long topicId,
                              @FormDataParam("user_id") @DefaultValue("0") long userId,
                              @FormDataParam("head_image_name") @DefaultValue("") String headImageName,
                              @FormDataParam("head_image") InputStream headImage,
                              @FormDataParam("background_image_name") @DefaultValue("") String backgroundImageName,
                              @FormDataParam("background_image") InputStream backgroundImage
    ) {
        NurseSpeakTopicBean topic = topicService.updateTopicPhoto(topicId, false, 0, headImageName, headImage, backgroundImageName, backgroundImage);
        return Response.ok(topic).build();
    }
}
