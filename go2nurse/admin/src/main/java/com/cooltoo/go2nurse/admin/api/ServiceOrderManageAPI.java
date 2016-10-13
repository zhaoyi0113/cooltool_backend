package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/7/17.
 */
@Path("/admin/service_order")
public class ServiceOrderManageAPI {

    @Autowired private ServiceOrderService orderService;

    @Path("/all_order_status")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrder(@Context HttpServletRequest request) {
        return Response.ok(OrderStatus.getAll()).build();
    }

    @Path("/count/by_conditions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countOrderByConditions(@Context HttpServletRequest request,
                                         @QueryParam("service_item_id") @DefaultValue("") String strItemId,
                                         @QueryParam("user_id") @DefaultValue("") String strUserId,
                                         @QueryParam("category_id") @DefaultValue("") String strCategoryId,
                                         @QueryParam("top_category_id") @DefaultValue("") String strTopCategoryId,
                                         @QueryParam("vendor_type") @DefaultValue("") String strVendorType,
                                         @QueryParam("vendor_id") @DefaultValue("") String strVendorId,
                                         @QueryParam("order_status") @DefaultValue("") String strOrderStatus
    ) {
        Long itemId = !VerifyUtil.isIds(strItemId) ? null : VerifyUtil.parseLongIds(strItemId).get(0);
        Long userId = !VerifyUtil.isIds(strUserId) ? null : VerifyUtil.parseLongIds(strUserId).get(0);
        Long categoryId = !VerifyUtil.isIds(strCategoryId) ? null : VerifyUtil.parseLongIds(strCategoryId).get(0);
        Long topCategoryId = !VerifyUtil.isIds(strTopCategoryId) ? null : VerifyUtil.parseLongIds(strTopCategoryId).get(0);

        Long vendorId = !VerifyUtil.isIds(strVendorId) ? null : VerifyUtil.parseLongIds(strVendorId).get(0);
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        if (vendorId==null || vendorType==null) {
            vendorId = null;
            vendorType = null;
        }

        OrderStatus orderStatus = OrderStatus.parseString(strOrderStatus);
        long count = orderService.countOrderByConditions(itemId, userId, categoryId, topCategoryId, vendorId, vendorType, orderStatus);
        return Response.ok(count).build();
    }

    @Path("/by_conditions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderByConditions(@Context HttpServletRequest request,
                                         @QueryParam("service_item_id") @DefaultValue("") String strItemId,
                                         @QueryParam("user_id") @DefaultValue("") String strUserId,
                                         @QueryParam("category_id") @DefaultValue("") String strCategoryId,
                                         @QueryParam("top_category_id") @DefaultValue("") String strTopCategoryId,
                                         @QueryParam("vendor_type") @DefaultValue("") String strVendorType,
                                         @QueryParam("vendor_id") @DefaultValue("") String strVendorId,
                                         @QueryParam("order_status") @DefaultValue("") String strOrderStatus,
                                         @QueryParam("index") @DefaultValue("0") int pageIndex,
                                         @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        Long itemId = !VerifyUtil.isIds(strItemId) ? null : VerifyUtil.parseLongIds(strItemId).get(0);
        Long userId = !VerifyUtil.isIds(strUserId) ? null : VerifyUtil.parseLongIds(strUserId).get(0);
        Long categoryId = !VerifyUtil.isIds(strCategoryId) ? null : VerifyUtil.parseLongIds(strCategoryId).get(0);
        Long topCategoryId = !VerifyUtil.isIds(strTopCategoryId) ? null : VerifyUtil.parseLongIds(strTopCategoryId).get(0);

        Long vendorId = !VerifyUtil.isIds(strVendorId) ? null : VerifyUtil.parseLongIds(strVendorId).get(0);
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        if (vendorId==null || vendorType==null) {
            vendorId = null;
            vendorType = null;
        }

        OrderStatus orderStatus = OrderStatus.parseString(strOrderStatus);
        List<ServiceOrderBean> orders = orderService.getOrderByConditions(itemId, userId, categoryId, topCategoryId, vendorId, vendorType, orderStatus, pageIndex, sizePerPage);
        return Response.ok(orders).build();
    }

    @Path("/by_user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceOrderByUser(@Context HttpServletRequest request,
                                          @QueryParam("user_id") @DefaultValue("0") long userId
    ) {
        List<ServiceOrderBean> orders = orderService.getOrderByUserId(userId);
        return Response.ok(orders).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countServiceOrder(@Context HttpServletRequest request) {
        long countOfOrder = orderService.countAllOrder();
        return Response.ok(countOfOrder).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceOrder(@Context HttpServletRequest request,
                                    @QueryParam("index") @DefaultValue("0") int pageIndex,
                                    @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<ServiceOrderBean> orders = orderService.getOrder(pageIndex, sizePerPage);
        return Response.ok(orders).build();
    }

    @Path("/by_order_id")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserOrderByOrderId(@Context HttpServletRequest request,
                                          @QueryParam("order_id") @DefaultValue("0") long orderId
    ) {
        List<ServiceOrderBean> orders = orderService.getOrderByOrderId(orderId);
        return Response.ok(orders.get(0)).build();
    }

    @Path("/cancel")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelOrder(@Context HttpServletRequest request,
                                @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        ServiceOrderBean order = orderService.cancelOrder(false, -1, orderId);
        return Response.ok(order).build();
    }

    @Path("/in_process")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderInProcess(@Context HttpServletRequest request,
                                   @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        ServiceOrderBean order = orderService.orderInProcess(false, -1, orderId);
        return Response.ok(order).build();
    }

    @Path("/completed")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderCompleted(@Context HttpServletRequest request,
                                   @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        ServiceOrderBean order = orderService.completedOrder(false, -1, orderId);
        return Response.ok(order).build();
    }
}
