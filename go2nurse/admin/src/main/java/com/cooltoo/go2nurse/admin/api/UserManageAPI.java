package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.UserAddressBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.service.PatientService;
import com.cooltoo.go2nurse.service.UserAddressService;
import com.cooltoo.go2nurse.service.UserPatientRelationService;
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

    @Autowired private UserService userService;
    @Autowired private UserAddressService userAddressService;
    @Autowired private UserPatientRelationService userPatientRelationService;
    @Autowired private PatientService patientService;

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
        UserBean user = userService.updateUser(userId, name, gender, birthday, authority, address, hasDecide);
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
        UserBean user = userService.updateProfilePhoto(userId, imageName, image);
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
        UserBean bean = userService.updateMobilePassword(userId, newMobile, newPassword);
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
        long count = userService.countByAuthorityAndFuzzyName(authority, userName);
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
        List<UserBean> users = userService.getAllByAuthorityAndFuzzyName(authority, userName, pageIndex, sizePerPage);
        return Response.ok(users).build();
    }

    @Path("/patient")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserPatient(@Context HttpServletRequest request,
                                   @QueryParam("user_id") @DefaultValue("0") long userId
    ) {
        List<Long> patientIds = userPatientRelationService.getPatientByUser(userId, "all");
        List<PatientBean> patients = patientService.getAllByStatusAndIds(patientIds, null);
        return Response.ok(patients).build();
    }

    @Path("/address")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserAddress(@Context HttpServletRequest request,
                                   @QueryParam("user_id") @DefaultValue("0") long userId
    ) {
        List<UserAddressBean> addresses = userAddressService.getUserAddress(userId);
        return Response.ok(addresses).build();
    }
}