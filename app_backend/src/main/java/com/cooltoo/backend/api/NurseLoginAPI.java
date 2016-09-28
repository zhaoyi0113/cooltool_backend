package com.cooltoo.backend.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.entities.NurseTokenAccessEntity;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseLoginService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yzzhao on 3/2/16.
 */
@Path("/nurse")
public class NurseLoginAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseLoginAPI.class.getName());

    @Autowired
    private NurseLoginService loginService;

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@Context HttpServletRequest request,
                          @FormParam("mobile") String mobile,
                          @FormParam("password") String password) {
//        try {
            NurseTokenAccessEntity token = loginService.login(mobile, password);
            logger.info("token:" + token.getToken());
            HttpSession session = request.getSession();
            session.setAttribute(ContextKeys.NURSE_LOGIN_USER_ID, token.getUserId());
            return Response.ok(token.getToken()).build();
//        }catch(BadRequestException e){
//            logger.log(Level.SEVERE, e.getMessage(), e);
//            return Response.status(2000).build();
//        }
    }

    @POST
    @Path("logout")
    @LoginAuthentication(requireNurseLogin = true)
    public Response logout(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        loginService.logout(userId);
        logger.info("logout user "+userId);
        return Response.ok().build();
    }

    @GET
    @Path("islogin")
    @LoginAuthentication(requireNurseLogin = true)
    public Response isLogin(@Context HttpServletRequest request){
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        boolean login = loginService.isLogin(userId);
        return Response.ok(login).build();
    }
}
