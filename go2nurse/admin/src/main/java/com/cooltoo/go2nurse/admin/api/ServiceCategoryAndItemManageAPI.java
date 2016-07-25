package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.ServiceCategoryBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.beans.ServiceVendorBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.ServiceVendorCategoryAndItemService;
import com.cooltoo.util.VerifyUtil;
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
@Path("/admin/service_category_item")
public class ServiceCategoryAndItemManageAPI {

    @Autowired private ServiceVendorCategoryAndItemService vendorCategoryAndItemService;

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
            statuses.add(CommonStatus.DISABLED);
            statuses.add(CommonStatus.ENABLED);
        }
        return statuses;
    }

    @Path("/vendor/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countVendor(@Context HttpServletRequest request,
                                @QueryParam("status") @DefaultValue("ALL") String strStatus
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);
        long vendorCount = vendorCategoryAndItemService.countVendor(statuses);
        return Response.ok(vendorCount).build();
    }

    @Path("/vendor")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVendor(@Context HttpServletRequest request,
                              @QueryParam("status") @DefaultValue("ALL") String strStatus,
                              @QueryParam("index") @DefaultValue("0") int pageIndex,
                              @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        List<ServiceVendorBean> serviceVendor = vendorCategoryAndItemService.getVendor(statuses, pageIndex, sizePerPage);
        return Response.ok(serviceVendor).build();
    }

    @Path("/category/top/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countTopServiceCategory(@Context HttpServletRequest request,
                                            @QueryParam("status") @DefaultValue("ALL") String strStatus
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        long topServiceCategoryCount = vendorCategoryAndItemService.countTopCategory(statuses);
        return Response.ok(topServiceCategoryCount).build();
    }

    @Path("/category/top")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopServiceCategory(@Context HttpServletRequest request,
                                          @QueryParam("status") @DefaultValue("ALL") String strStatus,
                                          @QueryParam("index") @DefaultValue("0") int pageIndex,
                                          @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        List<ServiceCategoryBean> topServiceCategories = vendorCategoryAndItemService.getTopCategory(statuses, pageIndex, sizePerPage);
        return Response.ok(topServiceCategories).build();
    }

    @Path("/category/sub/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countSubServiceCategory(@Context HttpServletRequest request,
                                            @QueryParam("category_id") @DefaultValue("0") long categoryId,
                                            @QueryParam("status") @DefaultValue("ALL") String strStatus
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        long subServiceCategoryCount = vendorCategoryAndItemService.countCategoryByParentId(categoryId, statuses);
        return Response.ok(subServiceCategoryCount).build();
    }

    @Path("/category/sub")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubServiceCategory(@Context HttpServletRequest request,
                                          @QueryParam("category_id") @DefaultValue("0") long categoryId,
                                          @QueryParam("status") @DefaultValue("ALL") String strStatus,
                                          @QueryParam("index") @DefaultValue("0") int pageIndex,
                                          @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        List<ServiceCategoryBean> serviceCategories = vendorCategoryAndItemService.getCategoryByParentId(
                categoryId, statuses, pageIndex, sizePerPage);
        return Response.ok(serviceCategories).build();
    }

    @Path("/item/count_by_vendor")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countServiceItemByVendor(@Context HttpServletRequest request,
                                             @QueryParam("vendor_id") @DefaultValue("0") long vendorId,
                                             @QueryParam("status") @DefaultValue("ALL") String strStatus
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        long serviceItemCount = vendorCategoryAndItemService.countItemByVendorId(vendorId, statuses);
        return Response.ok(serviceItemCount).build();
    }

    @Path("/item/by_vendor")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceItemByVendor(@Context HttpServletRequest request,
                                           @QueryParam("vendor_id") @DefaultValue("0") long vendorId,
                                           @QueryParam("status") @DefaultValue("ALL") String strStatus,
                                           @QueryParam("index") @DefaultValue("0") int pageIndex,
                                           @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        List<ServiceItemBean> serviceItems = vendorCategoryAndItemService.getItemByVendorId(
                vendorId, statuses, pageIndex, sizePerPage);
        return Response.ok(serviceItems).build();
    }

    @Path("/item/count_by_category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countServiceItemByCategory(@Context HttpServletRequest request,
                                               @QueryParam("category_id") @DefaultValue("0") long categoryId,
                                               @QueryParam("status") @DefaultValue("ALL") String strStatus
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        long serviceItemCount = vendorCategoryAndItemService.countItemByCategoryId(categoryId, statuses);
        return Response.ok(serviceItemCount).build();
    }

    @Path("/item/by_category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceItemByCategory(@Context HttpServletRequest request,
                                             @QueryParam("category_id") @DefaultValue("0") long categoryId,
                                             @QueryParam("status") @DefaultValue("ALL") String strStatus,
                                             @QueryParam("index") @DefaultValue("0") int pageIndex,
                                             @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        List<ServiceItemBean> serviceItems = vendorCategoryAndItemService.getItemByCategoryId(
                categoryId, statuses, pageIndex, sizePerPage);
        return Response.ok(serviceItems).build();
    }

    @Path("/item/count_by_category_and_vendor")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countServiceItemByCategory(@Context HttpServletRequest request,
                                               @QueryParam("category_id") @DefaultValue("") String strCategoryId,
                                               @QueryParam("vendor_id") @DefaultValue("") String strVendorId,
                                               @QueryParam("vendor_type") @DefaultValue("") String strVendorType,
                                               @QueryParam("status") @DefaultValue("ALL") String strStatus
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        Long categoryId = !VerifyUtil.isIds(strCategoryId) ? null : VerifyUtil.parseLongIds(strCategoryId).get(0);
        Long vendorId = !VerifyUtil.isIds(strVendorId) ? null : VerifyUtil.parseLongIds(strVendorId).get(0);
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        long serviceItemCount = vendorCategoryAndItemService.countItemByCategoryVendorAndStatus(categoryId, vendorId, vendorType, statuses);
        return Response.ok(serviceItemCount).build();
    }

    @Path("/item/by_category_and_vendor")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceItemByCategory(@Context HttpServletRequest request,
                                             @QueryParam("category_id") @DefaultValue("") String strCategoryId,
                                             @QueryParam("vendor_id") @DefaultValue("") String strVendorId,
                                             @QueryParam("vendor_type") @DefaultValue("") String strVendorType,
                                             @QueryParam("status") @DefaultValue("ALL") String strStatus,
                                             @QueryParam("index") @DefaultValue("0") int pageIndex,
                                             @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        Long categoryId = !VerifyUtil.isIds(strCategoryId) ? null : VerifyUtil.parseLongIds(strCategoryId).get(0);
        Long vendorId = !VerifyUtil.isIds(strVendorId) ? null : VerifyUtil.parseLongIds(strVendorId).get(0);
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        List<ServiceItemBean> serviceItems = vendorCategoryAndItemService.getItemByCategoryVendorAndStatus(
                categoryId, vendorId, vendorType, statuses, pageIndex, sizePerPage);
        return Response.ok(serviceItems).build();
    }

    //==============================================================
    //                         adding
    //==============================================================

    @Path("/vendor")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addServiceVendor(@Context HttpServletRequest request,
                                     @FormParam("name") @DefaultValue("") String name,
                                     @FormParam("description") @DefaultValue("") String description
    ) {
        ServiceVendorBean vendor = vendorCategoryAndItemService.addVendor(name, description);
        return Response.ok(vendor).build();
    }

    @Path("/category")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addServiceCategory(@Context HttpServletRequest request,
                                       @FormParam("name") @DefaultValue("") String name,
                                       @FormParam("description") @DefaultValue("") String description,
                                       @FormParam("grade") @DefaultValue("0") int grade,
                                       @FormParam("parent_id") @DefaultValue("0") long parentId
    ) {
        ServiceCategoryBean category = vendorCategoryAndItemService.addCategory(name, description, grade, parentId);
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
                                   @FormParam("category_id") @DefaultValue("0") long categoryId,
                                   @FormParam("vendor_id") @DefaultValue("0") long vendorId,
                                   @FormParam("vendor_type") @DefaultValue("") String vendorType
    ) {
        ServiceItemBean serviceItem = vendorCategoryAndItemService.addItem(name, clazz, description, price, timeDuration, timeUnit, grade, categoryId, vendorId, vendorType);
        return Response.ok(serviceItem).build();
    }

    //==============================================================
    //                         editing
    //==============================================================

    @Path("/vendor/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editServiceVendor(@Context HttpServletRequest request,
                                      @FormParam("vendor_id") @DefaultValue("0") long vendorId,
                                      @FormParam("name") @DefaultValue("") String name,
                                      @FormParam("description") @DefaultValue("") String description,
                                      @FormParam("status") @DefaultValue("") String status
    ) {
        ServiceVendorBean vendor = vendorCategoryAndItemService.updateVendor(vendorId, name, description, status);
        return Response.ok(vendor).build();
    }

    @Path("/vendor/edit_image")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response editServiceVendorLogo(@Context HttpServletRequest request,
                                          @FormDataParam("vendor_id") @DefaultValue("0") long vendorId,
                                          @FormDataParam("image_name") @DefaultValue("") String imageName,
                                          @FormDataParam("image") InputStream image,
                                          @FormDataParam("image")FormDataContentDisposition disposition
    ) {
        ServiceVendorBean vendor = vendorCategoryAndItemService.updateVendorLogoImage(vendorId, imageName, image);
        return Response.ok(vendor).build();
    }

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
        ServiceCategoryBean category = vendorCategoryAndItemService.updateCategory(categoryId, name, description, grade, parentId, status);
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
        ServiceCategoryBean category = vendorCategoryAndItemService.updateCategoryImage(categoryId, imageName, image);
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
                                    @FormParam("vendor_id") @DefaultValue("0") long vendorId,
                                    @FormParam("vendor_type") @DefaultValue("") String vendorType,
                                    @FormParam("status") @DefaultValue("") String status
    ) {
        ServiceItemBean serviceItem = vendorCategoryAndItemService.updateItem(itemId, name, clazz, description, price, timeDuration, timeUnit, grade, categoryId, vendorId, vendorType, status);
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
        ServiceItemBean serviceItem = vendorCategoryAndItemService.updateItemImage(itemId, imageName, image);
        return Response.ok(serviceItem).build();
    }

}
