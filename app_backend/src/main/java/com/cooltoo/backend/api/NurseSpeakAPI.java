package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
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
 * Created by yzzhao on 3/15/16.
 */
@Path("/nurse/speak")
@LoginAuthentication(requireNurseLogin = true)
public class NurseSpeakAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakAPI.class);

    @Autowired
    private NurseSpeakService speakService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/query/{index}/{number}")
    public Response getNurseSpeakContentsList(@Context HttpServletRequest request,
                                              @PathParam("index") int index,
                                              @PathParam("number") int number) {
        long userId = Long.parseLong(request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID).toString());
        logger.info("get nurse speak for user " + userId+" at index="+index+", number="+number);
        List<NurseSpeakBean> nurseSpeak = speakService.getNurseSpeak(userId, index, number);
        logger.info("get nurse speak list size "+nurseSpeak.size());
        return Response.ok(nurseSpeak).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addSpeak(@Context HttpServletRequest request,
                             @FormDataParam("speak_type") String speakType,
                             @FormDataParam("content") String content,
                             @FormDataParam("file_name") String fileName,
                             @FormDataParam("file") InputStream fileInputStream) {
        long userId = Long.parseLong(request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID).toString());
        NurseSpeakBean nurseSpeak = speakService.addNurseSpeak(userId, content, speakType, fileName, fileInputStream);
        return Response.ok(nurseSpeak).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getNurseSpeakContent(@Context HttpServletRequest request,
                                         @PathParam("id") long id) {
        long userId = Long.parseLong(request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID).toString());
        NurseSpeakBean nurseSpeak = speakService.getNurseSpeak(id);
        return Response.ok(nurseSpeak).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/comment")
    public Response addSpeakComment(
            @Context HttpServletRequest request,
            @FormParam("nurseSpeakId") long nurseSpeakId,
            @FormParam("commentReceiverId") long commentReceiverId,
            @FormParam("comment") String comment
    ) {
        long userId = Long.parseLong(request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID).toString());
        NurseSpeakCommentBean commentBean = speakService.addSpeakComment(nurseSpeakId, userId, commentReceiverId, comment);
        return Response.ok(commentBean).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/thumbs_up")
    public Response addSpeakThumbsUp(
            @Context HttpServletRequest request,
            @FormParam("nurseSpeakId") long nurseSpeakId
    ) {
        long userId = Long.parseLong(request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID).toString());
        NurseSpeakThumbsUpBean thumbsUpBean = speakService.addNurseSpeakThumbsUp(nurseSpeakId, userId);
        return Response.ok(thumbsUpBean).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/thumbs_up")
    public Response deleteSpeakThumbsUp(
            @Context HttpServletRequest request,
            @FormParam("nurseSpeakId") long nurseSpeakId
    ) {
        long userId = Long.parseLong(request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID).toString());
        NurseSpeakThumbsUpBean thumbsUpbean = speakService.getNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(nurseSpeakId, userId);
        speakService.deleteNurseSpeakThumbsUp(nurseSpeakId, userId);
        return Response.ok(thumbsUpbean).build();
    }
}
