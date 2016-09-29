package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.go2nurse.beans.DoctorAppointmentBean;
import com.cooltoo.go2nurse.service.ChargeWebHookService;
import com.cooltoo.go2nurse.service.DoctorAppointmentService;
import com.cooltoo.util.NetworkUtil;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.ServiceCategoryBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import com.cooltoo.go2nurse.service.ServiceVendorCategoryAndItemService;
import com.cooltoo.util.VerifyUtil;
import com.pingplusplus.model.Charge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

import com.google.common.io.CharStreams;
import com.pingplusplus.model.Event;
import javax.servlet.ServletInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by hp on 2016/7/15.
 */
@Path("/user/order")
public class UserServiceOrderAPI {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceOrderAPI.class);

    @Autowired private DoctorAppointmentService doctorAppointmentService;
    @Autowired private ServiceOrderService orderService;
    @Autowired private ServiceVendorCategoryAndItemService serviceCategoryItemService;
    @Autowired private ChargeWebHookService chargeWebHookService;

    @Path("/category/top")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getServiceCategory(@Context HttpServletRequest request) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        List<ServiceCategoryBean> topCategories = serviceCategoryItemService.getCategoryByParentId(0L, statuses);
        return Response.ok(topCategories).build();
    }

    @Path("/category/sub_category")
    @GET
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
    @GET
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

    @Path("/vendor/item")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getServiceItemByVendor(@Context HttpServletRequest request,
                                           @QueryParam("vendor_id") @DefaultValue("0") int vendorId,
                                           @QueryParam("vendor_type") @DefaultValue("0") String vendorType /* company, hospital*/
    ) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        List<ServiceItemBean> serviceItems = serviceCategoryItemService.getItemByVendorId(vendorId, vendorType, statuses);
        return Response.ok(serviceItems).build();
    }

    @Path("/by_order_id")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserOrderByOrderId(@Context HttpServletRequest request,
                                          @QueryParam("order_id") @DefaultValue("0") long orderId
    ) {
        List<ServiceOrderBean> orders = orderService.getOrderByOrderId(orderId);
        return Response.ok(orders.get(0)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserOrder(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<ServiceOrderBean> userOrders = orderService.getOrderByUserId(userId);
        List<DoctorAppointmentBean> doctorAppointments = doctorAppointmentService.getDoctorAppointment(userId, "CANCELLED,TO_SERVICE,COMPLETED");
        List<Object> retVal = sortOrderAndAppointment(userOrders, doctorAppointments);
        return Response.ok(retVal).build();
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
        ServiceOrderBean order = orderService.addOrder(serviceItemId, userId, patientId, addressId, startTime, count, leaveAMessage, 0);
        return Response.ok(order).build();
    }

    @Path("/get_charge_of_order")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response payForOrder(@Context HttpServletRequest request,
                                @FormParam("order_id") @DefaultValue("0") long orderId,
                                @FormParam("channel") @DefaultValue("") String channel
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        String remoteIP;
        try { remoteIP = NetworkUtil.getIpAddress(request); }
        catch (IOException ex) { remoteIP = "127.0.0.1"; }
        Charge charge = orderService.payForService(userId, orderId, channel, remoteIP);
        logger.debug("pay success with charge "+charge);
        return Response.ok(charge).build();
    }

    @Path("/pingpp/webhooks")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response pingPpWebhooks(@Context HttpServletRequest request) {
//        logger.info("receive web hooks");
//        try {
//            ServletInputStream inputStream = request.getInputStream();
//            Reader reader = new InputStreamReader(inputStream);
//            String body = CharStreams.toString(reader);
//            logger.info("receive body "+body);
//            if(body != null) {
//                Event event = Event.GSON.fromJson(body, Event.class);
//                Charge charge = (Charge) event.getData().getObject();
//                orderService.orderChargeWebhooks(charge.getId(), event.getId(), body);
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage(), e);
//        }
//        return Response.ok().build();


        String returnValue = (String)chargeWebHookService.webHookBody(request);
        if (VerifyUtil.isStringEmpty(returnValue)) {
            return Response.ok().build();
        }
        else {
            return Response.ok(returnValue).build();
        }
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
        ServiceOrderBean order = orderService.updateOrder(orderId, patientId, addressId, startTime, count, leaveAMessage, 0);
        return Response.ok(order).build();
    }

    @Path("/cancel")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response userCancelOrder(@Context HttpServletRequest request,
                                  @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        ServiceOrderBean order = orderService.cancelOrder(true, userId, orderId);
        return Response.ok(order).build();
    }

    @Path("/score")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response userScoreOrder(@Context HttpServletRequest request,
                                   @FormParam("order_id") @DefaultValue("0") long orderId,
                                   @FormParam("score") @DefaultValue("0") float score
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        ServiceOrderBean order = orderService.scoreOrder(true, userId, orderId, score);
        return Response.ok(order).build();
    }

    private List<Object> sortOrderAndAppointment(List<ServiceOrderBean> orders, List<DoctorAppointmentBean> appointments) {
        List<Object> retVal = new ArrayList<>();
        for (ServiceOrderBean order : orders) {
            retVal.add(order);
        }
        for (DoctorAppointmentBean appointment : appointments) {
            retVal.add(appointment);
        }
        Collections.sort(retVal, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                if (null==o1 && null==o2) {
                    return 0;
                }
                if (null==o1) {
                    return 1;
                }
                if (null==o2) {
                    return -1;
                }
                Date date1 = (o1 instanceof ServiceOrderBean) ? ((ServiceOrderBean)o1).getTime() : ((DoctorAppointmentBean)o1).getTime();
                Date date2 = (o2 instanceof ServiceOrderBean) ? ((ServiceOrderBean)o2).getTime() : ((DoctorAppointmentBean)o2).getTime();
                long delta = (date2.getTime()-date1.getTime());
                return delta==0 ? 0 : (delta>0 ? 1 : -1);
            }
        });
        return retVal;
    }
}
