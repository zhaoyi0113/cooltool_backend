package com.cooltoo.backend.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.entities.TokenAccessEntity;
import com.cooltoo.filter.LoginAuthentication;
import com.cooltoo.serivces.NurseLoginService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 3/2/16.
 */
@Path("/nurse")
public class NurseLoginAPI {

    private static final Logger logger = Logger.getLogger(NurseLoginAPI.class.getName());

    @Autowired
    private NurseLoginService loginService;

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@Context HttpServletRequest request,
                          @FormParam("mobile") String mobile,
                          @FormParam("password") String password) {
        TokenAccessEntity token = loginService.login(mobile, password);
        logger.info("token:" + token.getToken());
        HttpSession session = request.getSession();
        session.setAttribute(ContextKeys.NURSE_LOGIN_USER_ID, token.getUserId());
        return Response.ok(token.getToken()).build();
    }

    @POST
    @Path("logout")
    @LoginAuthentication
    public Response logout(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        loginService.logout(userId);
        return Response.ok().build();
    }
}
