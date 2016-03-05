package com.cooltoo.api;

import com.cooltoo.beans.HospitalDepartmentRelationBean;
import com.cooltoo.entities.HospitalDepartmentRelationEntity;
import com.cooltoo.serivces.HospitalDepartmentRelationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Path("/hospital_department_relation")
public class HospitalDepartmentRelationAPI {

    private static final Logger logger = Logger.getLogger(HospitalDepartmentRelationAPI.class.getName());

    @Autowired
    private HospitalDepartmentRelationService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<HospitalDepartmentRelationBean> all = service.getAll();
        return Response.ok(all).build();
    }

    @Path("/getone")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOneById(@DefaultValue("-1") @FormParam("id") int id) {
        HospitalDepartmentRelationBean bean = service.getOneById(id);
        logger.info("get hospital department relation is " + bean);
        if (null == bean) {
            Response.ok().build();
        }
        return Response.ok(bean).build();
    }

    @Path("/delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteById(@DefaultValue("-1") @FormParam("id") int id) {
        HospitalDepartmentRelationBean one = service.deleteById(id);
        logger.info("delete hospital department relation is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @Path("/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @DefaultValue("-1") @FormParam("id") int id,
            @DefaultValue("-1") @FormParam("hospitalId") int hospitalId,
            @DefaultValue("-1") @FormParam("departmentId") int departmentId,
            @FormParam("name") String name) {
        HospitalDepartmentRelationBean one = service.update(id, hospitalId, departmentId);
        logger.info("update hospital department relation is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @Path("/new")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response newOne(
            @DefaultValue("-1") @FormParam("hospitalId") int hospitalId,
            @DefaultValue("-1") @FormParam("departmentId") int departmentId
    ) {
        int id = service.newOne(hospitalId, departmentId);
        logger.info("new hospital department relation id is " + id);
        return Response.ok(id).build();
    }
}
