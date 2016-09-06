package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.beans.ActivityBean;
import com.cooltoo.constants.ActivityStatus;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.AdvertisementBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.AdvertisementService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/9/6.
 */
@Path("/user/advertisement")
public class AdvertisementAPI {

    @Autowired private AdvertisementService advertisementService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getAdvertisementByStatus(@Context HttpServletRequest request) {
        String status = CommonStatus.ENABLED.name();
        List<AdvertisementBean> activities = advertisementService.getAdvertisementByStatus(status);
        return Response.ok(activities).build();
    }
}
