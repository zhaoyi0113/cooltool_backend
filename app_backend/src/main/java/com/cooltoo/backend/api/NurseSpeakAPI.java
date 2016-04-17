package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.ImagesInSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.SpeakType;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/query/all/{type}/{index}/{number}")
    public Response getSpeakByAnonymous(@Context HttpServletRequest request,
                                        @PathParam("type") String type,
                                        @PathParam("index")  @DefaultValue("0")  int index,
                                        @PathParam("number") @DefaultValue("10") int number
    ) {
        logger.info("anonymous user to get all speak content type={} index={} number={}", type, index, number);
        List<NurseSpeakBean> all = speakService.getSpeakByType(-1, type, index, number);
        logger.info("anonymous user to get all speak content, size ", all.size());
        return Response.ok(all).build();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSpeakById(@Context HttpServletRequest request,
                                 @PathParam("id") long id) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakBean nurseSpeak = speakService.getNurseSpeak(userId, id);
        return Response.ok(nurseSpeak).build();
    }

    @Path("/query")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSpeakByUserIdAndType(@Context HttpServletRequest request,
                                            @FormParam("user_id") @DefaultValue("0") long userId,
                                            @FormParam("type")   @DefaultValue("ALL") String type,
                                            @PathParam("index")  @DefaultValue("0")  int index,
                                            @PathParam("number") @DefaultValue("10") int number
    ) {
        if (userId<=0) {
            userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        }
        logger.info("get nurse speak for user={} type={} at index({}) number({}).", userId, type, index, number);
        List<NurseSpeakBean> nurseSpeak = null;
        if (!"ALL".equalsIgnoreCase(type)) {
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
    @Path("/query/{index}/{number}")
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSpeakAllType(@Context HttpServletRequest request,
                                    @PathParam("index")  @DefaultValue("0")  int index,
                                    @PathParam("number") @DefaultValue("10") int number
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
    public Response getSpeakSmugType(@Context HttpServletRequest request,
                                     @PathParam("index")  @DefaultValue("0")  int index,
                                     @PathParam("number") @DefaultValue("10") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user {} to get all smugs", userId);
        List<NurseSpeakBean> smugs = speakService.getSpeakByType(userId, SpeakType.SMUG.name(), index, number);
        logger.info("user {} to get all smugs, size : {}", userId, smugs.size());
        return Response.ok(smugs).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/query/cathart/{index}/{number}")
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSpeakCathartType(@Context HttpServletRequest request,
                                        @PathParam("index")  @DefaultValue("0")  int index,
                                        @PathParam("number") @DefaultValue("10") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user {} to get all cathart", userId);
        List<NurseSpeakBean> smugs = speakService.getSpeakByType(userId, SpeakType.CATHART.name(), index, number);
        logger.info("user {} to get all cathart, size : {}", userId, smugs.size());
        return Response.ok(smugs).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/query/ask_question/{index}/{number}")
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSpeakAskQuestionType(@Context HttpServletRequest request,
                                            @PathParam("index")  @DefaultValue("0")  int index,
                                            @PathParam("number") @DefaultValue("10") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user {} to get all ask_questions", userId);
        List<NurseSpeakBean> smugs = speakService.getSpeakByType(userId, SpeakType.ASK_QUESTION.name(), index, number);
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
    public Response addAskQuestion(@Context HttpServletRequest request,
                                   @FormDataParam("content") String content,
                                   @FormDataParam("file_name") String fileName,
                                   @FormDataParam("file") InputStream fileInputStream) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakBean nurseSpeak = speakService.addAskQuestion(userId, content, fileName, fileInputStream);
        return Response.ok(nurseSpeak).build();
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/comment")
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

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/comment")
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/thumbs_up")
    @LoginAuthentication(requireNurseLogin = true)
    public Response setSpeakThumbsUp(@Context HttpServletRequest request,
                                     @FormParam("nurseSpeakId") long nurseSpeakId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakThumbsUpBean thumbsUpBean = speakService.setNurseSpeakThumbsUp(nurseSpeakId, userId);
        return Response.ok(thumbsUpBean).build();
    }
}
