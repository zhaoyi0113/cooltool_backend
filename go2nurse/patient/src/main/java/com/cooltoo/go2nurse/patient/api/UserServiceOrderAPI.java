package com.cooltoo.go2nurse.patient.api;

import com.pingplusplus.model.Charge;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.ServiceCategoryBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.ServiceVendorCategoryAndItemService;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by hp on 2016/7/15.
 */
@Path("/user/order")
public class UserServiceOrderAPI {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceOrderAPI.class);

    @Autowired private ServiceOrderService orderService;
    @Autowired private ServiceVendorCategoryAndItemService serviceCategoryItemService;

    @Path("/category/top")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getServiceCategory(@Context HttpServletRequest request) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        List<ServiceCategoryBean> topCategories = serviceCategoryItemService.getCategoryByParentId(0L, statuses);
        return Response.ok(topCategories).build();
    }

    @Path("/category/sub_category")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getSubServiceCategory(@Context HttpServletRequest request,
                                          @QueryParam("category_id") @DefaultValue("0") long parentId
    ) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        List<ServiceCategoryBean> subCategories = serviceCategoryItemService.getCategoryByParentId(parentId, statuses);
        return Response.ok(subCategories).build();
    }

    @Path("/category/item")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getServiceItemByCategoryId(@Context HttpServletRequest request,
                                               @QueryParam("category_id") @DefaultValue("0") long categoryId
    ) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        List<ServiceItemBean> serviceItems = serviceCategoryItemService.getItemByCategoryId(categoryId, statuses);
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
                                 @FormParam("count") @DefaultValue("0") int count,
                                 @FormParam("leave_a_message") @DefaultValue("") String leaveAMessage
                                 ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        ServiceOrderBean order = orderService.addOrder(serviceItemId, userId, patientId, addressId, startTime, count, leaveAMessage);
        return Response.ok(order).build();
    }

    @Path("/get_charge_of_order")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response payForOrder(@Context HttpServletRequest request,
                                @FormParam("order_id") @DefaultValue("0") long orderId,
                                @FormParam("channel") @DefaultValue("") String channel
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        String remoteIP = request.getRemoteAddr();
        Charge charge = orderService.payForService(userId, orderId, channel, remoteIP);
        return Response.ok(charge).build();
    }

    @Path("/pingpp/webhooks")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response pingPpWebhooks(@Context HttpServletRequest request) {
        Enumeration<String> enu = request.getHeaderNames();
        logger.info("header names --- {}", enu);

        enu = request.getAttributeNames();
        logger.info("attribute names --- {}", enu);

        enu = request.getParameterNames();
        logger.info("parameter names --- {}", enu);
        return Response.ok().build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response userEditOrder(@Context HttpServletRequest request,
                                  @FormParam("order_id") @DefaultValue("0") long orderId,
                                  @FormParam("patient_id") @DefaultValue("") String strPatientId,
                                  @FormParam("address_id") @DefaultValue("") String strAddressId,
                                  @FormParam("start_time") @DefaultValue("") String startTime,
                                  @FormParam("count") @DefaultValue("") String strCount,
                                  @FormParam("leave_a_message") @DefaultValue("") String leaveAMessage
    ) {
        Long patientId = !VerifyUtil.isIds(strPatientId) ? null : VerifyUtil.parseLongIds(strPatientId).get(0);
        Long addressId = !VerifyUtil.isIds(strAddressId) ? null : VerifyUtil.parseLongIds(strAddressId).get(0);
        Integer count = !VerifyUtil.isIds(strCount) ? null : VerifyUtil.parseIntIds(strCount).get(0);
        ServiceOrderBean order = orderService.updateOrder(orderId, patientId, addressId, startTime, count, leaveAMessage);
        return Response.ok(order).build();
    }

}
