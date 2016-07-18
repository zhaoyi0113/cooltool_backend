package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.ServiceCategoryBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.service.ServiceCategoryAndItemService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

/**
 * Created by hp on 2016/7/15.
 */
@Path("/admin/service_category_item")
public class ServiceCategoryAndItemManageAPI {

    @Autowired private ServiceCategoryAndItemService categoryAndItemService;

    //==============================================================
    //                         getting
    //==============================================================

    @Path("/category/top/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countTopServiceCategory(@Context HttpServletRequest request) {
        long topServiceCategoryCount = categoryAndItemService.countTopCategory();
        return Response.ok(topServiceCategoryCount).build();
    }

    @Path("/category/top")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopServiceCategory(@Context HttpServletRequest request,
                                          @QueryParam("index") @DefaultValue("0") int pageIndex,
                                          @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<ServiceCategoryBean> topServiceCategories = categoryAndItemService.getTopCategory(pageIndex, sizePerPage);
        return Response.ok(topServiceCategories).build();
    }

    @Path("/category/sub/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countSubServiceCategory(@Context HttpServletRequest request,
                                            @QueryParam("category_id") @DefaultValue("0") long categoryId) {
        long subServiceCategoryCount = categoryAndItemService.countCategoryByParentId(categoryId);
        return Response.ok(subServiceCategoryCount).build();
    }

    @Path("/category/sub")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubServiceCategory(@Context HttpServletRequest request,
                                          @QueryParam("category_id") @DefaultValue("0") long categoryId,
                                          @QueryParam("index") @DefaultValue("0") int pageIndex,
                                          @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<ServiceCategoryBean> serviceCategories = categoryAndItemService.getCategoryByParentId(
                categoryId, pageIndex, sizePerPage);
        return Response.ok(serviceCategories).build();
    }

    @Path("/item/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countServiceItem(@Context HttpServletRequest request,
                                     @QueryParam("category_id") @DefaultValue("0") long categoryId) {
        long serviceItemCount = categoryAndItemService.countItemByCategoryId(categoryId);
        return Response.ok(serviceItemCount).build();
    }

    @Path("/item")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceItem(@Context HttpServletRequest request,
                                   @QueryParam("category_id") @DefaultValue("0") long categoryId,
                                   @QueryParam("index") @DefaultValue("0") int pageIndex,
                                   @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<ServiceItemBean> serviceItems = categoryAndItemService.getItemByCategoryId(
                categoryId, pageIndex, sizePerPage);
        return Response.ok(serviceItems).build();
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
                                       @FormParam("grade") @DefaultValue("0") int grade,
                                       @FormParam("parent_id") @DefaultValue("0") long parentId
    ) {
        ServiceCategoryBean category = categoryAndItemService.addCategory(name, description, grade, parentId);
        return Response.ok(category).build();
    }

    @Path("/item")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addServiceItem(@Context HttpServletRequest request,
                                   @FormParam("name") @DefaultValue("") String name,
                                   @FormParam("clazz") @DefaultValue("") String clazz,
                                   @FormParam("description") @DefaultValue("") String description,
                                   @FormParam("price") @DefaultValue("") String price,
                                   @FormParam("time_duration") @DefaultValue("0") int timeDuration,
                                   @FormParam("time_unit") @DefaultValue("") String timeUnit,
                                   @FormParam("grade") @DefaultValue("0") int grade,
                                   @FormParam("category_id") @DefaultValue("0") long categoryId
    ) {
        ServiceItemBean serviceItem = categoryAndItemService.addItem(name, clazz, description, price, timeDuration, timeUnit, grade, categoryId);
        return Response.ok(serviceItem).build();
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
                                        @FormParam("parent_id") @DefaultValue("0") long parentId,
                                        @FormParam("status") @DefaultValue("") String status
    ) {
        ServiceCategoryBean category = categoryAndItemService.updateCategory(categoryId, name, description, grade, parentId, status);
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
        ServiceCategoryBean category = categoryAndItemService.updateCategoryImage(categoryId, imageName, image);
        return Response.ok(category).build();
    }

    @Path("/item/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editServiceItem(@Context HttpServletRequest request,
                                    @FormParam("item_id") @DefaultValue("0") long itemId,
                                    @FormParam("name") @DefaultValue("") String name,
                                    @FormParam("clazz") @DefaultValue("") String clazz,
                                    @FormParam("description") @DefaultValue("") String description,
                                    @FormParam("price") @DefaultValue("") String price,
                                    @FormParam("time_duration") @DefaultValue("0") int timeDuration,
                                    @FormParam("time_unit") @DefaultValue("") String timeUnit,
                                    @FormParam("grade") @DefaultValue("0") int grade,
                                    @FormParam("category_id") @DefaultValue("0") long categoryId,
                                    @FormParam("status") @DefaultValue("") String status
    ) {
        ServiceItemBean serviceItem = categoryAndItemService.updateItem(itemId, name, clazz, description, price, timeDuration, timeUnit, grade, categoryId, status);
        return Response.ok(serviceItem).build();
    }

    @Path("/item/edit_image")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response editServiceItemImage(@Context HttpServletRequest request,
                                         @FormDataParam("item_id") @DefaultValue("0") long itemId,
                                         @FormDataParam("image_name") @DefaultValue("") String imageName,
                                         @FormDataParam("image") InputStream image,
                                         @FormDataParam("image")FormDataContentDisposition disposition
    ) {
        ServiceItemBean serviceItem = categoryAndItemService.updateItemImage(itemId, imageName, image);
        return Response.ok(serviceItem).build();
    }

}
