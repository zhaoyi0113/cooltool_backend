package com.cooltoo.backend.api;

import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseDeviceTokensService;
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
    @Path("{token}")
    public Response registerAnonymousDeviceToken(@PathParam("token") String token){
        deviceTokensService.registerAnonymousDeviceToken(token);
        return Response.ok().build();
    }

    @POST
    @Path("/user/{token}")
    @LoginAuthentication(requireNurseLogin = true)
    public Response registerUserDeviceToken(@Context HttpServletRequest request,
                                            @PathParam("token") String token){
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        deviceTokensService.registerUserDeviceToken(userId, token);
        return Response.ok().build();
    }

    @POST
    @Path("/inactive/{token}")
    @LoginAuthentication(requireNurseLogin = true)
    public Response inactievUserDeviceToken(@Context HttpServletRequest request,
                                            @PathParam("token") String token){
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        deviceTokensService.inactiveUserDeviceToken(userId, token);
        return Response.ok().build();
    }

}
