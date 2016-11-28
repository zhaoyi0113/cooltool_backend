package com.cooltoo.admin.api;

import com.cooltoo.beans.AdminUserTokenAccessBean;
import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.services.AdminUserLoginService;
import com.cooltoo.constants.ContextKeys;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by zhaolisong on 16/3/22.
 */
@Path("/admin/adminuser")
public class AdminUserLoginAPI {

    @Autowired
    AdminUserLoginService loginService;

    @POST
    @Path("login")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(
            @Context HttpServletRequest request,
            @FormParam("name") String userName,
            @FormParam("password") String password
    ) {
        AdminUserTokenAccessBean token = loginService.login(userName, password);
        HttpSession session = request.getSession();
        session.setAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID, token.getUserId());
        return Response.ok(token.getToken()).build();
    }

    @POST
    @Path("logout")
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response logout(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        loginService.logout(userId);
        return Response.ok().build();
    }

    @GET
    @Path("isLogin")
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response isLogin(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        boolean login = loginService.isLogin(userId);
        return Response.ok(login).build();
    }

}
