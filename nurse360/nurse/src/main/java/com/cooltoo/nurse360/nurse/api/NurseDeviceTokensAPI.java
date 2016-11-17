package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.DeviceType;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.services.NurseDeviceTokensService;
import com.cooltoo.constants.ContextKeys;
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
@Path("/nurse/device_tokens")
public class NurseDeviceTokensAPI {

    @Autowired
    private NurseDeviceTokensService deviceTokensService;

    @POST
    @Path("/{token}/{device_type}")
    public Response registerAnonymousDeviceToken(@PathParam("token") String token,
                                                 @PathParam("device_type") String deviceToken
    ){
        deviceTokensService.registerAnonymousDeviceToken(token, DeviceType.parseString(deviceToken));
        return Response.ok().build();
    }

    @POST
    @Path("/user/{token}/{device_type}")
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response registerUserDeviceToken(@Context HttpServletRequest request,
                                            @PathParam("token") String token,
                                            @PathParam("device_type") String deviceToken
    ){
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        deviceTokensService.registerUserDeviceToken(userId, token, DeviceType.parseString(deviceToken));
        return Response.ok().build();
    }

    @POST
    @Path("/inactive/{token}")
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response inactiveUserDeviceToken(@Context HttpServletRequest request,
                                            @PathParam("token") String token){
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        deviceTokensService.inactiveUserDeviceToken(userId, token);
        return Response.ok().build();
    }

}
