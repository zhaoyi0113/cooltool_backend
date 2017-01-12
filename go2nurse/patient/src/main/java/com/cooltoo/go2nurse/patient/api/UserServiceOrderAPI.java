package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.WhoDenyPatient;
import com.cooltoo.go2nurse.service.*;
import com.cooltoo.go2nurse.service.notification.NotifierForAllModule;
import com.cooltoo.util.NetworkUtil;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
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
    @Autowired private NotifierForAllModule notifierForAllModule;
    @Autowired private NurseServiceForGo2Nurse nurseService;
    @Autowired private NurseWalletService nurseWalletService;

    @Autowired private DenyPatientService denyPatientService;

    //=========================================================================================
    //                                    Category Service
    //=========================================================================================
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


    //=========================================================================================
    //                                    Item Service
    //=========================================================================================
    @Path("/category/item")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getServiceItemByCategoryId(@Context HttpServletRequest request,
                                               @QueryParam("category_id") @DefaultValue("0") long categoryId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        List<ServiceItemBean> serviceItems = serviceCategoryItemService.getItemByCategoryId(null, null, null, categoryId, null, YesNoEnum.YES, statuses);

        // filter the vendor deny patient
        List<DenyPatientBean> patientDeniedByVendor = denyPatientService.getWhoDenyPatient(userId, null, WhoDenyPatient.VENDOR);
        List<String> vendorUniqueString = new ArrayList<>();
        for (DenyPatientBean tmp : patientDeniedByVendor) {
            String uniqueString = tmp.getVendorType().name()+"_"+tmp.getVendorId()+"_"+tmp.getDepartId();
            if (!vendorUniqueString.contains(uniqueString)) {
                vendorUniqueString.add(uniqueString);
            }
        }

        // filtered the items
        List<ServiceItemBean> returnItem = new ArrayList<>();
        for (ServiceItemBean tmp : serviceItems) {
            String uniqueString = tmp.getVendorType().name()+"_"+tmp.getVendorId()+"_"+tmp.getVendorDepartId();
            if (vendorUniqueString.contains(uniqueString)) {
                continue;
            }

            returnItem.add(tmp);
        }

        return Response.ok(returnItem).build();
    }

    @Path("/vendor/item")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getServiceItemByVendor(@Context HttpServletRequest request,
                                           @QueryParam("vendor_type") @DefaultValue("0") String strVendorType, /* company, hospital*/
                                           @QueryParam("vendor_id") @DefaultValue("0") long vendorId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        List<ServiceItemBean> serviceItems = serviceCategoryItemService.getItemByCategoryId(vendorType, vendorId, null, null, null, YesNoEnum.YES, statuses);

        // filter the vendor deny patient
        List<DenyPatientBean> patientDeniedByVendor = denyPatientService.getWhoDenyPatient(userId, null, WhoDenyPatient.VENDOR);
        List<String> vendorUniqueString = new ArrayList<>();
        for (DenyPatientBean tmp : patientDeniedByVendor) {
            String uniqueString = tmp.getVendorType().name()+"_"+tmp.getVendorId()+"_"+tmp.getDepartId();
            if (!vendorUniqueString.contains(uniqueString)) {
                vendorUniqueString.add(uniqueString);
            }
        }

        // filtered the items
        List<ServiceItemBean> returnItem = new ArrayList<>();
        for (ServiceItemBean tmp : serviceItems) {
            String uniqueString = tmp.getVendorType().name()+"_"+tmp.getVendorId()+"_"+tmp.getVendorDepartId();
            if (vendorUniqueString.contains(uniqueString)) {
                continue;
            }

            returnItem.add(tmp);
        }

        return Response.ok(returnItem).build();
    }



    //=========================================================================================
    //                                    Doctor Appointment Service
    //=========================================================================================
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserOrder(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<ServiceOrderBean> userOrders = orderService.getOrderByUserId(userId);
        List<DoctorAppointmentBean> doctorAppointments = doctorAppointmentService.getDoctorAppointment(userId, "CANCELLED,WAIT_NURSE_FETCH,COMPLETED");
        List<Object> retVal = sortOrderAndAppointment(userOrders, doctorAppointments);
        return Response.ok(retVal).build();
    }


    //=========================================================================================
    //                                    Order Service
    //=========================================================================================
    @Path("/by_order_id")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserOrderByOrderId(@Context HttpServletRequest request,
                                          @QueryParam("order_id") @DefaultValue("0") long orderId
    ) {
        List<ServiceOrderBean> orders = orderService.getOrderByOrderId(orderId);
        return Response.ok(orders).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response userAddOrder(@Context HttpServletRequest request,
                                 @FormParam("service_item_id") @DefaultValue("0")  long serviceItemId,
                                 @FormParam("patient_id")      @DefaultValue("0")  long patientId,
                                 @FormParam("address")         @DefaultValue("0")String address,
                                 @FormParam("start_time")      @DefaultValue("") String startTime,
                                 @FormParam("count")           @DefaultValue("0")   int count,
                                 @FormParam("leave_a_message") @DefaultValue("") String leaveAMessage
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        ServiceItemBean item = serviceCategoryItemService.getItemById(serviceItemId);
        if (null!=item) {
            boolean isUserDenied = denyPatientService.isVendorDenyPatient(userId, 0, item.getVendorType(), item.getVendorId(), item.getVendorDepartId());
            if (isUserDenied) {
                throw new BadRequestException(ErrorCode.USER_FORBIDDEN_BY_VENDOR);
            }
        }
        ServiceOrderBean order = orderService.addOrder(serviceItemId, userId, patientId, address, startTime, count, leaveAMessage, null);
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
        try { remoteIP = NetworkUtil.newInstance().getIpAddress(request); }
        catch (IOException ex) { remoteIP = "127.0.0.1"; }
        Charge charge = orderService.payForService(userId, orderId, channel, remoteIP);
        logger.debug("pay success with charge "+charge);
        return Response.ok(charge).build();
    }

//@Path("/pingpp/webhooks/test")
//@GET
//@Produces(MediaType.APPLICATION_JSON)
//public Response testSendMsg(@QueryParam("hospitalid") int hospitalId,
//                            @QueryParam("departmentid") int departmentId
//) {
//    List<String> managerMobile = nurseService.getManagerMobiles(hospitalId, departmentId);
//    notifierForAllModule.leanCloudRequestSmsCodeNewOrder(managerMobile, "M2356543676655765");
//    List<Long> managerId = nurseService.getManagerId(hospitalId, departmentId);
//    notifierForAllModule.newOrderToDispatchAlertToNurse360(managerId, 1, OrderStatus.PAID, "new order created!!");
//    return Response.ok().build();
//}

    @Path("/pingpp/webhooks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response pingPpWebhooksProtocolGet(@Context HttpServletRequest request) {
        Object returnMassage = webhook(request);
        if (returnMassage instanceof String) {
            return Response.ok(returnMassage).build();
        }
        else {
            return Response.ok().build();
        }
    }

    @Path("/pingpp/webhooks")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response pingPpWebhooksProtocolPost(@Context HttpServletRequest request) {
        Object returnMassage = webhook(request);
        if (returnMassage instanceof String) {
            return Response.ok(returnMassage).build();
        }
        else {
            return Response.ok().build();
        }
    }

    private Object webhook(HttpServletRequest request) {
        Map<String, Object> returnValue = chargeWebHookService.webHookBody(request);
        if (null==returnValue) {
            return null;
        }

        Object order = returnValue.get(ChargeWebHookService.ORDER);
        Object message = returnValue.get(ChargeWebHookService.MESSAGE);
        if (null==message || ((message instanceof String) && VerifyUtil.isStringEmpty((String)message))) {
            return null;
        }
        else {
            if (null!=order && (order instanceof ServiceOrderBean) && OrderStatus.PAID.equals(((ServiceOrderBean)order).getOrderStatus())) {
                ServiceOrderBean orderBean = (ServiceOrderBean)order;
                // notifierForAllModule.orderAlertToGo2nurseUser(orderBean.getUserId(), orderBean.getId(), orderBean.getOrderStatus(), "waiting for dispatch order!");
                // need send message to Manager
                ServiceVendorType vendorType = orderBean.getVendorType();
                long vendorId = orderBean.getVendorId();
                long departId = orderBean.getVendorDepartId();
                if (ServiceVendorType.HOSPITAL.equals(vendorType)) {
                    List<String> managerMobile = nurseService.getManagerMobiles((int)vendorId, (int)departId);
                    notifierForAllModule.leanCloudRequestSmsCodeNewOrder(managerMobile, orderBean.getOrderNo());
                    List<Long> managerId = nurseService.getManagerId((int)vendorId, (int)departId);
                    notifierForAllModule.newOrderToDispatchAlertToNurse360(managerId, orderBean.getId(), orderBean.getOrderStatus(), "new order created!!");
                }
            }
            return message;
        }
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response userEditOrder(@Context HttpServletRequest request,
                                  @FormParam("order_id")   @DefaultValue("0") long orderId,
                                  @FormParam("patient_id") @DefaultValue("") String strPatientId,
                                  @FormParam("address")    @DefaultValue("") String strAddress,
                                  @FormParam("start_time") @DefaultValue("") String startTime,
                                  @FormParam("count")      @DefaultValue("") String strCount,
                                  @FormParam("leave_a_message") @DefaultValue("") String leaveAMessage
    ) {
        Long patientId = !VerifyUtil.isIds(strPatientId) ? null : VerifyUtil.parseLongIds(strPatientId).get(0);
        Integer count = !VerifyUtil.isIds(strCount) ? null : VerifyUtil.parseIntIds(strCount).get(0);
        ServiceOrderBean order = orderService.updateOrder(orderId, patientId, strAddress, startTime, count, leaveAMessage);
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
        notifierForAllModule.orderAlertToNurse360(orderId, order.getOrderStatus(), "order completed!");
        return Response.ok(order).build();
    }

    @Path("/completed")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response userCompletedOrder(@Context HttpServletRequest request,
                                       @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        ServiceOrderBean order = orderService.completedOrder(true, userId, orderId);
        notifierForAllModule.orderAlertToNurse360(orderId, order.getOrderStatus(), "order completed!");
        nurseWalletService.orderCompleted(orderId);
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
