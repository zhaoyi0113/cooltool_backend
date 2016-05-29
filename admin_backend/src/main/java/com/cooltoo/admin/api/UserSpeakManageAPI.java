package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.NurseSpeakBean;
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

    @Autowired
    private NurseSpeakService userSpeakService;

    // base_url/admin/user_speak/count/?speak_type=ALL&content=点&start_time=2016-05-11 11:11:11&end_time=2016-05-21 11:11:11
    @Path("/count/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countSpeakByContentLikeAndTime(@Context HttpServletRequest request,
                                                   @QueryParam("speak_type") @DefaultValue("ALL") String speakType,
                                                   @QueryParam("content")    @DefaultValue("") String content,
                                                   @QueryParam("start_time") @DefaultValue("") String startTime,
                                                   @QueryParam("end_time")   @DefaultValue("") String endTime
    ) {
        long count = userSpeakService.countByContentAndTime(speakType, 0, content, startTime, endTime);
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
        long count = userSpeakService.countByContentAndTime("", userId, content, startTime, endTime);
        return Response.ok(count).build();
    }

    //  base_url/admin/user_speak/?speak_type=ALL&content=点&start_time=2016-05-11 11:11:11&end_time=2016-05-21 11:11:11&page_index=0&size_per_page=5
    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getSpeakByContentLikeAndTime(@Context HttpServletRequest request,
                                                 @QueryParam("speak_type") @DefaultValue("ALL") String speakType,
                                                 @QueryParam("content")    @DefaultValue("") String content,
                                                 @QueryParam("start_time") @DefaultValue("") String startTime,
                                                 @QueryParam("end_time")   @DefaultValue("") String endTime,
                                                 @QueryParam("page_index") @DefaultValue("0")int pageIndex,
                                                 @QueryParam("size_per_page") @DefaultValue("20") int sizePerPage
    ) {
        List<NurseSpeakBean> speaks = userSpeakService.getSpeakByContentLikeAndTime(speakType, 0, content, startTime, endTime, pageIndex, sizePerPage);
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
        List<NurseSpeakBean> speaks = userSpeakService.getSpeakByContentLikeAndTime("", userId, content, startTime, endTime, pageIndex, sizePerPage);
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

    //==========================================================
    //            用户举报接口
    //==========================================================
    @Path("/complaint/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countComplaint(@Context HttpServletRequest request,
                                    @QueryParam("status") @DefaultValue("ALL") String status
    ) {
        // TODO -- need fixed
        return Response.ok(0).build();
    }

    @Path("/complaint")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getComplaintByStatus(@Context HttpServletRequest request,
                                          @QueryParam("status") @DefaultValue("ALL") String status
    ) {
        // TODO -- need fixed
        return Response.ok().build();
    }
}
