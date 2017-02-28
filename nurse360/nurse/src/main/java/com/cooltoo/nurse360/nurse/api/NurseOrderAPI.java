package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.NurseAuthorizationJudgeService;
import com.cooltoo.go2nurse.service.notification.NotifierForAllModule;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.go2nurse.service.NurseOrderRelationService;
import com.cooltoo.go2nurse.service.NursePatientRelationService;
import com.cooltoo.nurse360.service.NurseServiceForNurse360;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/10/8.
 */
@Path("/nurse/order")
public class NurseOrderAPI {

    @Autowired private NurseOrderRelationService nurseOrderService;
    @Autowired private NurseServiceForNurse360 nurseService;
    @Autowired private NotifierForAllModule notifierForAllModule;
    @Autowired private NursePatientRelationService nursePatientRelation;
    @Autowired private NurseAuthorizationJudgeService nurseAuthorizationJudgeService;

    /**
     * @param orderStatus CANCELLED,        // 取消订单
     *                    TO_PAY,           // 下单成功，等待支付
     *                    TO_DISPATCH,      // 支付成功，等待管理员提醒抢单或派单
     *                    TO_SERVICE,       // 提醒抢单，等待护士抢单
     *                    IN_PROCESS,       // 抢单成功(或派单成功), 上门服务
     *                    COMPLETED,        // 服务完成
     *                    CREATE_CHARGE_FAILED, //创建订单失败
     *                    REFUND_IN_PROCESS,// 退款处理中
     *                    REFUND_PROCESSED, // 退款管理员已处理
     *                    REFUND_COMPLETED, // 退款完成
     *                    REFUND_FAILED     // 退款失败
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getOrder(@Context HttpServletRequest request,
                             @QueryParam("order_status") @DefaultValue("") String orderStatus,
                             @QueryParam("index") @DefaultValue("0") int pageIndex,
                             @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<ServiceOrderBean> orders = nurseOrderService.getAllOrder(nurseId, CommonStatus.ENABLED.name(), orderStatus, pageIndex, sizePerPage);
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
        if (orderId>0) {
            nurseAuthorizationJudgeService.canNurseFetchOrder(nurseId, orderId);
        }
        Map<String, Long> orderRelativeIds = nurseOrderService.fetchOrder(nurseId, orderId, false);

        // add patient to nurse_patient_relation table
        Long userId = orderRelativeIds.get("user_id");
        Long patientId = orderRelativeIds.get("patient_id");
        if (null!=userId) {
            patientId = null==patientId ? 0 : patientId;
            nursePatientRelation.addUserPatientToNurse(nurseId, patientId, userId);
        }
        Map<String, Long> ret = new HashMap<>();
        ret.put("order_id", orderId);
        return Response.ok(ret).build();
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
        // need to send message to Manager
        List<ServiceOrderBean> orders = nurseOrderService.getOrderByOrderId(nurseId, orderId);
        for (ServiceOrderBean tmp : orders) {
            if (null==tmp) { continue; }

            ServiceVendorType vendorType = tmp.getVendorType();
            long vendorId = tmp.getVendorId();
            long departId = tmp.getVendorDepartId();

            if (ServiceVendorType.HOSPITAL.equals(vendorType)) {
                List<String> managerMobile = nurseService.getManagerMobiles((int)vendorId, (int)departId);
                notifierForAllModule.leanCloudRequestSmsCodeRedispatch(managerMobile, tmp.getOrderNo());
            }
        }

        return Response.ok().build();
    }

//    @Path("/completed")
//    @PUT
//    @Produces(MediaType.APPLICATION_JSON)
//    @Nurse360LoginAuthentication(requireNurseLogin = true)
//    public Response orderCompleted(@Context HttpServletRequest request,
//                                   @FormParam("order_id") @DefaultValue("0") long orderId
//    ) {
//        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
//        orderId = nurseOrderService.completedOrder(nurseId, orderId);
//        return Response.ok(orderId).build();
//    }
}
