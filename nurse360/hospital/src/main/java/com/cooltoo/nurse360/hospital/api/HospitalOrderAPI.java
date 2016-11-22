package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.NurseOrderRelationService;
import com.cooltoo.go2nurse.service.NurseServiceForGo2Nurse;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.hospital.service.HospitalAdminService;
import com.cooltoo.util.SetUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired private HospitalAdminService adminService;
    @Autowired private ServiceOrderService orderService;
    @Autowired private NurseOrderRelationService nurseOrderRelation;
    @Autowired private NurseServiceForGo2Nurse nurseService;


    //=============================================================
    //            Authentication of MANAGER Role
    //=============================================================
    @RequestMapping(path = "/manager/order/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countOrder(HttpServletRequest request,
                           @RequestParam(defaultValue = "",  name = "order_status") String strOrderStatus /* CANCELLED, TO_PAY, TO_DISPATCH, TO_SERVICE, IN_PROCESS, COMPLETED, CREATE_CHARGE_FAILED*/
    ) {
        long adminId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        HospitalAdminBean admin = adminService.getAdminUser(adminId);
        long count = orderService.countOrderByConditions(
                null, null, null, null,
                (long)admin.getHospitalId(),
                ServiceVendorType.HOSPITAL,
                (long)admin.getDepartmentId(),
                OrderStatus.parseString(strOrderStatus));
        return count;
    }

    @RequestMapping(path = "/manager/order", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ServiceOrderBean> getOrder(HttpServletRequest request,
                                           @RequestParam(defaultValue = "",  name = "order_status") String strOrderStatus,
                                           @RequestParam(defaultValue = "0",  name = "index") int index,
                                           @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        long adminId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        HospitalAdminBean admin = adminService.getAdminUser(adminId);
        List<ServiceOrderBean> orders = orderService.getOrderByConditions(
                null, null, null, null,
                (long)admin.getHospitalId(),
                ServiceVendorType.HOSPITAL,
                (long)admin.getDepartmentId(),
                OrderStatus.parseString(strOrderStatus),
                index, number);
        setOrderWaitStaff(orders);
        return orders;
    }


    //=============================================================
    //            Authentication of NURSE Role
    //=============================================================
    @RequestMapping(path = "/order/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countOrder(HttpServletRequest request,
                           @RequestParam(defaultValue = "0", name = "nurse_id") long nurseId,
                           @RequestParam(defaultValue = "",  name = "order_status") String strOrderStatus
    ) {
        long count = nurseOrderRelation.countOrderByNurseIdAndOrderStatus(
                nurseId,
                CommonStatus.ENABLED.name(),
                OrderStatus.parseString(strOrderStatus));
        return count;
    }

    @RequestMapping(path = "/order", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ServiceOrderBean> getOrder(HttpServletRequest request,
                                           @RequestParam(defaultValue = "0", name = "nurse_id") long nurseId,
                                           @RequestParam(defaultValue = "",  name = "order_status") String strOrderStatus,
                                           @RequestParam(defaultValue = "0",  name = "index") int index,
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
