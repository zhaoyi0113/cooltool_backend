package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.RegisterFrom;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NurseOrderRelationBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.*;
import com.cooltoo.go2nurse.service.notification.NotifierForAllModule;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import com.cooltoo.util.SetUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/22.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalOrderAPI {

    @Autowired private ServiceVendorCategoryAndItemService serviceVendorCategoryAndItemService;
    @Autowired private ServiceOrderService orderService;
    @Autowired private NurseOrderRelationService nurseOrderRelation;
    @Autowired private NurseServiceForGo2Nurse nurseService;
    @Autowired private NotifierForAllModule notifierForAllModule;
    @Autowired private NurseWalletService nurseWalletService;
    @Autowired private DenyPatientService denyPatientService;


    //=============================================================
    //            Authentication of ADMINISTRATOR Role
    //=============================================================
    @RequestMapping(path = "/admin/order/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countOrder(HttpServletRequest request,
                           @RequestParam(defaultValue = "",  name = "vendor_type")  String strVendorType, /* hospital, company */
                           @RequestParam(defaultValue = "",  name = "vendor_id")    String strVendorId,
                           @RequestParam(defaultValue = "",  name = "depart_id")    String strDepartId,
                           @RequestParam(defaultValue = "",  name = "order_status") String strOrderStatus /* CANCELLED, TO_PAY, PAID, WAIT_NURSE_FETCH, IN_PROCESS, COMPLETED, CREATE_CHARGE_FAILED*/
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId(strVendorId, strDepartId, userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        long count = orderService.countOrderByConditions(
                null, null, null, null, null,
                ServiceVendorType.parseString(strVendorType),
                hospitalId,
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
                null, null, null, null, null,
                ServiceVendorType.parseString(strVendorType),
                hospitalId,
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
        notifierForAllModule.orderAlertToNurse360(orderId, order.getOrderStatus(), "order canceled!");
        notifierForAllModule.orderAlertToGo2nurseUser(order.getUserId(), orderId, order.getOrderStatus(), "order canceled!");
        return order;
    }

    @RequestMapping(path = "/manager/order/completed", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public ServiceOrderBean orderCompleted(HttpServletRequest request,
                                           @RequestParam(defaultValue = "0", name = "order_id") long orderId
    ) {
        ServiceOrderBean order = orderService.completedOrder(false, -1, orderId);
        notifierForAllModule.orderAlertToNurse360(orderId, order.getOrderStatus(), "order completed!");
        notifierForAllModule.orderAlertToGo2nurseUser(order.getUserId(), orderId, order.getOrderStatus(), "order completed!");
        nurseWalletService.orderCompleted(orderId);
        return order;
    }

    @RequestMapping(path = "/manager/order/dispatch", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public NurseOrderRelationBean orderDispatchToNurse(HttpServletRequest request,
                                                       @RequestParam(defaultValue = "0", name = "order_id") long orderId,
                                                       @RequestParam(defaultValue = "0", name = "nurse_id") long nurseId
    ) {
        List<ServiceOrderBean> order = orderService.getOrderByOrderId(orderId);
        if (!VerifyUtil.isListEmpty(order) && denyPatientService.isNurseDenyPatient(order.get(0).getUserId(), 0, nurseId)) {
            throw new BadRequestException(ErrorCode.USER_FORBIDDEN_BY_VENDOR);
        }
        //dispatch or replace order's nurse
        NurseOrderRelationBean nurseOrder = nurseOrderRelation.dispatchToNurse(nurseId, orderId, true);
        return nurseOrder;
    }

    @RequestMapping(path = "/manager/order/modify", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public ServiceOrderBean modifyTimeAddressMessage(HttpServletRequest request,
                                                     @RequestParam(defaultValue = "0", name = "order_id")       long orderId,
                                                     @RequestParam(defaultValue = "0", name = "address")      String address,
                                                     @RequestParam(defaultValue = "",  name = "count")        String strCount,
                                                     @RequestParam(defaultValue = "",  name = "start_time")   String startTime,
                                                     @RequestParam(defaultValue = "",  name = "message_left") String messageLeft
    ) {
        //modify start_time, address, message left, score
        Integer count = !VerifyUtil.isIds(strCount) ? null : VerifyUtil.parseIntIds(strCount).get(0);
        ServiceOrderBean order = orderService.updateOrder(orderId, null, address, startTime, count, messageLeft);
        return order;
    }

    @RequestMapping(path = "/manager/order/notify/department", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public void notifyNurseInDepartment(HttpServletRequest request,
                                        @RequestParam(defaultValue = "0", name = "order_id") long orderId,
                                        @RequestParam(defaultValue = "0", name = "hospital_id") int hospitalId,
                                        @RequestParam(defaultValue = "0", name = "department_id") int departmentId,
                                        @RequestParam(defaultValue = "",  name = "register_from") String registerFrom/* cooltoo, go2nurse */
    ) {
        //change order status to WAIT_NURSE_FETCH (提醒抢单，等待护士抢单)
        orderService.alertNurseToFetchOrder(orderId);
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
            notifierForAllModule.newOrderAlertToNurse360(nursesId, order.getId(), order.getOrderStatus(), "new order can fetch");
        }
        return;
    }

    @RequestMapping(path = "/manager/order/service/item/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countServiceItem(HttpServletRequest request) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        long orderCount = serviceVendorCategoryAndItemService.countItemByCategoryId(
                ServiceVendorType.HOSPITAL, hospitalId, departmentId,
                null, null, YesNoEnum.YES,
                Arrays.asList(new CommonStatus[]{CommonStatus.ENABLED}));
        return orderCount;
    }

    @RequestMapping(path = "/manager/order/service/item", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ServiceItemBean> getServiceItem(HttpServletRequest request,
                                                @RequestParam("index")  int pageIndex,
                                                @RequestParam("number") int sizePerPage
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        List<ServiceItemBean> items = serviceVendorCategoryAndItemService.getItemByCategoryId(
                ServiceVendorType.HOSPITAL, hospitalId, departmentId,
                null, null, YesNoEnum.YES,
                Arrays.asList(new CommonStatus[]{CommonStatus.ENABLED}),
                pageIndex, sizePerPage);
        return items;
    }

    @RequestMapping(path = "/manager/order/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ServiceOrderBean createOrder(HttpServletRequest request,
                                        @RequestParam(defaultValue = "0", name = "service_item_id")   long serviceItemId,
                                        @RequestParam(defaultValue = "0", name = "user_id")           long userId,
                                        @RequestParam(defaultValue = "0", name = "patient_id")        long patientId,
                                        @RequestParam(defaultValue = "0", name = "address")         String address,
                                        @RequestParam(defaultValue = "0", name = "start_time")      String startTime,
                                        @RequestParam(defaultValue = "0", name = "count")              int count,
                                        @RequestParam(defaultValue = "",  name = "leave_a_message") String leaveAMessage
    ) {
        ServiceOrderBean order = orderService.addOrder(serviceItemId, userId, patientId, address, startTime, count, leaveAMessage);
        return order;
    }


    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    @RequestMapping(path = "/order/{order_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public ServiceOrderBean countUserOrder(HttpServletRequest request,
                                                 @PathVariable long order_id
    ) {
        List<ServiceOrderBean> orders = orderService.getOrderByOrderId(order_id);
        return orders.get(0);
    }


    @RequestMapping(path = "/order/user/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countUserOrder(HttpServletRequest request,
                               @RequestParam(defaultValue = "0",name = "user_id")       long userId,
                               @RequestParam(defaultValue = "", name = "patient_id")  String strPatientId,
                               @RequestParam(defaultValue = "", name = "order_status")String strOrderStatus
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        Long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : null;
        long count = orderService.countOrderByConditions(
                null,
                userId, patientId,
                null, null,
                ServiceVendorType.HOSPITAL, hospitalId, departmentId,
                OrderStatus.parseString(strOrderStatus));
        return count;
    }

    @RequestMapping(path = "/order/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ServiceOrderBean> getUserOrder(HttpServletRequest request,
                                               @RequestParam(defaultValue = "0",name = "user_id")        long userId,
                                               @RequestParam(defaultValue = "", name = "patient_id")   String strPatientId,
                                               @RequestParam(defaultValue = "", name = "order_status")String strOrderStatus,
                                               @RequestParam(defaultValue = "0",  name = "index")  int index,
                                               @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        Long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : null;
        List<ServiceOrderBean> orders = orderService.getOrderByConditions(
                null,
                userId, patientId,
                null, null,
                ServiceVendorType.HOSPITAL, hospitalId, departmentId,
                OrderStatus.parseString(strOrderStatus),
                index, number);
        setOrderWaitStaff(orders);
        return orders;
    }

    @RequestMapping(path = "/order/nurse/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countNurseOrder(HttpServletRequest request,
                                @RequestParam(defaultValue = "0", name = "nurse_id")        long nurseId,
                                @RequestParam(defaultValue = "",  name = "order_status") String strOrderStatus
    ) {
        long count = nurseOrderRelation.countOrderByNurseIdAndOrderStatus(
                nurseId,
                CommonStatus.ENABLED.name(),
                OrderStatus.parseString(strOrderStatus));
        return count;
    }

    @RequestMapping(path = "/order/nurse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ServiceOrderBean> getNurseOrder(HttpServletRequest request,
                                                @RequestParam(defaultValue = "0",  name = "nurse_id")       long nurseId,
                                                @RequestParam(defaultValue = "",   name = "order_status") String strOrderStatus,
                                                @RequestParam(defaultValue = "0",  name = "index")  int index,
                                                @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        List<ServiceOrderBean> orders = nurseOrderRelation.getOrderByNurseIdAndOrderStatus(
                nurseId,
                CommonStatus.ENABLED.name(),
                OrderStatus.parseString(strOrderStatus),
                index, number);
        setOrderWaitStaff(orders);
        return orders;
    }

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
                    null, null, null, null, null,
                    ServiceVendorType.HOSPITAL,
                    hospitalId,
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
                    null, null, null, null, null,
                    ServiceVendorType.HOSPITAL,
                    hospitalId,
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
        if (!VerifyUtil.isListEmpty(orders)) {
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
            tmp.setProperty(ServiceOrderBean.WAIT_STAFF, nurse);
        }
    }
}
