package com.cooltoo.nurse360.admin.api;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NurseVisitPatientServiceItemBean;
import com.cooltoo.go2nurse.service.NurseVisitPatientServiceItemService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/7.
 */
@Path("/admin/nurse/visit/patient/service/item")
public class NurseVisitPatientServiceItemManageAPI {

    @Autowired private NurseVisitPatientServiceItemService nurseVisitPatientServiceItem;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countVisitPatientServiceItem(@Context HttpServletRequest request,
                                                 @QueryParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                                 @QueryParam("department_id") @DefaultValue("-1") int departmentId
    ) {
        long items = nurseVisitPatientServiceItem.countVisitPatientServiceItem(
                0>hospitalId ? null : hospitalId,
                0>departmentId ? null : departmentId
        );
        return Response.ok(items).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVisitPatientServiceItem(@Context HttpServletRequest request,
                                               @QueryParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                               @QueryParam("department_id") @DefaultValue("-1") int departmentId,
                                               @QueryParam("index") @DefaultValue("0") int pageIndex,
                                               @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<NurseVisitPatientServiceItemBean> items = nurseVisitPatientServiceItem.getVisitPatientServiceItem(
                0>hospitalId ? null : hospitalId,
                0>departmentId ? null : departmentId,
                pageIndex, sizePerPage);
        return Response.ok(items).build();
    }

    @Path("/{item_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVisitPatientServiceItem(@Context HttpServletRequest request,
                                               @PathParam("item_id") @DefaultValue("-1") int itemId
    ) {
        List<NurseVisitPatientServiceItemBean> items = nurseVisitPatientServiceItem.getVisitPatientServiceItem(itemId+"");
        if (VerifyUtil.isListEmpty(items)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        return Response.ok(items.get(0)).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response setVisitPatientRecordStatusToDelete(@Context HttpServletRequest request,
                                                        @FormParam("service_item_id") @DefaultValue("0") long serviceItemId
    ) {
        List<Long> updateIds = nurseVisitPatientServiceItem.deleteServiceItemByIds(Arrays.asList(new Long[]{serviceItemId}));
        return Response.ok(updateIds).build();
    }


    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateVisitPatientRecord(@Context HttpServletRequest request,
                                             @FormParam("service_item_id") @DefaultValue("0") long serviceItemId,
                                             @FormParam("item_name") @DefaultValue("") String itemName,
                                             @FormParam("item_description") @DefaultValue("") String itemDescription
    ) {
        NurseVisitPatientServiceItemBean item = nurseVisitPatientServiceItem.updateServiceItem(serviceItemId, itemName, itemDescription);
        return Response.ok(item).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVisitPatientRecord(@Context HttpServletRequest request,
                                          @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                          @FormParam("department_id") @DefaultValue("0") int departmentId,
                                          @FormParam("item_name") @DefaultValue("") String itemName,
                                          @FormParam("item_description") @DefaultValue("") String itemDescription
    ) {
        long itemId = nurseVisitPatientServiceItem.addServiceItem(hospitalId, departmentId, itemName, itemDescription);
        return Response.ok(itemId).build();
    }
}
