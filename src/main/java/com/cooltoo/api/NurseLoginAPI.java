package com.cooltoo.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.entities.TokenAccessEntity;
import com.cooltoo.serivces.NurseLoginService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by yzzhao on 3/2/16.
 */
@Path("/nurse/login")
public class NurseLoginAPI {

    @Autowired
    private NurseLoginService loginService;

    @POST
    public Response login(@Context HttpServletRequest request,
                          @FormParam("mobile") String mobile,
                          @FormParam("password") String password) {
        TokenAccessEntity token = loginService.login(mobile, password);
        HttpSession session = request.getSession();
        session.setAttribute(ContextKeys.NURSE_LOGIN, token.getUserId());
        return Response.ok().build();
    }
}
