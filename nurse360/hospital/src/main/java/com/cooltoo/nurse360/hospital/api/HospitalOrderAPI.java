package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.RegisterFrom;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.NurseOrderRelationBean;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.NurseOrderRelationService;
import com.cooltoo.go2nurse.service.NurseServiceForGo2Nurse;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import com.cooltoo.go2nurse.service.notification.NotifierForAllModule;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import com.cooltoo.util.SetUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/22.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalOrderAPI {

    @Autowired private ServiceOrderService orderService;
    @Autowired private NurseOrderRelationService nurseOrderRelation;
    @Autowired private NurseServiceForGo2Nurse nurseService;
    @Autowired private NotifierForAllModule notifierForAllModule;


    //=============================================================
    //            Authentication of ADMINISTRATOR Role
    //=============================================================
    @RequestMapping(path = "/admin/order/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countOrder(HttpServletRequest request,
                           @RequestParam(defaultValue = "",  name = "vendor_type")  String strVendorType, /* hospital, company */
                           @RequestParam(defaultValue = "",  name = "vendor_id")    String strVendorId,
                           @RequestParam(defaultValue = "",  name = "depart_id")    String strDepartId,
                           @RequestParam(defaultValue = "",  name = "order_status") String strOrderStatus /* CANCELLED, TO_PAY, TO_DISPATCH, TO_SERVICE, IN_PROCESS, COMPLETED, CREATE_CHARGE_FAILED*/
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId(strVendorId, strDepartId, userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        long count = orderService.countOrderByConditions(
                null, null, null, null,
                hospitalId,
                ServiceVendorType.parseString(strVendorType),
                departmentId,
                OrderStatus.parseString(strOrderStatus));
        return count;
    }

    @RequestMapping(path = "/admin/order", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ServiceOrderBean> getOrder(HttpServletRequest request,
                                           @RequestParam(defaultValue = "",  name = "vendor_type")  String strVendorType, /* hospital, company */
                                           @RequestParam(defaultValue = "",  name = "vendor_id")    String strVendorId,
                                           @RequestParam(defaultValue = "",  name = "depart_id")    String strDepartId,
                                           @RequestParam(defaultValue = "",  name = "order_status") String strOrderStatus,
                                           @RequestParam(defaultValue = "0",  name = "index") int index,
                                           @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId(strVendorId, strDepartId, userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        List<ServiceOrderBean> orders = orderService.getOrderByConditions(
                null, null, null, null,
                hospitalId,
                ServiceVendorType.parseString(strVendorType),
                departmentId,
                OrderStatus.parseString(strOrderStatus),
                index, number);
        setOrderWaitStaff(orders);
        return orders;
    }


    //=============================================================
    //            Authentication of MANAGER Role
    //=============================================================
    @RequestMapping(path = "/manager/order/cancel", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public ServiceOrderBean cancelOrder(HttpServletRequest request,
                                        @RequestParam(defaultValue = "0", name = "order_id") long orderId
    ) {
        ServiceOrderBean order = orderService.cancelOrder(false, -1, orderId);
        notifierForAllModule.orderAlertToNurse(orderId, order.getOrderStatus(), "order canceled!");
        notifierForAllModule.orderAlertToPatient(order.getUserId(), orderId, order.getOrderStatus(), "order canceled!");
        return order;
    }

    @RequestMapping(path = "/manager/order/in_process", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public ServiceOrderBean orderInProcess(HttpServletRequest request,
                                           @RequestParam(defaultValue = "0", name = "order_id") long orderId
    ) {
        ServiceOrderBean order = orderService.orderInProcess(false, -1, orderId);
        notifierForAllModule.orderAlertToNurse(orderId, order.getOrderStatus(), "order is in_process!");
        notifierForAllModule.orderAlertToPatient(order.getUserId(), orderId, order.getOrderStatus(), "order is in_process!");
        return order;
    }

    @RequestMapping(path = "/manager/order/completed", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public ServiceOrderBean orderCompleted(HttpServletRequest request,
                                           @RequestParam(defaultValue = "0", name = "order_id") long orderId
    ) {
        ServiceOrderBean order = orderService.completedOrder(false, -1, orderId);
        notifierForAllModule.orderAlertToNurse(orderId, order.getOrderStatus(), "order completed!");
        notifierForAllModule.orderAlertToPatient(order.getUserId(), orderId, order.getOrderStatus(), "order completed!");
        return order;
    }

    @RequestMapping(path = "/manager/order/dispatch", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public NurseOrderRelationBean orderDispatchToNurse(HttpServletRequest request,
                                                       @RequestParam(defaultValue = "0", name = "order_id") long orderId,
                                                       @RequestParam(defaultValue = "0", name = "nurse_id") long nurseId
    ) {
        //dispatch or replace order's nurse
        NurseOrderRelationBean nurseOrder = nurseOrderRelation.dispatchToNurse(nurseId, orderId);
        return nurseOrder;
    }

    @RequestMapping(path = "/manager/order/modify", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public ServiceOrderBean modifyTimeAddressMessage(HttpServletRequest request,
                                                     @RequestParam(defaultValue = "0", name = "order_id") long orderId,
                                                     @RequestParam(defaultValue = "0", name = "address")  long addressId,
                                                     @RequestParam(defaultValue = "",  name = "start_time")   String startTime,
                                                     @RequestParam(defaultValue = "",  name = "message_left") String messageLeft
    ) {
        //modify start_time, address, message left, score
        ServiceOrderBean order = orderService.updateOrder(orderId, null, addressId, startTime, null, messageLeft, 0);
        return order;
    }

    @RequestMapping(path = "/manager/order/notify/department", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public void notifyNurseInDepartment(HttpServletRequest request,
                                        @RequestParam(defaultValue = "0", name = "order_id") long orderId,
                                        @RequestParam(defaultValue = "0", name = "hospital_id") int hospitalId,
                                        @RequestParam(defaultValue = "0", name = "department_id") int departmentId,
                                        @RequestParam(defaultValue = "",  name = "register_from") String registerFrom/* cooltoo, go2nurse */
    ) {
        //notify nurse in department to fetch order
        List<ServiceOrderBean> orders = orderService.getOrderByOrderId(orderId);
        if (null!=orders && !orders.isEmpty()) {
            ServiceOrderBean order = orders.get(0);
            List<NurseBean> nurses = nurseService.getNurseByCanAnswerQuestion(null, YesNoEnum.YES.name(), null, hospitalId, departmentId, RegisterFrom.parseString(registerFrom));
            List<Long> nursesId = new ArrayList<>();
            for (NurseBean tmp : nurses) {
                if (!nursesId.contains(tmp.getId())) {
                    nursesId.add(tmp.getId());
                }
            }
            notifierForAllModule.newOrderAlertToNurse(nursesId, order.getId(), order.getOrderStatus(), "new order can fetch");
        }
        return;
    }

    @RequestMapping(path = "/manager/order/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ServiceOrderBean createOrder(HttpServletRequest request,
                                        @RequestParam(defaultValue = "0", name = "service_item_id")   long serviceItemId,
                                        @RequestParam(defaultValue = "0", name = "user_id")           long userId,
                                        @RequestParam(defaultValue = "0", name = "patient_id")        long patientId,
                                        @RequestParam(defaultValue = "0", name = "address_id")        long addressId,
                                        @RequestParam(defaultValue = "0", name = "start_time")      String startTime,
                                        @RequestParam(defaultValue = "0", name = "count")              int count,
                                        @RequestParam(defaultValue = "",  name = "leave_a_message") String leaveAMessage
    ) {
        ServiceOrderBean order = orderService.addOrder(serviceItemId, userId, patientId, addressId, startTime, count, leaveAMessage, 0);
        return order;
    }


    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    @RequestMapping(path = "/order/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countOrder(HttpServletRequest request,
                           @RequestParam(defaultValue = "",  name = "order_status") String strOrderStatus
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        if (userDetails.isNurse()) {
            long count = nurseOrderRelation.countOrderByNurseIdAndOrderStatus(
                    userDetails.getId(),
                    CommonStatus.ENABLED.name(),
                    OrderStatus.parseString(strOrderStatus));
            return count;
        }
        else if (userDetails.isNurseManager()) {
            long count = orderService.countOrderByConditions(
                    null, null, null, null,
                    hospitalId,
                    ServiceVendorType.HOSPITAL,
                    departmentId,
                    OrderStatus.parseString(strOrderStatus));
            return count;
        }
        return 0;
    }

    @RequestMapping(path = "/order", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ServiceOrderBean> getOrder(HttpServletRequest request,
                                           @RequestParam(defaultValue = "",  name = "order_status") String strOrderStatus,
                                           @RequestParam(defaultValue = "0",  name = "index") int index,
                                           @RequestParam(defaultValue = "10", name = "number") int number
    ) {

        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        if (userDetails.isNurse()) {
            List<ServiceOrderBean> orders = nurseOrderRelation.getOrderByNurseIdAndOrderStatus(
                    userDetails.getId(),
                    CommonStatus.ENABLED.name(),
                    OrderStatus.parseString(strOrderStatus),
                    index, number);
            setOrderWaitStaff(orders);
            return orders;
        }
        else if (userDetails.isNurseManager()) {
            List<ServiceOrderBean> orders = orderService.getOrderByConditions(
                    null, null, null, null,
                    hospitalId,
                    ServiceVendorType.HOSPITAL,
                    departmentId,
                    OrderStatus.parseString(strOrderStatus),
                    index, number);
            setOrderWaitStaff(orders);
            return orders;
        }
        return new ArrayList<>();
    }


    //=============================================================
    //            Common Method
    //=============================================================
    private List<Long> getOrderIds(List<ServiceOrderBean> orders) {
        List<Long> orderIds = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(orderIds)) {
            for (ServiceOrderBean tmp : orders) {
                orderIds.add(tmp.getId());
            }
        }
        return orderIds;
    }

    private void setOrderWaitStaff(List<ServiceOrderBean> orders) {
        if (VerifyUtil.isListEmpty(orders)) {
            return;
        }
        Map<Long, Long> orderIdToNurseId = nurseOrderRelation.getOrdersWaitStaffId(getOrderIds(orders));
        Map<Long, NurseBean> nurseIdToBean = nurseService.getNurseIdToBean((List<Long>) SetUtil.newInstance().getMapValueSet(orderIdToNurseId));
        for (ServiceOrderBean tmp : orders) {
            Long nurseId = orderIdToNurseId.get(tmp.getId());
            NurseBean nurse = nurseIdToBean.get(nurseId);
            if (null!=nurse) {
                tmp.setProperty(ServiceOrderBean.WAIT_STAFF, nurse);
            }
        }
    }
}
