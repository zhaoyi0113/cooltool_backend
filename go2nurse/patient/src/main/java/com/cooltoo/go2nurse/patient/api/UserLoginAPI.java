package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.entities.UserTokenAccessEntity;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by yzzhao on 3/2/16.
 */
@Path("/user/login_logout")
public class UserLoginAPI {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginAPI.class);

    @Autowired private UserLoginService loginService;

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@Context HttpServletRequest request,
                          @FormParam("mobile") String mobile,
                          @FormParam("password") String password,
                          @FormParam("channel") String channel,
                          @FormParam("channelid")String channelId
    ) {
        UserTokenAccessEntity token = loginService.login(mobile, password, channel, channelId);
        logger.info("token:" + token.getToken());
        HttpSession session = request.getSession();
        session.setAttribute(ContextKeys.USER_LOGIN_USER_ID, token.getUserId());
        return Response.ok(token.getToken()).build();
    }

    @Path("/logout")
    @POST
    @LoginAuthentication(requireUserLogin = true)
    public Response logout(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        loginService.logout(userId);
        logger.info("logout user "+userId);
        return Response.ok().build();
    }

    @Path("/is_login")
    @GET
    @LoginAuthentication(requireUserLogin = true)
    public Response isLogin(@Context HttpServletRequest request){
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        boolean login = loginService.isLogin(userId);
        return Response.ok(login).build();
    }
}
