package com.cooltoo.api;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.serivces.HospitalDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Path("/hospital_department")
public class HospitalDepartmentAPI {

    private static final Logger logger = Logger.getLogger(HospitalDepartmentAPI.class.getName());

    @Autowired
    private HospitalDepartmentService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<HospitalDepartmentBean> all = service.getAll();
        return Response.ok(all).build();
    }

    @Path("/getone")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOneById(@DefaultValue("-1") @FormParam("id") int id) {
        HospitalDepartmentBean bean = service.getOneById(id);
        logger.info("get hospital department is " + bean);
        if (null == bean) {
            Response.ok().build();
        }
        return Response.ok(bean).build();
    }

    @Path("/delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteById(@DefaultValue("-1") @FormParam("id") int id) {
        HospitalDepartmentBean one = service.deleteById(id);
        logger.info("delete hospital department is " + one);
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
            @FormParam("name") String name) {
        HospitalDepartmentBean one = service.update(id, name);
        logger.info("update hospital department is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @Path("/new")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response newOne(
            @FormParam("name") String name
    ) {
        int id = service.createHospitalDepartment(name);
        logger.info("new hospital department id is " + id);
        return Response.ok(id).build();
    }
}
