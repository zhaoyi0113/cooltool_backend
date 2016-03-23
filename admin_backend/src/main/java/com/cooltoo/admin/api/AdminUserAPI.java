package com.cooltoo.admin.api;

import com.cooltoo.admin.beans.AdminUserBean;
import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.admin.services.AdminUserService;
import com.cooltoo.constants.ContextKeys;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/3/22.
 */
@Path("/adminuser")
public class AdminUserAPI {

    @Autowired
    AdminUserService adminUserService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllUser(
           @Context HttpServletRequest request
    ) {
        long loginId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
//        // Just for debug
//        long loginId = 1;
        List<AdminUserBean> allUsers = adminUserService.getAllUsersByAdmin(loginId);
        return Response.ok(allUsers).build();
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getUser(
            @Context HttpServletRequest request,
            @PathParam("userId") long userId
    ) {
        long loginId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
//        // Just for debug
//        long loginId = 1;
        AdminUserBean user = adminUserService.getUserByAdmin(loginId, userId);
        return Response.ok(user).build();
    }

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response createNormalUser(
            @Context HttpServletRequest request,
            @FormParam("name") String name,
            @FormParam("password") String password,
            @FormParam("phone") @DefaultValue("") String telephoneNumber,
            @FormParam("email") @DefaultValue("") String email
    ) {
        long loginId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
//        // Just for debug
//        long loginId = 1;
        AdminUserBean bean = adminUserService.createUserByAdmin(loginId, name, password, telephoneNumber, email);
        return Response.ok(bean).build();
    }

    @DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteNormalUser (
        @Context HttpServletRequest request,
        @FormParam("userId") long userId
    ) {
        long loginId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
//        // Just for debug
//        long loginId = 1;
        adminUserService.deleteUserByAdmin(loginId, userId);
        return Response.ok().build();
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateUser(
            @Context HttpServletRequest request,
            @FormParam("password") String password,
            @FormParam("phone") @DefaultValue("") String telephoneNumber,
            @FormParam("email") @DefaultValue("") String email
    ) {
        long loginId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        AdminUserBean bean = adminUserService.updateUser(loginId, password, telephoneNumber, email);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/updateuser")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateUser(
            @Context HttpServletRequest request,
            @FormParam("userId") long userId,
            @FormParam("name") String newName,
            @FormParam("password") String password,
            @FormParam("phone") @DefaultValue("") String telephoneNumber,
            @FormParam("email") @DefaultValue("") String email
    ) {
        long loginId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
//        // Just for debug
//        long loginId = 1;
        AdminUserBean bean = adminUserService.updateUserByAdmin(loginId, userId, newName, password, telephoneNumber, email);
        return Response.ok(bean).build();
    }
}
