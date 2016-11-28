package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.NurseOrderRelationService;
import com.cooltoo.go2nurse.service.NurseServiceForGo2Nurse;
import com.cooltoo.go2nurse.service.ServiceOrderService;
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
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
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
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
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
