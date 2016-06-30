package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.service.UserService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

/**
 * Created by hp on 2016/6/13.
 */
@Path("/admin/user")
public class UserManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(UserManageAPI.class.getName());

    @Autowired private UserService service;

    @Path("/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@Context HttpServletRequest request,
                               @FormParam("user_id") @DefaultValue("0") long userId,
                               @FormParam("name") @DefaultValue("") String name,
                               @FormParam("gender") @DefaultValue("2") int gender,
                               @FormParam("birthday") @DefaultValue("") String birthday,
                               @FormParam("address") @DefaultValue("") String address,
                               @FormParam("authority") @DefaultValue("1") int authority,
                               @FormParam("has_decide") @DefaultValue("NO") String hasDecide
    ) {
        UserBean user = service.updateUser(userId, name, gender, birthday, authority, address, hasDecide);
        logger.info("update user is " + user);
        if (null == user) {
            return Response.ok().build();
        }
        return Response.ok(user).build();
    }

    @POST
    @Path("/edit_head_photo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateProfilePhoto(@Context HttpServletRequest request,
                                       @FormDataParam("user_id") @DefaultValue("0") long userId,
                                       @FormDataParam("image_name") @DefaultValue("") String imageName,
                                       @FormDataParam("file") InputStream image,
                                       @FormDataParam("file") FormDataContentDisposition disposition
    ) {
        UserBean user = service.updateProfilePhoto(userId, imageName, image);
        logger.info("upload successfully");
        return Response.ok(user).build();
    }

    @Path("/modify/mobile_or_password")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMobilePassword(@Context HttpServletRequest request,
                                         @FormParam("user_id") @DefaultValue("0") long userId,
                                         @FormParam("new_mobile") @DefaultValue("") String newMobile,
                                         @FormParam("new_password") @DefaultValue("") String newPassword
    ) {
        UserBean bean = service.updateMobilePassword(userId, newMobile, newPassword);
        logger.info("update user mobile and password is " + bean);
        return Response.ok(bean).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countUser(@Context HttpServletRequest request,
                              @QueryParam("authority") @DefaultValue("") String authority,
                              @QueryParam("user_name") @DefaultValue("") String userName
    ) {
        long count = service.countByAuthorityAndFuzzyName(authority, userName);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@Context HttpServletRequest request,
                            @QueryParam("authority") @DefaultValue("") String authority,
                            @QueryParam("user_name") @DefaultValue("") String userName,
                            @QueryParam("index") @DefaultValue("0") int pageIndex,
                            @QueryParam("number") @DefaultValue("10") int sizePerPage

    ) {
        List<UserBean> users = service.getAllByAuthorityAndFuzzyName(authority, userName, pageIndex, sizePerPage);
        return Response.ok(users).build();
    }

}