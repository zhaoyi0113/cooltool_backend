package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.beans.NurseSpeakComplaintBean;
import com.cooltoo.backend.services.NurseSpeakComplaintService;
import com.cooltoo.backend.services.NurseSpeakService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/5/16.
 */
@Path("/admin/user_speak")
public class UserSpeakManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(UserSpeakManageAPI.class.getName());

    @Autowired private NurseSpeakService userSpeakService;
    @Autowired private NurseSpeakComplaintService complaintService;

    // base_url/admin/user_speak/count/?speak_type=ALL&content=点&start_time=2016-05-11 11:11:11&end_time=2016-05-21 11:11:11
    @Path("/count/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countSpeakByContentLikeAndTime(@Context HttpServletRequest request,
                                                   @QueryParam("speak_type") @DefaultValue("ALL") String speakType,
                                                   @QueryParam("status") @DefaultValue("") String status,
                                                   @QueryParam("content")    @DefaultValue("") String content,
                                                   @QueryParam("start_time") @DefaultValue("") String startTime,
                                                   @QueryParam("end_time")   @DefaultValue("") String endTime
    ) {
        long count = userSpeakService.countByContentAndTime(speakType, status, 0, content, startTime, endTime);
        return Response.ok(count).build();
    }

    // base_url/admin/user_speak/count/?user_id=1&content=点&start_time=2016-05-11 11:11:11&end_time=2016-05-21 11:11:11
    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countSpeakByContentLikeAndTime(@Context HttpServletRequest request,
                                                   @QueryParam("user_id")    @DefaultValue("0") long userId,
                                                   @QueryParam("content")    @DefaultValue("") String content,
                                                   @QueryParam("start_time") @DefaultValue("") String startTime,
                                                   @QueryParam("end_time")   @DefaultValue("") String endTime
    ) {
        long count = userSpeakService.countByContentAndTime("", "", userId, content, startTime, endTime);
        return Response.ok(count).build();
    }

    //  base_url/admin/user_speak/?speak_type=ALL&content=点&start_time=2016-05-11 11:11:11&end_time=2016-05-21 11:11:11&page_index=0&size_per_page=5
    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getSpeakByContentLikeAndTime(@Context HttpServletRequest request,
                                                 @QueryParam("speak_type") @DefaultValue("ALL") String speakType,
                                                 @QueryParam("status") @DefaultValue("") String status,
                                                 @QueryParam("content")    @DefaultValue("") String content,
                                                 @QueryParam("start_time") @DefaultValue("") String startTime,
                                                 @QueryParam("end_time")   @DefaultValue("") String endTime,
                                                 @QueryParam("page_index") @DefaultValue("0")int pageIndex,
                                                 @QueryParam("size_per_page") @DefaultValue("20") int sizePerPage
    ) {
        List<NurseSpeakBean> speaks = userSpeakService.getSpeakByContentLikeAndTime(speakType, status, 0, content, startTime, endTime, pageIndex, sizePerPage);
        return Response.ok(speaks).build();
    }

    //  base_url/admin/user_speak/?user_id=1&content=点&start_time=2016-05-11 11:11:11&end_time=2016-05-21 11:11:11&page_index=0&size_per_page=5
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getSpeakByContentLikeAndTime(@Context HttpServletRequest request,
                                                 @QueryParam("user_id")    @DefaultValue("0") long userId,
                                                 @QueryParam("content")    @DefaultValue("") String content,
                                                 @QueryParam("start_time") @DefaultValue("") String startTime,
                                                 @QueryParam("end_time")   @DefaultValue("") String endTime,
                                                 @QueryParam("page_index") @DefaultValue("0")int pageIndex,
                                                 @QueryParam("size_per_page") @DefaultValue("20") int sizePerPage
    ) {
        List<NurseSpeakBean> speaks = userSpeakService.getSpeakByContentLikeAndTime("", "", userId, content, startTime, endTime, pageIndex, sizePerPage);
        return Response.ok(speaks).build();
    }

    @Path("/invisible")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateSpeakStatus(@Context HttpServletRequest request,
                                      @FormParam("speak_ids") @DefaultValue("") String speakIds,
                                      @FormParam("status") @DefaultValue("disabled") String status
    ) {
        long effectedCount = userSpeakService.updateSpeakStatus(speakIds, status);
        return Response.ok(effectedCount).build();
    }

    @Path("/comment/invisible")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateSpeakCommentStatus(@Context HttpServletRequest request,
                                             @FormParam("comment_id") @DefaultValue("0") long commentId,
                                             @FormParam("status") @DefaultValue("disabled") String status
    ) {
        NurseSpeakCommentBean comment = userSpeakService.updateSpeakComment(commentId, "", status);
        return Response.ok(comment).build();
    }

    //==========================================================
    //            用户举报接口
    //==========================================================
    @Path("/complaint_by_status/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countComplaint(@Context HttpServletRequest request,
                                   @PathParam("status") @DefaultValue("ALL") String status
    ) {
        long count = complaintService.countByStatus(status);
        return Response.ok(count).build();
    }

    @Path("/complaint_by_speak_id/count/{speak_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countComplaint(@Context HttpServletRequest request,
                                   @PathParam("speak_id") @DefaultValue("0") long speakId
    ) {
        long count = complaintService.countBySpeakId(speakId);
        return Response.ok(count).build();
    }

    @Path("/complaint_by_status")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getComplaintByStatus(@Context HttpServletRequest request,
                                         @QueryParam("status") @DefaultValue("ALL") String status,
                                         @QueryParam("index") @DefaultValue("0") int pageIndex,
                                         @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<NurseSpeakComplaintBean> complaints = complaintService.getComplaintByStatus(status, pageIndex, sizePerPage);
        return Response.ok(complaints).build();
    }

    @Path("/complaint_by_speak_id")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getComplaintBySpeakId(@Context HttpServletRequest request,
                                         @QueryParam("speak_id") @DefaultValue("0") long speakId,
                                         @QueryParam("index") @DefaultValue("0") int pageIndex,
                                         @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<NurseSpeakComplaintBean> complaints = complaintService.getComplaintBySpeakId(speakId, pageIndex, sizePerPage);
        return Response.ok(complaints).build();
    }

    @Path("/complaint/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editComplaint(@Context HttpServletRequest request,
                                  @FormParam("complaint_id") @DefaultValue("0") long complaintId,
                                  @FormParam("status") @DefaultValue("") String status,
                                  @FormParam("reason") @DefaultValue("") String reason
    ) {
        NurseSpeakComplaintBean complaint = complaintService.updateCompliant(complaintId, reason, status);
        return Response.ok(complaint).build();
    }
}
