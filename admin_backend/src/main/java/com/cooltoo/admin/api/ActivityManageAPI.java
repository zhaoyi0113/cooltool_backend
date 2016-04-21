package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.beans.ActivityBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.services.ActivityService;
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
import java.util.Map;

/**
 * Created by hp on 2016/4/21.
 */
@Path("/admin/activity")
public class ActivityManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(ActivityManageAPI.class.getName());

    @Autowired
    private ActivityService activityService;

    // status ==> all/enable/disable/editing
    @Path("/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countActivity(@Context HttpServletRequest request,
                                  @PathParam("status") String status) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get activity count by status={}", userId, status);
        long count = activityService.countActivityByStatus(status);
        logger.info("count = {}", count);
        return Response.ok(count).build();
    }


    // status ==> all/enable/disable/editing
    @Path("/{status}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getActivityByStatus(@Context HttpServletRequest request,
                                        @PathParam("status") String status,
                                        @PathParam("index")  @DefaultValue("0") int index,
                                        @PathParam("number") @DefaultValue("10") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get activities by status={} at page={}, {}/page", userId, status, index, number);
        List<ActivityBean> activities = activityService.getActivityByStatus(status, index, number);
        logger.info("count = {}", activities.size());
        return Response.ok(activities).build();
    }

    @Path("/get_by_ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getActivityByIds(@Context HttpServletRequest request,
                                     @QueryParam("ids") String ids
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get activities by ids={}", userId, ids);
        List<ActivityBean> activities = activityService.getActivityByIds(ids);
        logger.info("count = {}", activities.size());
        return Response.ok(activities).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteActivityByIds(@Context HttpServletRequest request,
                                        @FormParam("ids") String ids
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} delete activity by ids={}", ids);
        String deleteIds = activityService.deleteByIds(ids);
        logger.info("ids={}", ids);
        return Response.ok(deleteIds).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response createActivity(@Context HttpServletRequest request,
                                   @FormParam("title") String title,
                                   @FormParam("subtitle") String subtitle,
                                   @FormParam("description") String description,
                                   @FormParam("time") String time,
                                   @FormParam("place") String place,
                                   @FormParam("price") double price

    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} create activity", userId);
        ActivityBean activity = activityService.createActivity(title, subtitle, description, time, place, ""+price);
        logger.info("activity is {}", activity);
        return Response.ok(activity).build();
    }

    @Path("/edit/base_information")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateActivityBasicInfo(@Context HttpServletRequest request,
                                            @FormParam("activity_id") long activityId,
                                            @FormParam("title") String title,
                                            @FormParam("subtitle") String subtitle,
                                            @FormParam("description") String description,
                                            @FormParam("time") String time,
                                            @FormParam("place") String place,
                                            @FormParam("price") double price

    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update activity basic information", userId);
        ActivityBean activity = activityService.updateActivityBasicInfo(activityId, title, subtitle, description, time, place, ""+price, null, null);
        logger.info("activity is {}", activity);
        return Response.ok(activity).build();
    }

    @Path("/edit/front_cover")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateActivityFrontCover(@Context HttpServletRequest request,
                                             @FormDataParam("activity_id") long activityId,
                                             @FormDataParam("file_name") String imageName,
                                             @FormDataParam("file") InputStream image,
                                             @FormDataParam("file") FormDataContentDisposition disp

    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update activity front cover", userId);
        ActivityBean frontCover = activityService.updateActivityBasicInfo(activityId, null, null, null, null, null, null, imageName, image);
        logger.info("activity is {}", frontCover);
        return Response.ok(frontCover).build();
    }

    @Path("/edit/status")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateActivityStatus(@Context HttpServletRequest request,
                                         @FormParam("activity_id") long activityId,
                                         @FormParam("status") String status

    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update activity front cover", userId);
        ActivityBean bean = activityService.updateActivityStatus(activityId, status);
        logger.info("activity is {}", bean);
        return Response.ok(bean).build();
    }

    //=============================================================
    //         edit the activity content
    //=============================================================

    // 获取活动详情
    // param={"activity_id":"1","nginx_url":"http://nginx_server_ip:port/storage_or_temporary_path/"}
    @Path("/get_detail")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getDetailById(@Context HttpServletRequest request,
                                  @QueryParam("param") String idAndNginxUrl
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get detail activity by idAndNginxUrl={}", userId, idAndNginxUrl);

        // parse Json
        Map<String, String> idAndUrl = VerifyUtil.parseJsonKeyVal(idAndNginxUrl);
        if (idAndUrl.isEmpty()) {
            logger.info("json parse is empty", userId, idAndNginxUrl);
            return Response.ok().build();
        }
        String strId    = idAndUrl.get("activity_id");
        long   lId      = Long.parseLong(strId);
        String nginxUrl = idAndUrl.get("nginx_url");

        ActivityBean activity = activityService.getActivityById(lId, nginxUrl);
        return Response.ok(activity).build();
    }


    // 将活动置为 editing, 并且将活动对应的图片迁至临时文件夹
    @Path("/edit/content/cache")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response cacheActivity2Temporary(@Context HttpServletRequest request,
                                            @FormParam("activity_id") long activityId

    ) {
        long   userId    = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        String userToken = (String)request.getAttribute(ContextKeys.ADMIN_USER_TOKEN);

        logger.info("user {} with token {} cache activity to temporary path", userId, userToken);
        ActivityBean bean = activityService.moveActivity2Temporary(userToken, activityId);
        logger.info("activity is {}", bean);
        return Response.ok(bean).build();
    }


    // 活动为 editing 时, 向活动中添加图片，图片缓存在临时文件夹
    @Path("/edit/content/add_image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response addImage2Temporary(@Context HttpServletRequest request,
                                       @FormDataParam("activity_id") long activityId,
                                       @FormDataParam("file_name") String imageName,
                                       @FormDataParam("file") InputStream image,
                                       @FormDataParam("file") FormDataContentDisposition disp

    ) {
        long   userId    = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        String userToken = (String)request.getAttribute(ContextKeys.ADMIN_USER_TOKEN);

        logger.info("user {} with token {} cache image to temporary path", userId, userToken);
        String relativePath = activityService.createTemporaryFile(userToken, activityId, imageName, image);
        logger.info("relative path is {}", relativePath);
        return Response.ok(relativePath).build();
    }


    // 提交活动，将获取置为 disable, 将缓存在临时文件夹的图片，导入 storage 文件夹，
    // 并替换 content 中 <img/> src 的引用值。
    @Path("/edit/content/submit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateActivityContent(@Context HttpServletRequest request,
                                            @FormParam("activity_id") long activityId,
                                            @FormParam("content") String content

    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        String userToken = (String)request.getAttribute(ContextKeys.ADMIN_USER_TOKEN);

        logger.info("user {} submit activity content", userId);
        ActivityBean activity = activityService.updateActivityContent(userToken, activityId, content);

        logger.info("activity is {}", activity);
        return Response.ok(activity).build();
    }
}
