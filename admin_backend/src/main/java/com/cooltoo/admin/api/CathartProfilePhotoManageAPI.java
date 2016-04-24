package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.CathartProfilePhotoBean;
import com.cooltoo.backend.services.CathartProfilePhotoService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.util.VerifyUtil;
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
 * Created by hp on 2016/4/19.
 */
@Path("/admin/speak/cathart_profile")
public class CathartProfilePhotoManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(CathartProfilePhotoManageAPI.class.getName());

    @Autowired private CathartProfilePhotoService cathartPhotoService;

    @Path("/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countByStatus(@Context HttpServletRequest request,
                                  @PathParam("status") @DefaultValue("all") String status
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get cathart profile photo count by status {}", userId, status);
        long count = cathartPhotoService.countByStatus(status);
        logger.info("count is {}", count);
        return Response.ok(count).build();
    }

    @Path("/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getByStatus(@Context HttpServletRequest request,
                                @PathParam("status") @DefaultValue("all") String status
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get cathart profile photo by status {}", userId, status);
        List<CathartProfilePhotoBean> beans = cathartPhotoService.getAllByStatus(status);
        logger.info("count is {}", beans.size());
        return Response.ok(beans).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response createCathartPhoto(@Context HttpServletRequest request,
                                       @FormDataParam("name") String name,
                                       @FormDataParam("status") String status,
                                       @FormDataParam("image_name") String imageName,
                                       @FormDataParam("image") InputStream image,
                                       @FormDataParam("image")FormDataContentDisposition disposition
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} create cathart profile photo by name={} status={} imageName={} image={}", userId, name, status, imageName, image);
        if (VerifyUtil.isStringEmpty(imageName) && null!=disposition) {
            imageName = disposition.getFileName();
        }
        CathartProfilePhotoBean bean = cathartPhotoService.createCathartProfilePhoto(name, imageName, image, status);
        logger.info("new one is {}", bean);
        return Response.ok(bean).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editWithoutImage(@Context HttpServletRequest request,
                                     @FormParam("id") long recordId,
                                     @FormParam("name") String name,
                                     @FormParam("status") String status
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update cathart profile photo by name={} status={}", userId, name, status);
        CathartProfilePhotoBean bean = cathartPhotoService.updateCathartProfilePhoto(recordId, name, null, null, status);
        logger.info("after update is {}", bean);
        return Response.ok(bean).build();
    }

    @Path("/edit_image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editImage(@Context HttpServletRequest request,
                              @FormDataParam("id") long recordId,
                              @FormDataParam("image_name") String imageName,
                              @FormDataParam("image") InputStream image,
                              @FormDataParam("image")FormDataContentDisposition disposition
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} create cathart profile photo by imageName={} image={}", userId, imageName, image);
        if (VerifyUtil.isStringEmpty(imageName) && null!=disposition) {
            imageName = disposition.getFileName();
        }
        CathartProfilePhotoBean bean = cathartPhotoService.updateCathartProfilePhoto(recordId, null, imageName, image, null);
        logger.info("after update is {}", bean);
        return Response.ok(bean).build();
    }

    @Path("/disable")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response disableCathartPhotos(@Context HttpServletRequest request,
                                         @FormParam("id") String ids
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} disable cathart profile photo by ids={}", userId, ids);
        String disabledIds = cathartPhotoService.setStatusByIds(ids);
        logger.info("disable ids is {}", disabledIds);
        return Response.ok(disabledIds).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteCathartPhotos(@Context HttpServletRequest request,
                                        @FormParam("id") String ids
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} delete cathart profile photo by ids={}", userId, ids);
        String deletedIds = cathartPhotoService.deleteByIds(ids);
        logger.info("delete ids is {}", deletedIds);
        return Response.ok(deletedIds).build();
    }
}
