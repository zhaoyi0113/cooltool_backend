package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.go2nurse.service.NurseOrderRelationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/10/8.
 */
@Path("/nurse/order")
public class NurseOrderAPI {

    @Autowired private NurseOrderRelationService nurseOrderService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getOrder(@Context HttpServletRequest request,
                             @QueryParam("index") @DefaultValue("0") int pageIndex,
                             @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<ServiceOrderBean> orders = nurseOrderService.getAllOrder(nurseId, CommonStatus.ENABLED.name(), pageIndex, sizePerPage);
        return Response.ok(orders).build();
    }

    @Path("/self")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getSelfOrder(@Context HttpServletRequest request,
                                 @QueryParam("index") @DefaultValue("0") int pageIndex,
                                 @QueryParam("number") @DefaultValue("10") int sizePerPage) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<ServiceOrderBean> orders = nurseOrderService.getOrderByNurseIdAndOrderStatus(nurseId, CommonStatus.ENABLED.name(), null, pageIndex, sizePerPage);
        return Response.ok(orders).build();
    }

    @Path("/order_no")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getOrderByNo(@Context HttpServletRequest request,
                                 @QueryParam("order_no") @DefaultValue("0") String orderNo
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<ServiceOrderBean> orders = nurseOrderService.getOrderByOrderNo(nurseId, orderNo);
        return Response.ok(orders).build();
    }

    @Path("/{order_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getOrder(@Context HttpServletRequest request,
                             @PathParam("order_id") @DefaultValue("0") long orderId
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<ServiceOrderBean> orders = nurseOrderService.getOrderByOrderId(nurseId, orderId);
        return Response.ok(orders).build();
    }

    @Path("/fetch")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response grabOrder(@Context HttpServletRequest request,
                              @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        nurseOrderService.fetchOrder(nurseId, orderId);
        return Response.ok().build();
    }

    @Path("/cancel")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response cancelOrder(@Context HttpServletRequest request,
                                @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        nurseOrderService.giveUpOrder(nurseId, orderId);
        return Response.ok().build();
    }

    @Path("/completed")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response orderCompleted(@Context HttpServletRequest request,
                                   @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        orderId = nurseOrderService.completedOrder(nurseId, orderId);
        return Response.ok(orderId).build();
    }
}
