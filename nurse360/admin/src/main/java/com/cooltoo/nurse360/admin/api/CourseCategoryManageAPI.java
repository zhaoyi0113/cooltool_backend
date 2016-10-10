package com.cooltoo.nurse360.admin.api;

import com.cooltoo.nurse360.beans.Nurse360CourseCategoryBean;
import com.cooltoo.nurse360.service.CourseCategoryServiceForNurse360;
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

@Path("/admin/course/category")
public class CourseCategoryManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(CourseCategoryManageAPI.class);

    @Autowired private CourseCategoryServiceForNurse360 categoryService;


    @Path("/{category_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategoryById(@Context HttpServletRequest request,
                                          @PathParam("category_id") @DefaultValue("0") long categoryId
    ) {
        logger.info("get category by category id");
        Nurse360CourseCategoryBean category = categoryService.getCategoryById(categoryId);
        return Response.ok(category).build();
    }

    // status ==> all/enabled/disabled/deleted
    @Path("/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countCategory(@Context HttpServletRequest request,
                                  @PathParam("status") @DefaultValue("") String status
    ) {
        logger.info("get category count by status={}", status);
        long count = categoryService.countByStatus(status);
        logger.info("count = {}", count);
        return Response.ok(count).build();
    }

    // status ==> all/enabled/disabled/deleted
    @Path("/{status}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategoryByStatus(@Context HttpServletRequest request,
                                      @PathParam("status") @DefaultValue("") String status,
                                      @PathParam("index")  @DefaultValue("0") int index,
                                      @PathParam("number") @DefaultValue("10") int number
    ) {
        logger.info("get category by status={} at page={}, {}/page", status, index, number);
        List<Nurse360CourseCategoryBean> categories = categoryService.getCategoryByStatus(status, index, number);
        logger.info("count = {}", categories.size());
        return Response.ok(categories).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCategory(@Context HttpServletRequest request,
                                @FormParam("name") @DefaultValue("") String name,
                                @FormParam("introduction") @DefaultValue("") String introduction
    ) {
        logger.info("new category");
        Nurse360CourseCategoryBean category = categoryService.addCategory(name, introduction, null, null);
        return Response.ok(category).build();
    }

    @Path("/edit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCategory(@Context HttpServletRequest request,
                                   @FormParam("category_id") @DefaultValue("0") long categoryId,
                                   @FormParam("name") @DefaultValue("") String name,
                                   @FormParam("introduction") @DefaultValue("") String introduction,
                                   @FormParam("status") @DefaultValue("disabled") String status
    ) {
        logger.info("update category");
        Nurse360CourseCategoryBean category = categoryService.updateCategory(categoryId, name, introduction, status, null, null);
        return Response.ok(category).build();
    }

    @Path("/edit/image")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateCategory(@Context HttpServletRequest request,
                                   @FormDataParam("category_id") @DefaultValue("0") long categoryId,
                                   @FormDataParam("image_name") @DefaultValue("") String imageName,
                                   @FormDataParam("image") InputStream image,
                                   @FormDataParam("image") FormDataContentDisposition disposition
    ) {
        logger.info("update category front cover");
        Nurse360CourseCategoryBean category = categoryService.updateCategory(categoryId, null, null, null, imageName, image);
        return Response.ok(category).build();
    }
}