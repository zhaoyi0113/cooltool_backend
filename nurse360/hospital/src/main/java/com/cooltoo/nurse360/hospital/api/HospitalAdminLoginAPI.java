package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.nurse360.beans.HospitalAdminAccessTokenBean;
import com.cooltoo.nurse360.service.hospital.HospitalAdminAccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/10.
 */
@Path("/hospital_management/admin")
public class HospitalAdminLoginAPI {

    @Autowired private HospitalAdminAccessTokenService adminAccessTokenService;

    @Path("/login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpServletRequest request,
                          @FormParam("name") String userName,
                          @FormParam("password") String password
    ) {
        HospitalAdminAccessTokenBean token = adminAccessTokenService.addToken(userName, password);
        HttpSession session = request.getSession();
        session.setAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID, token.getAdminId());
        Map<String, String> retVal = new HashMap<>();
        retVal.put("token", token.getToken());
        return Response.ok(retVal).build();
    }

    @Path("/logout")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletRequest request) {
        String adminToken = (String) request.getAttribute(ContextKeys.ADMIN_USER_TOKEN);
        adminAccessTokenService.setTokenDisable(adminToken);
        return Response.ok().build();
    }

    @Path("/is_login")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response isLogin(@Context HttpServletRequest request) {
        String token = (String) request.getAttribute(ContextKeys.ADMIN_USER_TOKEN);
        boolean login = adminAccessTokenService.isTokenEnable(token);
        Map<String, Boolean> retVal = new HashMap<>();
        retVal.put("isLogin", login);
        return Response.ok(retVal).build();
    }
}
