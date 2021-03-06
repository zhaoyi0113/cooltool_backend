package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseSpeakComplaintService;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.backend.services.VideoInSpeakService;
import com.cooltoo.constants.CCVideoStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.constants.VideoPlatform;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.*;

/**
 * Created by yzzhao on 3/15/16.
 */
@Path("/nurse/speak")
public class NurseSpeakAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakAPI.class);

    @Autowired private NurseSpeakService speakService;
    @Autowired private NurseSpeakComplaintService complaintService;
    @Autowired private VideoInSpeakService videoInSpeakService;

    @Path("/query/all/{type}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = false)
    public Response getSpeakByAnonymous(@Context HttpServletRequest request,
                                        @PathParam("type") String type,
                                        @PathParam("index")  @DefaultValue("0")  int index,
                                        @PathParam("number") @DefaultValue("10") int number
    ) {
        if ("ALL".equalsIgnoreCase(type)) {
            type = SpeakType.allValues();
            logger.info("speak type assign to {}", type);
        }

        long userId = 0;
        Object objUserId = request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        if (null!=objUserId) {
            userId = (Long) objUserId;
        }
        logger.info("anonymous user to get all speak content useUserId={} userId={} type={} index={} number={}", userId, type, index, number);
        List<NurseSpeakBean> all = speakService.getSpeak(false, userId, type, index, number);
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
        String type = "smug,ask_question,SHORT_VIDEO";
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

    @Path("/short_video/callback")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response shortVideoCallback(@Context HttpServletRequest request,
                                       @QueryParam("videoid") @DefaultValue("") String videoCodeId,
                                       @QueryParam("status") @DefaultValue("") String status,
                                       @QueryParam("duration") @DefaultValue("0") long durationSecond,
                                       @QueryParam("image") @DefaultValue("") String frontCoverUrl
    ) {
        logger.info("short video call back videoCodeId={} status={} durationSecond={} frontCoverUrl={}",
                videoCodeId, status, durationSecond, frontCoverUrl);
        List<VideoInSpeakBean> videosUpdated = videoInSpeakService.updateVideoStatus(videoCodeId, VideoPlatform.CC, status);
        if (!VerifyUtil.isListEmpty(videosUpdated)) {
            return Response.ok(VerifyUtil.CC_VIDEO_CALLBACK_MESSAGE).build();
        }
        else {
            return Response.ok().build();
        }
    }

    @Path("/short_video/authority/7niu/token")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response shortVideoQiNiuAuthorityToken(@Context HttpServletRequest request,
                                                  @QueryParam("key") @DefaultValue("") String key
    ) {
        String bucketName = videoInSpeakService.getBucketName();
        String token = videoInSpeakService.getQiNiuAuthorityToken(key, bucketName);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("bucketName", bucketName);
        return Response.ok(map).build();
    }

    @Path("/short_video/7niu_callback")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response shortVideoQiNiuCallback(@Context HttpServletRequest request) {
        //接收七牛回调过来的内容
        String contentType = request.getHeader("content-type");
        String authority = request.getHeader("authorization");
        String fileName = request.getParameter("filename");
        String hash = request.getParameter("hash");
        String key = request.getParameter("key");
        String callbackBody = "filename="+fileName+"&key="+key+"&hash="+hash;
        boolean isValid = videoInSpeakService.isValidQiNiuCallback(authority, contentType, callbackBody);
        if (isValid) {
            videoInSpeakService.addVideo(0, VideoPlatform.QiNiu.name(), key, null, null, null, null);
            videoInSpeakService.updateVideoStatus(key, VideoPlatform.QiNiu, CCVideoStatus.OK.name());
        }
        //设置返回给七牛的json格式的数据
        Map<String, String> retOK = new HashMap<>();
        retOK.put("response", isValid ? "success" : "error");
        return Response.ok(retOK).build();
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

    @Path("/short_video")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addShortVideo(@Context HttpServletRequest request,
                                  @FormDataParam("platform") @DefaultValue("") String platform,
                                  @FormDataParam("content") @DefaultValue("") String content,
                                  @FormDataParam("video_code") @DefaultValue("") String videoCode,
                                  @FormDataParam("background_image_name") @DefaultValue("") String backgroundImageName,
                                  @FormDataParam("background_image") InputStream backgroundImage,
                                  @FormDataParam("snapshot_image_name") @DefaultValue("") String snapshotImageName,
                                  @FormDataParam("snapshot_image") InputStream snapshotImage
    ) {
        logger.info("add short video content={} video_code={} platform={} background_image={} snapshot={}",
                content, videoCode, platform, null!=backgroundImage, null!=snapshotImage);
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseSpeakBean nurseSpeak = speakService.addShortVideo(userId, content);
        if (null!=nurseSpeak) {
            VideoInSpeakBean video = null;
            if (VideoPlatform.QiNiu.name().equalsIgnoreCase(platform)) {
                video = videoInSpeakService.updateVideo(platform, videoCode,
                        backgroundImageName, backgroundImage,
                        snapshotImageName, snapshotImage,
                        nurseSpeak.getId(), CCVideoStatus.OK.name());
            }
            if (null==video) {
                video = videoInSpeakService.addVideo(nurseSpeak.getId(), platform, videoCode,
                        backgroundImageName, backgroundImage,
                        snapshotImageName, snapshotImage);
            }

            List<VideoInSpeakBean> videosInSpeak = new ArrayList<>();
            videosInSpeak.add(video);
            nurseSpeak.setVideos(videosInSpeak);
        }
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
