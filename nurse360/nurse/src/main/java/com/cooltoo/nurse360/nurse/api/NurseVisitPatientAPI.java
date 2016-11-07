package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.NurseVisitPatientBean;
import com.cooltoo.go2nurse.beans.NurseVisitPatientServiceItemBean;
import com.cooltoo.go2nurse.service.NurseVisitPatientService;
import com.cooltoo.go2nurse.service.NurseVisitPatientServiceItemService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import com.cooltoo.util.JSONUtil;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/7.
 */
@Path("/nurse/visit/patient")
public class NurseVisitPatientAPI {

    @Autowired private CommonNurseHospitalRelationService nurseHospitalRelation;
    @Autowired private NurseVisitPatientServiceItemService visitPatientServiceItem;
    @Autowired private NurseVisitPatientService visitPatientService;
    private JSONUtil jsonUtil = JSONUtil.newInstance();

    @Path("/service/item")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getVisitPatientServiceItems(@Context HttpServletRequest request) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseHospitalRelationBean nurseHospital = nurseHospitalRelation.getRelationByNurseId(nurseId, null);
        List<NurseVisitPatientServiceItemBean> items = new ArrayList<>();
        if (null!=nurseHospital) {
            items = visitPatientServiceItem.getVisitPatientServiceItem(nurseHospital.getHospitalId(), nurseHospital.getDepartmentId());
        }
        return Response.ok(items).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getVisitPatientRecord(@Context HttpServletRequest request,
                                          @QueryParam("user_id") @DefaultValue("-1") long userId,
                                          @QueryParam("patient_id") @DefaultValue("-1") long patientId,
                                          @QueryParam("index") @DefaultValue("0") int pageIndex,
                                          @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseVisitPatientBean> visits = visitPatientService.getVisitRecord(
                0>userId ? null : userId,
                0>patientId ? null : patientId,
                nurseId,
                null,
                pageIndex, sizePerPage);
        return Response.ok(visits).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response setVisitPatientRecordStatusToDelete(@Context HttpServletRequest request,
                                                        @FormParam("visit_record_id") @DefaultValue("0") long visitRecordId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<Long> updateIds = visitPatientService.setDeleteStatusVisitRecordByIds(nurseId, Arrays.asList(new Long[]{visitRecordId}));
        return Response.ok(updateIds).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response updateVisitPatientRecord(@Context HttpServletRequest request,
                                             @FormParam("visit_record_id") @DefaultValue("0") long visitRecordId,
                                             @FormParam("visit_record") @DefaultValue("") String visitRecord,
                                             @FormParam("service_item_ids") @DefaultValue("") String serviceItemIds
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseVisitPatientServiceItemBean> serviceItems = visitPatientServiceItem.getVisitPatientServiceItem(serviceItemIds);
        String serviceItemsJson = "";
        if (!VerifyUtil.isListEmpty(serviceItems)) {
            serviceItemsJson = jsonUtil.toJsonString(serviceItems);
        }
        NurseVisitPatientBean visit = visitPatientService.updateVisitRecord(nurseId, visitRecordId, visitRecord, serviceItemsJson);
        return Response.ok(visit).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addVisitPatientRecord(@Context HttpServletRequest request,
                                          @FormParam("user_id") @DefaultValue("0") long userId,
                                          @FormParam("patient_id") @DefaultValue("0") long patientId,
                                          @FormParam("service_item_ids") @DefaultValue("") String serviceItemIds,
                                          @FormParam("visit_record") @DefaultValue("") String visitRecord,
                                          @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseVisitPatientServiceItemBean> serviceItems = visitPatientServiceItem.getVisitPatientServiceItem(serviceItemIds);
        String serviceItemsJson = "";
        if (!VerifyUtil.isListEmpty(serviceItems)) {
            serviceItemsJson = jsonUtil.toJsonString(serviceItems);
        }
        long visitId = visitPatientService.addVisitRecord(nurseId, userId, patientId, orderId, visitRecord, serviceItemsJson);
        return Response.ok(visitId).build();
    }

    @Path("/image")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addVisitPatientRecordImage(@Context HttpServletRequest request,
                                               @FormDataParam("visit_record_id") @DefaultValue("0") long visitRecordId,
                                               @FormDataParam("image_name") String imageName,
                                               @FormDataParam("image") InputStream image,
                                               @FormDataParam("image")FormDataContentDisposition imageDis
    ) {
        Map<String, String> visitImageIdUrl = visitPatientService.addVisitRecorImage(visitRecordId, imageName, image);
        return Response.ok(visitImageIdUrl).build();
    }

    @Path("/sign")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addPatientSignImage(@Context HttpServletRequest request,
                                        @FormDataParam("visit_record_id") @DefaultValue("0") long visitRecordId,
                                        @FormDataParam("image_name") String imageName,
                                        @FormDataParam("image") InputStream image,
                                        @FormDataParam("image")FormDataContentDisposition imageDis
    ) {
        Map<String, String> visitImageIdUrl = visitPatientService.addPatientSignImage(visitRecordId, imageName, image);
        return Response.ok(visitImageIdUrl).build();
    }

}
