package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.go2nurse.beans.PatientBadgeBean;
import com.cooltoo.go2nurse.service.PatientBadgeService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lg380357 on 2016/3/3.
 */
@Path("/patient_badge")
public class PatientBadgeAPI {

    private static final Logger logger = LoggerFactory.getLogger(PatientBadgeAPI.class.getName());

    @Autowired
    private PatientBadgeService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<PatientBadgeBean> all = service.getAll();
        return Response.ok(all).build();
    }

    @Path("/getone")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOneById(@DefaultValue("-1") @FormParam("id") int id) {
        PatientBadgeBean bean = service.getOneById(id);
        logger.info("get patient-badge is " + bean);
        if (null == bean) {
            Response.ok().build();
        }
        return Response.ok(bean).build();
    }

    @Path("/delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteById(@DefaultValue("-1") @FormParam("id") int id) {
        PatientBadgeBean one = service.deleteById(id);
        logger.info("delete patient-badge is " + one);
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
            @DefaultValue("-1") @FormParam("patientId") long patientId,
            @DefaultValue("-1") @FormParam("badgeId") int badgeId) {
        PatientBadgeBean one = service.update(id, patientId, badgeId);
        logger.info("update patient-badge is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @Path("/new")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response newOne(
            @DefaultValue("-1") @FormParam("patientId") long patientId,
            @DefaultValue("-1") @FormParam("badgeId") int badgeId
    ) {
        int id = service.newOne(patientId, badgeId);
        logger.info("new patient-badge id is " + id);
        return Response.ok(id).build();
    }
}
