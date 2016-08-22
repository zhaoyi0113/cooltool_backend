package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.ConsultationCategoryBean;
import com.cooltoo.go2nurse.service.ConsultationCategoryService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/7/15.
 */
@Path("/admin/consultation_category")
public class ConsultationCategoryManageAPI {

    @Autowired private ConsultationCategoryService categoryService;

    //==============================================================
    //                         getting
    //==============================================================
    private List<CommonStatus> getCommonStatus(String strStatus) {
        List<CommonStatus> statuses = new ArrayList<>();
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status) {
            statuses.add(status);
        }
        else if ("all".equalsIgnoreCase(strStatus)) {
            statuses = CommonStatus.getAll();
        }
        return statuses;
    }

    @Path("/category/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countTopServiceCategory(@Context HttpServletRequest request,
                                            @QueryParam("status") @DefaultValue("ALL") String strStatus
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);
        long categoryCount = categoryService.countCategoryByStatus(statuses);
        return Response.ok(categoryCount).build();
    }

    @Path("/category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopServiceCategory(@Context HttpServletRequest request,
                                          @QueryParam("status") @DefaultValue("ALL") String strStatus,
                                          @QueryParam("index") @DefaultValue("0") int pageIndex,
                                          @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);
        List<ConsultationCategoryBean> categories = categoryService.getCategoryByStatus(statuses, pageIndex, sizePerPage);
        return Response.ok(categories).build();
    }

    //==============================================================
    //                         adding
    //==============================================================

    @Path("/category")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addServiceCategory(@Context HttpServletRequest request,
                                       @FormParam("name") @DefaultValue("") String name,
                                       @FormParam("description") @DefaultValue("") String description,
                                       @FormParam("grade") @DefaultValue("0") int grade
    ) {
        ConsultationCategoryBean category = categoryService.addCategory(name, description, grade);
        return Response.ok(category).build();
    }

    //==============================================================
    //                         editing
    //==============================================================

    @Path("/category/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editServiceCategory(@Context HttpServletRequest request,
                                        @FormParam("category_id") @DefaultValue("0") long categoryId,
                                        @FormParam("name") @DefaultValue("") String name,
                                        @FormParam("description") @DefaultValue("") String description,
                                        @FormParam("grade") @DefaultValue("0") int grade,
                                        @FormParam("status") @DefaultValue("") String status
    ) {
        ConsultationCategoryBean category = categoryService.updateCategory(categoryId, name, description, grade, status);
        return Response.ok(category).build();
    }

    @Path("/category/edit_image")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response editServiceCategoryImage(@Context HttpServletRequest request,
                                             @FormDataParam("category_id") @DefaultValue("0") long categoryId,
                                             @FormDataParam("image_name") @DefaultValue("") String imageName,
                                             @FormDataParam("image") InputStream image,
                                             @FormDataParam("image")FormDataContentDisposition disposition
    ) {
        ConsultationCategoryBean category = categoryService.updateCategoryImage(categoryId, imageName, image);
        return Response.ok(category).build();
    }
}
