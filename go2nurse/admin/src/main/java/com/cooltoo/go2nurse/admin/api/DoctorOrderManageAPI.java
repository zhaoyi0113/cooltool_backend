package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.go2nurse.beans.DoctorBean;
import com.cooltoo.go2nurse.beans.DoctorOrderBean;
import com.cooltoo.go2nurse.service.DoctorOrderService;
import com.cooltoo.go2nurse.service.DoctorService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 2016/10/14.
 */
@Path("/admin/doctor/order")
public class DoctorOrderManageAPI {

    @Autowired private DoctorService doctorService;
    @Autowired private DoctorOrderService doctorOrderService;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private Go2NurseUtility utility;


    //==============================================================
    //                         getting
    //==============================================================
    @Path("/{order_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderById(@Context HttpServletRequest request,
                                 @PathParam("order_id") @DefaultValue("0") long orderId
    ) {
        DoctorOrderBean order = doctorOrderService.getOrder(orderId);
        fillHospitalDepartment(order);
        return Response.ok(order).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countOrderByHospitalDepartment(@Context HttpServletRequest request,
                                                   @QueryParam("hospital_id") String strHospitalId,
                                                   @QueryParam("department_id") String strDepartmentId
    ) {
        Integer hospitalId = VerifyUtil.isIds(strHospitalId) ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        long count = doctorOrderService.countDoctorOrder(null==departmentId||0==departmentId, hospitalId, departmentId);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderByHospitalDepartment(@Context HttpServletRequest request,
                                                 @QueryParam("hospital_id") String strHospitalId,
                                                 @QueryParam("department_id") String strDepartmentId,
                                                 @QueryParam("index") @DefaultValue("0") int pageIndex,
                                                 @QueryParam("number") @DefaultValue("0") int sizePerPage
    ) {
        Integer hospitalId = VerifyUtil.isIds(strHospitalId) ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        List<DoctorOrderBean> orders = doctorOrderService.getDoctorOrder(null==departmentId||0==departmentId, hospitalId, departmentId, pageIndex, sizePerPage);
        return Response.ok(orders).build();
    }


    //==============================================================
    //                         deleting
    //==============================================================
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOrder(@Context HttpServletRequest request,
                                @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        doctorOrderService.deleteOrder(orderId);
        return Response.ok().build();
    }


    //==============================================================
    //                         updating
    //==============================================================
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response addServiceVendor(@Context HttpServletRequest request,
                                     @FormParam("order_id") @DefaultValue("0") long orderId,
                                     @FormParam("doctor_id") @DefaultValue("0") long doctorId,
                                     @FormParam("doctor_order") @DefaultValue("0") int doctorOrder,
                                     @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                     @FormParam("hospital_order") @DefaultValue("0") int hospitalOrder,
                                     @FormParam("department_id") @DefaultValue("0") int departmentId,
                                     @FormParam("department_order") @DefaultValue("-1") int departmentOrder
    ) {
        DoctorOrderBean order = doctorOrderService.setDoctorOrder(
                orderId,
                doctorId, doctorOrder,
                hospitalId, hospitalOrder,
                departmentId, departmentOrder);
        fillHospitalDepartment(order);
        return Response.ok(order).build();
    }


    //==============================================================
    //                         adding
    //==============================================================
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addServiceVendor(@Context HttpServletRequest request,
                                     @FormParam("doctor_id") @DefaultValue("0") long doctorId,
                                     @FormParam("doctor_order") @DefaultValue("0") int doctorOrder,
                                     @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                     @FormParam("hospital_order") @DefaultValue("0") int hospitalOrder,
                                     @FormParam("department_id") @DefaultValue("0") int departmentId,
                                     @FormParam("department_order") @DefaultValue("-1") int departmentOrder
    ) {
        DoctorOrderBean order = doctorOrderService.setDoctorOrder(
                -1,
                doctorId, doctorOrder,
                hospitalId, hospitalOrder,
                departmentId, departmentOrder);
        fillHospitalDepartment(order);
        return Response.ok(order).build();
    }

    //==============================================================
    //                         editing
    //==============================================================
    @Path("/change/doctor_order")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeDoctorOrder(@Context HttpServletRequest request,
                                      @FormParam("first_order_id") @DefaultValue("0") long firstOrderId,
                                      @FormParam("second_order_id") @DefaultValue("0") long secondOrderId
    ) {
        doctorOrderService.changeTwoDoctorOrderInDoctor(firstOrderId, secondOrderId);
        return Response.ok().build();
    }

    @Path("/change/hospital_order")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeHospitalOrder(@Context HttpServletRequest request,
                                        @FormParam("first_order_id") @DefaultValue("0") long firstOrderId,
                                        @FormParam("second_order_id") @DefaultValue("0") long secondOrderId
    ) {
        doctorOrderService.changeTwoDoctorOrderInHospital(firstOrderId, secondOrderId);
        return Response.ok().build();
    }

    @Path("/change/department_order")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeDepartmentOrder(@Context HttpServletRequest request,
                                          @FormParam("first_order_id") @DefaultValue("0") long firstOrderId,
                                          @FormParam("second_order_id") @DefaultValue("0") long secondOrderId
    ) {
        doctorOrderService.changeTwoDoctorOrderInDepartment(firstOrderId, secondOrderId);
        return Response.ok().build();
    }

    private void fillHospitalDepartment(DoctorOrderBean order) {
        if (null!=order) {
            DoctorBean doctor = doctorService.getDoctorById(order.getDoctorId(), null);
            HospitalBean hospital = hospitalService.getOneById(order.getHospitalId());
            HospitalDepartmentBean department = departmentService.getById(order.getDepartmentId(), utility.getHttpPrefixForNurseGo());
            order.setDoctor(doctor);
            order.setHospital(hospital);
            order.setDepartment(department);
        }
    }

}
