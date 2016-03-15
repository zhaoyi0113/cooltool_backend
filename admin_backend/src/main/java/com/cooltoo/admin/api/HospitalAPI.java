package com.cooltoo.admin.api;

import com.cooltoo.backend.services.HospitalService;
import com.cooltoo.beans.HospitalBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Path("/hospital")
public class HospitalAPI {

    private static final Logger logger = Logger.getLogger(HospitalAPI.class.getName());

    @Autowired
    private HospitalService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<HospitalBean> all = service.getAll();
        return Response.ok(all).build();
    }

    @Path("/getone")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOneById(@DefaultValue("-1") @FormParam("id") int id) {
        HospitalBean bean = service.getOneById(id);
        logger.info("get hospital is " + bean);
        if (null == bean) {
            Response.ok().build();
        }
        return Response.ok(bean).build();
    }

    @Path("/delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteById(@DefaultValue("-1") @FormParam("id") int id) {
        HospitalBean one = service.deleteById(id);
        logger.info("delete hospital is " + one);
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
            @FormParam("name") String name,
            @DefaultValue("") @FormParam("province") String province,
            @DefaultValue("") @FormParam("city") String city) {
        HospitalBean one = service.update(id, name, province, city);
        logger.info("update hospital is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @Path("/new")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response newOne(
            @FormParam("name") String name,
            @DefaultValue("") @FormParam("province") String province,
            @DefaultValue("") @FormParam("city") String city
    ) {
        int id = service.newOne(name, province, city);
        logger.info("new hospital id is " + id);
        return Response.ok(id).build();
    }
}
