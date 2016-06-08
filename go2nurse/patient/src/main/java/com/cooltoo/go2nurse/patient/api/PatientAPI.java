package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.converter.PatientBeanConverter;
import com.cooltoo.go2nurse.converter.PatientEntityConverter;
import com.cooltoo.go2nurse.service.PatientService;
import com.cooltoo.util.NumberUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.List;

/**
 * Created by yzzhao on 2/29/16.
 */
@Path("/patient")
public class PatientAPI {

    private static final Logger logger = Logger.getLogger(PatientAPI.class.getName());

    @Autowired
    private PatientService service;

    @Autowired
    private PatientBeanConverter beanConverter;

    @Autowired
    private PatientEntityConverter entityConverter;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<PatientBean> all = service.getAll();
        return Response.ok(all).build();
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newOne(
            @FormParam("name") String name,
            @DefaultValue("") @FormParam("nickname") String nickname,
            @DefaultValue("0") @FormParam("certificateId") int certificateId,
            @DefaultValue("0") @FormParam("officeId") int officeId,
            @DefaultValue("") @FormParam("mobile") String mobile,
            @DefaultValue("0") @FormParam("age") int age,
            @DefaultValue("") @FormParam("birthday") String birthday,
            @DefaultValue("") @FormParam("usercol") String usercol
    ) {
        Date date = null;
        long time = NumberUtil.getTime(birthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (time > 0) {
            date = new Date(time);
        }
        if (null == name || "".equals(name.trim()) || age<0) {
            logger.info("new patient name="+name +" age="+age);
            return Response.ok().build();
        }
        long id = service.create(name, nickname, certificateId, officeId, mobile, age, date, usercol);
        return Response.ok(id).build();
    }

    @POST
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOne(@DefaultValue("-1") @FormParam("id") long id) {
        PatientBean one = service.getOneById(id);
        logger.info("get patient is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @POST
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOne(@DefaultValue("-1")@FormParam("id") long id) {
        PatientBean one = service.delete(id);
        logger.info("delete patient is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOne(
            @DefaultValue("-1") @FormParam("id") long id,
            @DefaultValue("") @FormParam("name") String name,
            @DefaultValue("") @FormParam("nickname") String nickname,
            @DefaultValue("0") @FormParam("certificateId") int certificateId,
            @DefaultValue("0") @FormParam("officeId") int officeId,
            @DefaultValue("") @FormParam("mobile") String mobile,
            @DefaultValue("0") @FormParam("age") int age,
            @DefaultValue("") @FormParam("birthday") String birthday,
            @DefaultValue("") @FormParam("usercol") String usercol
    ) {
        Date date = null;
        long time = NumberUtil.getTime(birthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (time > 0) {
            date = new Date(time);
        }
        PatientBean one = service.update(id, name, nickname, certificateId, officeId, mobile, age, date, usercol);
        logger.info("update patient is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(id).build();
    }
}
