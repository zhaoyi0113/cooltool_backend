package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.service.ServiceOrderService;
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

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editServiceOrder(@Context HttpServletRequest request,
                                     @FormParam("order_id") @DefaultValue("0") long orderId,
                                     @FormParam("order_status") @DefaultValue("") String strOrderStatus,
                                     @FormParam("pay_time") @DefaultValue("") String payTime,
                                     @FormParam("payment_amount") @DefaultValue("") String paymentAmount
    ) {
        OrderStatus orderStatus = OrderStatus.parseString(strOrderStatus);
        ServiceOrderBean order = orderService.updateOrder(orderId, orderStatus, payTime, paymentAmount);
        return Response.ok(order).build();
    }
}
