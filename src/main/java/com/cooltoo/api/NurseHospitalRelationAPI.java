package com.cooltoo.api;

import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.serivces.NurseHospitalRelationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Path("/nurse_hospital_relation")
public class NurseHospitalRelationAPI {

    private static final Logger logger = Logger.getLogger(NurseHospitalRelationAPI.class.getName());

    @Autowired
    private NurseHospitalRelationService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<NurseHospitalRelationBean> all = service.getAll();
        return Response.ok(all).build();
    }

    @Path("/getone")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOneById(@DefaultValue("-1") @FormParam("id") long id) {
        NurseHospitalRelationBean bean = service.getOneById(id);
        logger.info("get nurse_hospital_relation is " + bean);
        if (null == bean) {
            Response.ok().build();
        }
        return Response.ok(bean).build();
    }

    @Path("/delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteById(@DefaultValue("-1") @FormParam("id") long id) {
        NurseHospitalRelationBean one = service.deleteById(id);
        logger.info("delete nurse_hospital_relation is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @Path("/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @DefaultValue("-1") @FormParam("id") long id,
            @DefaultValue("-1") @FormParam("nurseId") long nurseId,
            @DefaultValue("-1") @FormParam("hospitalId") int hospitalId,
            @DefaultValue("-1") @FormParam("departmentId") int departmentId,
            @FormParam("name") String name) {
        NurseHospitalRelationBean one = service.update(id, nurseId, hospitalId, departmentId);
        logger.info("update nurse_hospital_relation is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @Path("/new")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response newOne(
            @DefaultValue("-1") @FormParam("nurseId") long nurseId,
            @DefaultValue("-1") @FormParam("hospitalId") int hospitalId,
            @DefaultValue("-1") @FormParam("departmentId") int departmentId
    ) {
        long id = service.newOne(nurseId, hospitalId, departmentId);
        logger.info("new nurse_hospital_relation id is " + id);
        return Response.ok(id).build();
    }
}
