package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CategoryCourseOrderBean;
import com.cooltoo.go2nurse.service.CategoryCourseOrderService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Path("/admin/course/order")
public class CategoryCourseOrderManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(CategoryCourseOrderManageAPI.class);

    @Autowired private CategoryCourseOrderService categoryCourseOrderService;


    //==============================================================
    //                         getting
    //==============================================================
    @Path("/{order_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderById(@Context HttpServletRequest request,
                                 @PathParam("order_id") @DefaultValue("0") long orderId
    ) {
        CategoryCourseOrderBean order = categoryCourseOrderService.getOrderById(orderId);
        return Response.ok(order).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countOrderByConditions(@Context HttpServletRequest request,
                                           @QueryParam("hospital_id") String strHospitalId,
                                           @QueryParam("department_id") String strDepartmentId,
                                           @QueryParam("category_id") String strCategoryId
    ) {
        Long  categoryId = VerifyUtil.isIds(strCategoryId) ? VerifyUtil.parseLongIds(strCategoryId).get(0) : null;
        Integer hospitalId = VerifyUtil.isIds(strHospitalId) ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        long count = categoryCourseOrderService.countOrderByConditions(hospitalId, departmentId, categoryId);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderByConditions(@Context HttpServletRequest request,
                                         @QueryParam("hospital_id") String strHospitalId,
                                         @QueryParam("department_id") String strDepartmentId,
                                         @QueryParam("category_id") String strCategoryId,
                                         @QueryParam("index") @DefaultValue("0") int pageIndex,
                                         @QueryParam("number") @DefaultValue("0") int sizePerPage
    ) {
        Long  categoryId = VerifyUtil.isIds(strCategoryId) ? VerifyUtil.parseLongIds(strCategoryId).get(0) : null;
        Integer hospitalId = VerifyUtil.isIds(strHospitalId) ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        List<CategoryCourseOrderBean> orders = categoryCourseOrderService.getOrderByConditions(hospitalId, departmentId, categoryId, pageIndex, sizePerPage);
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
        categoryCourseOrderService.deleteOrder(orderId);
        return Response.ok().build();
    }


    //==============================================================
    //                         adding
    //==============================================================
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addServiceVendor(@Context HttpServletRequest request,
                                     @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                     @FormParam("department_id") @DefaultValue("0") int departmentId,
                                     @FormParam("category_id") @DefaultValue("0") long categoryId,
                                     @FormParam("course_id") @DefaultValue("0") long courseId,
                                     @FormParam("order") @DefaultValue("0") int order
    ) {
        if (-1==hospitalId && 0!=departmentId) {
            logger.error("cooltoo's department not zero");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else {
            departmentId = -1;
        }

        long orderId = categoryCourseOrderService.setCourseOrder(departmentId, categoryId, courseId, order);
        return Response.ok(orderId).build();
    }

    //==============================================================
    //                         editing
    //==============================================================
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeCategoryCourseOrder(@Context HttpServletRequest request,
                                              @FormParam("first_order_id") @DefaultValue("0") long firstOrderId,
                                              @FormParam("second_order_id") @DefaultValue("0") long secondOrderId
    ) {
        categoryCourseOrderService.changeTwoCategoryOrderInCategory(firstOrderId, secondOrderId);
        return Response.ok().build();
    }
}
