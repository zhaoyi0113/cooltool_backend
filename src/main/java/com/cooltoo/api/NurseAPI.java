package com.cooltoo.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.serivces.NurseService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Path("/nurse")
public class NurseAPI {

    private static final Logger logger = Logger.getLogger(NurseAPI.class.getName());

    @Autowired
    NurseService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllNurses() {
        List<NurseBean> all = service.getAll();
        return Response.ok(all).build();
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newNurse(
            @FormParam("name") String name,
            @FormParam("age") int age,
            @FormParam("gender") int gender,
            @DefaultValue("")@FormParam("mobile") String mobile,
            @DefaultValue("") @FormParam("identificateId") String identificateId
    ) {
        if (null == name || "".equals(name.trim()) || age<0 || gender<0) {
            logger.info("new urse name="+name +" age="+age +" gender="+gender);
            return Response.ok().build();
        }
        long id = service.newNurse(identificateId, name,age, gender, mobile);
        return Response.ok(id).build();
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNurse(@DefaultValue("-1") @FormParam("id") long id) {
        NurseBean one = service.getNurse(id);
        logger.info("get nurse is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @POST
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteNurse(@DefaultValue("-1")@FormParam("id") long id) {
        NurseBean one = service.deleteNurse(id);
        logger.info("delete nurse is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateNurse(
            @DefaultValue("-1") @FormParam("id") long id,
            @FormParam("name") String name,
            @FormParam("age") int age,
            @FormParam("gender") int gender,
            @DefaultValue("")@FormParam("mobile") String mobile,
            @DefaultValue("") @FormParam("identificateId") String identificateId
    ) {
        NurseBean one = service.updateNurse(id, identificateId, name,age, gender, mobile);
        logger.info("update nurse is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }
}
