package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.DeviceType;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.UserDeviceTokensService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by yzzhao on 4/28/16.
 */
@Path("/user/device_tokens")
public class UserDeviceTokensAPI {

    @Autowired
    private UserDeviceTokensService deviceTokensService;

    @Path("/user/{token}/{type}")
    @POST
    @LoginAuthentication(requireUserLogin = true)
    public Response registerUserDeviceToken(@Context HttpServletRequest request,
                                            @PathParam("token") String token,
                                            @PathParam("type") String strType
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        DeviceType type = DeviceType.parseString(strType);
        deviceTokensService.registerUserDeviceToken(userId, type, token);
        return Response.ok().build();
    }

    @POST
    @Path("/inactive/{token}/{type}")
    @LoginAuthentication(requireUserLogin = true)
    public Response inactiveUserDeviceToken(@Context HttpServletRequest request,
                                            @PathParam("token") String token,
                                            @PathParam("type") String strType
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        DeviceType type = DeviceType.parseString(strType);
        deviceTokensService.inactiveUserDeviceToken(userId, type, token);
        return Response.ok().build();
    }

}
