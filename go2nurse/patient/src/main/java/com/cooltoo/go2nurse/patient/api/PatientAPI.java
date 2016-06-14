package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.PatientService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yzzhao on 2/29/16.
 */
@Path("/patient")
public class PatientAPI {

    private static final Logger logger = LoggerFactory.getLogger(PatientAPI.class);

    @Autowired private PatientService service;

    @Path("/get_by_ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getByPatientsId(@Context HttpServletRequest request,
                                    @QueryParam("patients_id") @DefaultValue("") String strPatientsId
    ) {
        List<Long> patientsId = VerifyUtil.parseLongIds(strPatientsId);
        List<PatientBean> beans;
        if (!VerifyUtil.isListEmpty(patientsId)) {
            beans = service.getAllByStatusAndIds(patientsId, CommonStatus.ENABLED);
        }
        else {
            beans = new ArrayList<>();
        }
        logger.info("patient count is {}", beans.size());
        return Response.ok(beans).build();
    }

    @Path("/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response create(@Context HttpServletRequest request,
                           @FormParam("name") @DefaultValue("") String name,
                           @FormParam("gender") @DefaultValue("2") int gender,
                           @FormParam("birthday") @DefaultValue("") String strBirthday,
                           @FormParam("identityCard") @DefaultValue("") String identityCard,
                           @FormParam("mobile") @DefaultValue("") String mobile
    ) {
        Date date = null;
        long time = NumberUtil.getTime(strBirthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (time > 0) {
            date = new Date(time);
        }
        long id = service.create(name, gender, date, identityCard, mobile);
        return Response.ok(id).build();
    }

    @Path("/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response update(@Context HttpServletRequest request,
                           @FormParam("id") @DefaultValue("-1") long id,
                           @FormParam("name") @DefaultValue("") String name,
                           @FormParam("gender") @DefaultValue("2") int gender,
                           @FormParam("birthday") @DefaultValue("") String strBirthday,
                           @FormParam("identityCard") @DefaultValue("") String identityCard,
                           @FormParam("mobile") @DefaultValue("") String mobile,
                           @FormParam("status") @DefaultValue("ENABLED") String status
    ) {
        Date birthday = null;
        long time = NumberUtil.getTime(strBirthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (time > 0) {
            birthday = new Date(time);
        }
        PatientBean one = service.update(id, name, gender, birthday, identityCard, mobile, status);
        return Response.ok(one).build();
    }
}
