package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.WorkFileTypeBean;
import com.cooltoo.backend.services.WorkFileTypeService;
import com.cooltoo.constants.ContextKeys;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Path("/admin/workfile_type")
public class WorkFileTypeAPI {


    private static final Logger logger = Logger.getLogger(SpeakTypeAPI.class.getName());

    @Autowired
    private WorkFileTypeService workfileTypeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllWorkfileTypes(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user : " + userId +" get all work file type");
        List<WorkFileTypeBean> speakTypes = workfileTypeService.getAllWorkFileType();
        return Response.ok(speakTypes).build();
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
        workfileTypeService.updateSpeakType(id, name, factor, maxFileCount, minFileCount, null, null);
        return Response.ok().build();
    }

    @POST
    @Path("/edit_image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editImage(@Context HttpServletRequest request,
                                        @FormParam("id") @DefaultValue("-1") int id,
                                        @FormParam("image") InputStream image,
                                        @FormParam("disable_image") InputStream disableImage) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user : " + userId +" edit work file type enable image : " + (image!=null) + "  disable image: " + (disableImage!=null));
        workfileTypeService.updateSpeakType(id, null, -1, -1, -1, image, disableImage);
        return Response.ok().build();
    }
}
