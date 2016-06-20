package com.cooltoo.backend.api;

import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.HospitalService;
import com.cooltoo.backend.services.NurseHospitalRelationService;
import com.cooltoo.beans.HospitalBean;
import com.cooltoo.backend.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.ContextKeys;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Path("/nurse/hospital")
public class NurseHospitalAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseHospitalAPI.class.getName());

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private NurseHospitalRelationService hospitalRelationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getAll() {
        List<HospitalBean> all = hospitalService.getAllHospitalEnable();
        return Response.ok(all).build();
    }

    @Path("/{province_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getByProvinceId(@Context HttpServletRequest request,
                                    @PathParam("province_id") int provinceId
    ) {
        List<HospitalBean> provinceHospitals = hospitalService.getHospitalByProvince(provinceId, 1);
        return Response.ok(provinceHospitals).build();
    }

    @Path("/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response searchHospital(@DefaultValue("") @QueryParam("name") String  name) {
        List<HospitalBean> hospitals = hospitalService.searchHospital(true, true, name, -1, -1, -1, "", 1, -1, 0, 0);
        logger.info("get hospital size is {}", hospitals.size());
        if (null == hospitals) {
            Response.ok(new ArrayList<>()).build();
        }
        return Response.ok(hospitals).build();
    }

    @Path("/relation")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getNurseAndHospitalRelation(@Context HttpServletRequest request) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseHospitalRelationBean relation = hospitalRelationService.getRelationByNurseId(nurseId);
        logger.info("user " + nurseId + " get hospital relation ======" + relation);
        return Response.ok(relation).build();
    }

    @Path("/relation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response setNurseAndHospitalRelation(@Context HttpServletRequest request,
                                                @FormParam("hospital")      int hospitalId,
                                                @FormParam("department")    int departmentId) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        long relationId = hospitalRelationService.newOne(nurseId, hospitalId, departmentId);
        logger.info("user " + nurseId + " select hospital " + hospitalId + " select department " + departmentId);
        return Response.ok(relationId).build();
    }

    @Path("/set_hospital_with_name")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response setNurseAndHospitalRelation(@Context HttpServletRequest request,
                                                @FormParam("hospital_name") @DefaultValue("") String hospitalName
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        long relationId = hospitalRelationService.newOne(nurseId, hospitalName);
        logger.info("user {} set hospital_name={}", nurseId, hospitalName);
        return Response.ok(relationId).build();
    }
}
