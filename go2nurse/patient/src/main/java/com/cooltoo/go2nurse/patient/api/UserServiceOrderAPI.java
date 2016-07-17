package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.ServiceCategoryBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.ServiceCategoryAndItemService;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/7/15.
 */
@Path("/user/order")
public class UserServiceOrderAPI {

    @Autowired private ServiceOrderService orderService;
    @Autowired private ServiceCategoryAndItemService serviceCategoryItemService;

    @Path("/category/top")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getServiceCategory(@Context HttpServletRequest request) {
        List<ServiceCategoryBean> topCategories = serviceCategoryItemService.getCategoryByParentId(0L);
        return Response.ok(topCategories).build();
    }

    @Path("/category/sub_category")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getSubServiceCategory(@Context HttpServletRequest request,
                                          @QueryParam("category_id") @DefaultValue("0") long parentId
    ) {
        List<ServiceCategoryBean> subCategories = serviceCategoryItemService.getCategoryByParentId(parentId);
        return Response.ok(subCategories).build();
    }

    @Path("/category/item")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getServiceItemByCategoryId(@Context HttpServletRequest request,
                                               @QueryParam("category_id") @DefaultValue("0") long categoryId
    ) {
        List<ServiceItemBean> serviceItems = serviceCategoryItemService.getItemByCategoryId(categoryId);
        return Response.ok(serviceItems).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserOrder(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<ServiceOrderBean> userOrders = orderService.getOrderByUserId(userId);
        return Response.ok(userOrders).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response userAddOrder(@Context HttpServletRequest request,
                                 @FormParam("service_item_id") @DefaultValue("0") long serviceItemId,
                                 @FormParam("patient_id") @DefaultValue("0") long patientId,
                                 @FormParam("address_id") @DefaultValue("0") long addressId,
                                 @FormParam("start_time") @DefaultValue("") String startTime,
                                 @FormParam("time_duration") @DefaultValue("0") int timeDuration,
                                 @FormParam("time_unit") @DefaultValue("") String timeUnit,
                                 @FormParam("total_consumption") @DefaultValue("") String totalConsumption
                                 ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        ServiceOrderBean order = orderService.addOrder(serviceItemId, userId, patientId, addressId, startTime, timeDuration, timeUnit, totalConsumption);
        return Response.ok(order).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response userEditOrder(@Context HttpServletRequest request,
                                  @FormParam("order_id") @DefaultValue("0") long orderId,
                                  @FormParam("service_item_id") @DefaultValue("0") long serviceItemId,
                                  @FormParam("patient_id") @DefaultValue("0") long patientId,
                                  @FormParam("address_id") @DefaultValue("0") long addressId,
                                  @FormParam("start_time") @DefaultValue("") String startTime,
                                  @FormParam("time_duration") @DefaultValue("0") int timeDuration,
                                  @FormParam("time_unit") @DefaultValue("") String timeUnit,
                                  @FormParam("total_consumption") @DefaultValue("") String totalConsumption
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        ServiceOrderBean order = orderService.updateOrder(orderId, patientId, addressId, startTime, timeDuration, timeUnit, totalConsumption);
        return Response.ok(order).build();
    }

    @Path("/edit/order_status")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response userEditOrder(@Context HttpServletRequest request,
                                  @FormParam("order_id") @DefaultValue("0") long orderId,
                                  @FormParam("order_status") @DefaultValue("") String orderStatus,
                                  @FormParam("pay_time") @DefaultValue("") String payTime,
                                  @FormParam("payment_amount") @DefaultValue("") String paymentAmount
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        ServiceOrderBean order = orderService.updateOrder(orderId, OrderStatus.parseString(orderStatus), payTime, paymentAmount);
        return Response.ok(order).build();
    }

}
