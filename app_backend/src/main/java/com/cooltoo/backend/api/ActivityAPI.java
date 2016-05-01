package com.cooltoo.backend.api;

import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.beans.ActivityBean;
import com.cooltoo.constants.ActivityStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.services.ActivityService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/4/21.
 */
@Path("/activity")
public class ActivityAPI {

    private static final Logger logger = LoggerFactory.getLogger(ActivityAPI.class.getName());

    @Autowired
    private ActivityService activityService;

    @Path("/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActivityByStatus(@Context HttpServletRequest request,
                                        @PathParam("index")  @DefaultValue("0") int index,
                                        @PathParam("number") @DefaultValue("10") int number
    ) {
        String status = ActivityStatus.ENABLE.name();
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user {} get activities by status={} at page={}, {}/page", userId, status, index, number);
        List<ActivityBean> activities = activityService.getActivityByStatus(status, index, number);
        logger.info("count = {}", activities.size());
        return Response.ok(activities).build();
    }

    // 获取活动详情
    // param={"activity_id":"1","nginx_url":"http://nginx_server_ip:port/storage_or_temporary_path/"}
    @Path("/get_detail")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActivityDetailById(@Context HttpServletRequest request,
                                          @QueryParam("param") String idAndNginxUrl
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user {} get detail activity by idAndNginxUrl={}", userId, idAndNginxUrl);

        // parse Json
        Map<String, String> idAndUrl = VerifyUtil.parseJsonKeyVal(idAndNginxUrl);
        if (idAndUrl.isEmpty()) {
            logger.info("json parse is empty", userId, idAndNginxUrl);
            return Response.ok().build();
        }
        String strActivityId    = idAndUrl.get("activity_id");
        long   lActivityId      = Long.parseLong(strActivityId);
        String nginxUrl         = idAndUrl.get("nginx_url");

        ActivityBean activity = activityService.getActivityById(lActivityId, nginxUrl);
        return Response.ok(activity).build();
    }
}
