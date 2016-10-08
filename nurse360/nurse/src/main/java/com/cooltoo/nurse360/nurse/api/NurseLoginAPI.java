package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.entities.NurseTokenAccessEntity;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.nurse360.service.NurseLoginServiceForNurse360;
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
@Path("/nurse")
public class NurseLoginAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseLoginAPI.class.getName());

    @Autowired
    private NurseLoginServiceForNurse360 loginService;

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@Context HttpServletRequest request,
                          @FormParam("mobile") String mobile,
                          @FormParam("password") String password
    ) {
        NurseTokenAccessEntity token = loginService.login(mobile, password);
        logger.info("token:" + token.getToken());
        HttpSession session = request.getSession();
        session.setAttribute(ContextKeys.NURSE_LOGIN_USER_ID, token.getUserId());
        return Response.ok(token.getToken()).build();
    }

    @Path("/logout")
    @PUT
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response logout(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        loginService.logout(userId);
        logger.info("logout user "+userId);
        return Response.ok().build();
    }

    @Path("/is_login")
    @GET
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response isLogin(@Context HttpServletRequest request){
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        boolean login = loginService.isLogin(userId);
        return Response.ok(login).build();
    }
}
