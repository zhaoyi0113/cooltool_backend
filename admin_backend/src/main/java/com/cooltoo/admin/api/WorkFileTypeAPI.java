package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.WorkFileTypeBean;
import com.cooltoo.backend.services.WorkFileTypeService;
import com.cooltoo.constants.ContextKeys;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Path("/admin/workfile_type")
public class WorkFileTypeAPI {


    private static final Logger logger = LoggerFactory.getLogger(WorkFileTypeAPI.class.getName());

    @Autowired
    private WorkFileTypeService workfileTypeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllWorkfileTypes(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user : " + userId +" get all work file type");
        List<WorkFileTypeBean> workfileTypes = workfileTypeService.getAllWorkFileType();
        return Response.ok(workfileTypes).build();
    }

    @POST
    @Path("/edit")
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editBaseInformation(@Context HttpServletRequest request,
                                        @FormParam("id") @DefaultValue("-1") int id,
                                        @FormParam("name") String name,
                                        @FormParam("factor") int factor,
                                        @FormParam("max_file_count") int maxFileCount,
                                        @FormParam("min_file_count") int minFileCount) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user : " + userId +" edit work file type name : " + name + "  factor: " + factor + " maxFileCount: " + maxFileCount +" minFileCount: " + minFileCount);
        workfileTypeService.updateWorkfileType(id, name, factor, maxFileCount, minFileCount, null, null);
        return Response.ok().build();
    }

    @POST
    @Path("/edit_image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editImage(@Context HttpServletRequest request,
                                        @FormDataParam("id") @DefaultValue("-1") int id,
                                        @FormDataParam("image") InputStream image) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user : " + userId +" edit work file type enable image : " + (image!=null));
        workfileTypeService.updateWorkfileType(id, null, -1, -1, -1, image, null);
        return Response.ok().build();
    }

    @POST
    @Path("/edit_disable_image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editDisableImage(@Context HttpServletRequest request,
                                     @FormDataParam("id") @DefaultValue("-1") int id,
                                     @FormDataParam("image") InputStream image) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user : " + userId +" edit work file type disable image: " + (image!=null));
        workfileTypeService.updateWorkfileType(id, null, -1, -1, -1, null, image);
        return Response.ok().build();
    }
}
