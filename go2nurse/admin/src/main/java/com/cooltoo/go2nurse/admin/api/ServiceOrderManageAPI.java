package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.RegisterFrom;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.go2nurse.beans.NurseOrderRelationBean;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.NurseOrderRelationService;
import com.cooltoo.go2nurse.service.NurseServiceForGo2Nurse;
import com.cooltoo.go2nurse.service.notification.NotifierForAllModule;
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
import java.util.List;

/**
 * Created by hp on 2016/7/17.
 */
@Path("/admin/service_order")
public class ServiceOrderManageAPI {
    private static final Logger logger = LoggerFactory.getLogger(ServiceOrderManageAPI.class);

    @Autowired private ServiceOrderService orderService;
    @Autowired private NurseServiceForGo2Nurse nurseService;
    @Autowired private NurseOrderRelationService nurseOrderRelation;
    @Autowired private NotifierForAllModule notifierForAllModule;

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
                                           @QueryParam("vendor_depart_id") @DefaultValue("") String strVendorDepartId,
                                           @QueryParam("order_status") @DefaultValue("") String strOrderStatus
    ) {
        Long itemId = !VerifyUtil.isIds(strItemId) ? null : VerifyUtil.parseLongIds(strItemId).get(0);
        Long userId = !VerifyUtil.isIds(strUserId) ? null : VerifyUtil.parseLongIds(strUserId).get(0);
        Long categoryId = !VerifyUtil.isIds(strCategoryId) ? null : VerifyUtil.parseLongIds(strCategoryId).get(0);
        Long topCategoryId = !VerifyUtil.isIds(strTopCategoryId) ? null : VerifyUtil.parseLongIds(strTopCategoryId).get(0);

        Long vendorId = !VerifyUtil.isIds(strVendorId) ? null : VerifyUtil.parseLongIds(strVendorId).get(0);
        Long vendorDepartId = !VerifyUtil.isIds(strVendorDepartId) ? null : VerifyUtil.parseLongIds(strVendorDepartId).get(0);
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        if (vendorId==null || vendorType==null) {
            vendorId = null;
            vendorType = null;
        }

        OrderStatus orderStatus = OrderStatus.parseString(strOrderStatus);
        long count = orderService.countOrderByConditions(itemId, userId, categoryId, topCategoryId, vendorId, vendorType, vendorDepartId, orderStatus);
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
                                         @QueryParam("vendor_depart_id") @DefaultValue("") String strVendorDepartId,
                                         @QueryParam("order_status") @DefaultValue("") String strOrderStatus,
                                         @QueryParam("index") @DefaultValue("0") int pageIndex,
                                         @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        Long itemId = !VerifyUtil.isIds(strItemId) ? null : VerifyUtil.parseLongIds(strItemId).get(0);
        Long userId = !VerifyUtil.isIds(strUserId) ? null : VerifyUtil.parseLongIds(strUserId).get(0);
        Long categoryId = !VerifyUtil.isIds(strCategoryId) ? null : VerifyUtil.parseLongIds(strCategoryId).get(0);
        Long topCategoryId = !VerifyUtil.isIds(strTopCategoryId) ? null : VerifyUtil.parseLongIds(strTopCategoryId).get(0);

        Long vendorId = !VerifyUtil.isIds(strVendorId) ? null : VerifyUtil.parseLongIds(strVendorId).get(0);
        Long vendorDepartId = !VerifyUtil.isIds(strVendorDepartId) ? null : VerifyUtil.parseLongIds(strVendorDepartId).get(0);
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        if (vendorId==null || vendorType==null) {
            vendorId = null;
            vendorType = null;
        }

        OrderStatus orderStatus = OrderStatus.parseString(strOrderStatus);
        List<ServiceOrderBean> orders = orderService.getOrderByConditions(itemId, userId, categoryId, topCategoryId, vendorId, vendorType, vendorDepartId, orderStatus, pageIndex, sizePerPage);
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
        notifierForAllModule.orderAlertToNurse360(orderId, order.getOrderStatus(), "order canceled!");
        notifierForAllModule.orderAlertToGo2nurseUser(order.getUserId(), orderId, order.getOrderStatus(), "order canceled!");
        return Response.ok(order).build();
    }

    @Path("/completed")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderCompleted(@Context HttpServletRequest request,
                                   @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        ServiceOrderBean order = orderService.completedOrder(false, -1, orderId);
        notifierForAllModule.orderAlertToNurse360(orderId, order.getOrderStatus(), "order completed!");
        notifierForAllModule.orderAlertToGo2nurseUser(order.getUserId(), orderId, order.getOrderStatus(), "order completed!");
        return Response.ok(order).build();
    }

    @Path("/dispatch/nurse")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderDispatchToNurse(@Context HttpServletRequest request,
                                         @FormParam("order_id") @DefaultValue("0") long orderId,
                                         @FormParam("nurse_id") @DefaultValue("0") long nurseId
    ) {
        //dispatch or replace order's nurse
        NurseOrderRelationBean nurseOrder = nurseOrderRelation.dispatchToNurse(nurseId, orderId);
        return Response.ok(nurseOrder).build();
    }

    @Path("/modify")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyTimeAddressMessage(@Context HttpServletRequest request,
                                             @FormParam("order_id") @DefaultValue("0") long orderId,
                                             @FormParam("address") @DefaultValue("0") long addressId,
                                             @FormParam("start_time") @DefaultValue("") String startTime,
                                             @FormParam("message_left") @DefaultValue("") String messageLeft
    ) {
        //modify start_time, address, message left, score
        ServiceOrderBean order = orderService.updateOrder(orderId, null, addressId, startTime, null, messageLeft);
        return Response.ok(order).build();
    }

    @Path("/notify/department")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response notifyNurseInDepartment(@Context HttpServletRequest request,
                                            @FormParam("order_id") @DefaultValue("0") long orderId,
                                            @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                            @FormParam("department_id") @DefaultValue("0") int departmentId,
                                            @FormParam("register_from") @DefaultValue("") String registerFrom/* cooltoo, go2nurse */
    ) {
        //change order status to TO_SERVICE (提醒抢单，等待护士抢单)
        orderService.alertNurseToFetchOrder(orderId);
        //notify nurse in department to fetch order
        List<ServiceOrderBean> orders = orderService.getOrderByOrderId(orderId);
        if (null!=orders && !orders.isEmpty()) {
            ServiceOrderBean order = orders.get(0);
            if (OrderStatus.TO_SERVICE.equals(order.getOrderStatus())) {
                List<NurseBean> nurses = nurseService.getNurseByCanAnswerQuestion(null, YesNoEnum.YES.name(), null, hospitalId, departmentId, RegisterFrom.parseString(registerFrom));
                List<Long> nursesId = getNurseIds(nurses);
                notifierForAllModule.newOrderAlertToNurse360(nursesId, order.getId(), order.getOrderStatus(), "new order can fetch");
            }
            else {
                logger.error("order status is {}, can not to fetch by nurse", order.getOrderStatus());
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder(@Context HttpServletRequest request,
                                @FormParam("service_item_id") @DefaultValue("0") long serviceItemId,
                                @FormParam("user_id")    @DefaultValue("0") long userId,
                                @FormParam("patient_id") @DefaultValue("0") long patientId,
                                @FormParam("address_id") @DefaultValue("0") long addressId,
                                @FormParam("start_time") @DefaultValue("") String startTime,
                                @FormParam("count") @DefaultValue("0") int count,
                                @FormParam("leave_a_message") @DefaultValue("") String leaveAMessage
    ) {
        ServiceOrderBean order = orderService.addOrder(serviceItemId, userId, patientId, addressId, startTime, count, leaveAMessage);
        return Response.ok(order).build();
    }

    private List<Long> getNurseIds(List<NurseBean> nurses) {
        List<Long> nurseId = new ArrayList<>();
        for (NurseBean tmp : nurses) {
            if (!nurseId.contains(tmp.getId())) {
                nurseId.add(tmp.getId());
            }
        }
        return nurseId;
    }
}
