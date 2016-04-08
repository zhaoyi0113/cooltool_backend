package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.SpeakType;
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
public class NurseSpeakAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakAPI.class);

    @Autowired
    private NurseSpeakService speakService;

    @Path("/query")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSpeakContentsList(@Context HttpServletRequest request,
                                         @FormParam("user_id") @DefaultValue("0")   long   userId,
                                         @FormParam("type")   @DefaultValue("ALL") String type,
                                         @FormParam("index")  @DefaultValue("0")   int    index,
                                         @FormParam("number") @DefaultValue("5")   int    number
    ) {
        if (userId<=0) {
            userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        }
        logger.info("get nurse speak for user={} type={} at index({}) number({}).", userId, type, index, number);
        List<NurseSpeakBean> nurseSpeak = null;
        if ("ALL".equalsIgnoreCase(type)) {
            nurseSpeak = speakService.getSpeakByUserIdAndType(userId, type, index, number);
        }
        else {
            nurseSpeak = speakService.getNurseSpeak(userId, index, number);
        }
        logger.info("get nurse speak list size "+nurseSpeak.size());
        return Response.ok(nurseSpeak).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/query/all/{type}/{index}/{number}")
    public Response getAllSpeak(@Context HttpServletRequest request,
                                @PathParam("type") String type,
                                @PathParam("index") int index,
                                @PathParam("number") int number
    ) {
        logger.info("anonymous user to get all speak content type={} index={} number={}", type, index, number);
        List<NurseSpeakBean> all = speakService.getSpeakByType(type, index, number);
        logger.info("anonymous user to get all speak content, size ", all.size());
        return Response.ok(all).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/query/{index}/{number}")
    @LoginAuthentication(requireNurseLogin = true)
    public Response getNurseSpeakContentsList(@Context HttpServletRequest request,
                                              @PathParam("index") int index,
                                              @PathParam("number") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("get nurse speak for user " + userId+" at index="+index+", number="+number);
        List<NurseSpeakBean> nurseSpeak = speakService.getNurseSpeak(userId, index, number);
        logger.info("get nurse speak list size "+nurseSpeak.size());
        return Response.ok(nurseSpeak).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/query/smug/{index}/{number}")
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSmugContent(@Context HttpServletRequest request,
                                   @PathParam("index") int index,
                                   @PathParam("number") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user {} to get all smugs", userId);
        List<NurseSpeakBean> smugs = speakService.getSpeakByType(SpeakType.SMUG.name(), index, number);
        logger.info("user {} to get all smugs, size : {}", userId, smugs.size());
        return Response.ok(smugs).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/query/cathart/{index}/{number}")
    @LoginAuthentication(requireNurseLogin = true)
    public Response getCathartContent(@Context HttpServletRequest request,
                                   @PathParam("index") int index,
                                   @PathParam("number") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user {} to get all cathart", userId);
        List<NurseSpeakBean> smugs = speakService.getSpeakByType(SpeakType.CATHART.name(), index, number);
        logger.info("user {} to get all cathart, size : {}", userId, smugs.size());
        return Response.ok(smugs).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/query/ask_question/{index}/{number}")
    @LoginAuthentication(requireNurseLogin = true)
    public Response getAskQuestionContent(@Context HttpServletRequest request,
                                   @PathParam("index") int index,
                                   @PathParam("number") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user {} to get all ask_questions", userId);
        List<NurseSpeakBean> smugs = speakService.getSpeakByType(SpeakType.ASK_QUESTION.name(), index, number);
        logger.info("user {} to get all ask_questions, size : {}", userId, smugs.size());
        return Response.ok(smugs).build();
    }


    @Path("/smug")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addSmugSpeak(@Context HttpServletRequest request,
                             @FormDataParam("content") String content,
                             @FormDataParam("file_name") String fileName,
                             @FormDataParam("file") InputStream fileInputStream) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakBean nurseSpeak = speakService.addSmug(userId, content, fileName, fileInputStream);
        return Response.ok(nurseSpeak).build();
    }

    @Path("/cathart")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addCathartSpeak(@Context HttpServletRequest request,
                             @FormDataParam("content") String content,
                             @FormDataParam("file_name") String fileName,
                             @FormDataParam("file") InputStream fileInputStream) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakBean nurseSpeak = speakService.addCathart(userId, content, fileName, fileInputStream);
        return Response.ok(nurseSpeak).build();
    }

    @Path("/ask_question")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addSpeak(@Context HttpServletRequest request,
                             @FormDataParam("content") String content,
                             @FormDataParam("file_name") String fileName,
                             @FormDataParam("file") InputStream fileInputStream) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakBean nurseSpeak = speakService.addAskQuestion(userId, content, fileName, fileInputStream);
        return Response.ok(nurseSpeak).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @LoginAuthentication(requireNurseLogin = true)
    public Response getNurseSpeakContent(@Context HttpServletRequest request,
                                         @PathParam("id") long id) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakBean nurseSpeak = speakService.getNurseSpeak(id);
        return Response.ok(nurseSpeak).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/comment")
    @LoginAuthentication(requireNurseLogin = true)
    public Response addSpeakComment(
            @Context HttpServletRequest request,
            @FormParam("nurseSpeakId") long nurseSpeakId,
            @FormParam("commentReceiverId") long commentReceiverId,
            @FormParam("comment") String comment
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakCommentBean commentBean = speakService.addSpeakComment(nurseSpeakId, userId, commentReceiverId, comment);
        return Response.ok(commentBean).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/thumbs_up")
    @LoginAuthentication(requireNurseLogin = true)
    public Response addSpeakThumbsUp(
            @Context HttpServletRequest request,
            @FormParam("nurseSpeakId") long nurseSpeakId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakThumbsUpBean thumbsUpBean = speakService.addNurseSpeakThumbsUp(nurseSpeakId, userId);
        return Response.ok(thumbsUpBean).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/thumbs_up")
    @LoginAuthentication(requireNurseLogin = true)
    public Response deleteSpeakThumbsUp(
            @Context HttpServletRequest request,
            @FormParam("nurseSpeakId") long nurseSpeakId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakThumbsUpBean thumbsUpbean = speakService.getNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(nurseSpeakId, userId);
        speakService.deleteNurseSpeakThumbsUp(nurseSpeakId, userId);
        return Response.ok(thumbsUpbean).build();
    }
}
