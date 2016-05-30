package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseSpeakComplaintService;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.SpeakType;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzzhao on 3/15/16.
 */
@Path("/nurse/speak")
public class NurseSpeakAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakAPI.class);

    @Autowired private NurseSpeakService speakService;
    @Autowired private NurseSpeakComplaintService complaintService;

    @Path("/query/all/{type}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpeakByAnonymous(@Context HttpServletRequest request,
                                        @PathParam("type") String type,
                                        @PathParam("index")  @DefaultValue("0")  int index,
                                        @PathParam("number") @DefaultValue("10") int number
    ) {
        logger.info("anonymous user to get all speak content type={} index={} number={}", type, index, number);
        if ("ALL".equalsIgnoreCase(type)) {
            type = SpeakType.allValues();
            logger.info("speak type assign to {}", type);
        }
        List<NurseSpeakBean> all = speakService.getSpeak(false, -1, type, index, number);
        logger.info("all speak count={}", all.size());
        return Response.ok(all).build();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSpeakById(@Context HttpServletRequest request,
                                 @PathParam("id") long id) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseSpeakBean> nurseSpeak = speakService.getNurseSpeak(userId, id);
        return Response.ok(nurseSpeak).build();
    }

    @Path("/query/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSpeak(@Context HttpServletRequest request,
                             @PathParam("index") int index,
                             @PathParam("number") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        String type = "smug,ask_question";
        logger.info("user {} get speak by type={} at page={} {}/page", userId, type, index, number);

        List<NurseSpeakBean> speak = speakService.getSpeak(true, userId, type, index, number);
        logger.info("speak count={}", speak.size());

        return Response.ok(speak).build();
    }

    @Path("/query/{user_id}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSpeak(@Context HttpServletRequest request,
                             @PathParam("user_id") @DefaultValue("0") long userId,
                             @PathParam("index") @DefaultValue("0") int index,
                             @PathParam("number") @DefaultValue("10") int number
    ) {
        String type = "smug,ask_question";
        logger.info("user {} get speak by type={} at page={} {}/page", userId, type, index, number);

        List<NurseSpeakBean> speak = speakService.getSpeak(true, userId, type, index, number);
        logger.info("speak count={}", speak.size());

        return Response.ok(speak).build();
    }


    @Path("/smug")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addSmugSpeak(@Context HttpServletRequest request,
                                 @FormDataParam("content") String content,
                                 @FormDataParam("file_name") String fileName,
                                 @FormDataParam("file") InputStream file) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakBean nurseSpeak = speakService.addSmug(userId, content, fileName, file);
        return Response.ok(nurseSpeak).build();
    }

    @Path("/cathart")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addCathartSpeak(@Context HttpServletRequest request,
                                    @FormDataParam("anonymous_name") String anonymousName,
                                    @FormDataParam("content") String content,
                                    @FormDataParam("file_name") String fileName,
                                    @FormDataParam("file") InputStream file) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakBean nurseSpeak = speakService.addCathart(userId, content, anonymousName, fileName, file);
        return Response.ok(nurseSpeak).build();
    }

    @Path("/ask_question")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addAskQuestion(@Context HttpServletRequest request,
                                   @FormParam("content") String content) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakBean nurseSpeak = speakService.addAskQuestion(userId, content, null, null);
        return Response.ok(nurseSpeak).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response deleteSpeakByIds(@Context HttpServletRequest request,
                                     @FormParam("id") String speakIds
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseSpeakBean> speaks = speakService.deleteByIds(userId, speakIds);
        return Response.ok(speaks).build();
    }

    @Path("/image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addImageInSpeak(@Context HttpServletRequest request,
                                    @FormDataParam("speak_id") long speakId,
                                    @FormDataParam("file_name") String fileName,
                                    @FormDataParam("file") InputStream file
    ) {
        long userId =  (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        ImagesInSpeakBean imageAdded = speakService.addImage(userId, speakId, fileName, file);
        return Response.ok(imageAdded).build();
    }

    @Path("/image")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response deleteImageInSpeak(@Context HttpServletRequest request,
                                       @FormDataParam("image_in_speak_id") long imageInSpeakId
    ) {
        long userId =  (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        ImagesInSpeakBean imageAdded = speakService.deleteImagesInSpeak(imageInSpeakId);
        return Response.ok(imageAdded).build();
    }

    @Path("/complaint")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = false)
    public Response complaintSpeak(@Context HttpServletRequest request,
                                   @FormParam("speak_id") @DefaultValue("0") long complaintSpeakId,
                                   @FormParam("reason") @DefaultValue("") String complaintReason
    ) {
        Object objUserId =  request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        Long userId = -1L;
        if (null!=objUserId) {
            userId = (Long) objUserId;
        }
        NurseSpeakComplaintBean complaintBean = complaintService.addComplaint(userId, complaintSpeakId, complaintReason);
        return Response.ok(complaintBean).build();
    }

    @Path("/comment")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addSpeakComment(@Context HttpServletRequest request,
                                    @FormParam("nurseSpeakId") long nurseSpeakId,
                                    @FormParam("commentReceiverId") long commentReceiverId,
                                    @FormParam("comment") String comment
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakCommentBean commentBean = speakService.addSpeakComment(nurseSpeakId, userId, commentReceiverId, comment);
        return Response.ok(commentBean).build();
    }

    @Path("/comment")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response deleteSpeakComment(@Context HttpServletRequest request,
                                       @FormParam("id") String commentIds
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user {} delete speak comment by comment ids {}", userId, commentIds);

        List<NurseSpeakCommentBean> comments = speakService.deleteSpeakComment(userId, commentIds);
        if (null==comments || comments.isEmpty()) {
            return Response.ok().build();
        }
        if (comments.size()==1) {
            return Response.ok(comments.get(0)).build();
        }
        return Response.ok(comments).build();
    }

    @Path("/thumbs_up")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response setSpeakThumbsUp(@Context HttpServletRequest request,
                                     @FormParam("nurseSpeakId") long nurseSpeakId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakThumbsUpBean thumbsUpBean = speakService.setNurseSpeakThumbsUp(nurseSpeakId, userId);
        return Response.ok(thumbsUpBean).build();
    }

    @Path("/get_thumbs_up_user/{speak_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSpeakThumbsUp(@Context HttpServletRequest request,
                                     @PathParam("speak_id") long speak_id
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseFriendsBean> thumbsFriends = speakService.getThumbsUpUsers(userId, speak_id);
        return Response.ok(thumbsFriends).build();
    }
}
