package com.cooltoo.go2nurse.admin.api;

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
@Path("/admin/patient")
public class PatientManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(PatientManageAPI.class);

    @Autowired private PatientService service;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countByConditions(@Context HttpServletRequest request,
                                      @QueryParam("name") @DefaultValue("") String name,
                                      @QueryParam("gender") @DefaultValue("-1") int gender,
                                      @QueryParam("identityCard") @DefaultValue("") String identityCard,
                                      @QueryParam("mobile") @DefaultValue("") String mobile,
                                      @QueryParam("status") @DefaultValue("ALL") String status
    ) {
        long count = service.countAll(name, gender, mobile, identityCard, status);
        logger.info("patient count is {}", count);
        return Response.ok(count).build();
    }

    @Path("/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByPatientsId(@Context HttpServletRequest request,
                                    @QueryParam("name") @DefaultValue("") String name,
                                    @QueryParam("gender") @DefaultValue("-1") int gender,
                                    @QueryParam("identityCard") @DefaultValue("") String identityCard,
                                    @QueryParam("mobile") @DefaultValue("") String mobile,
                                    @QueryParam("status") @DefaultValue("ALL") String status,
                                    @QueryParam("index") @DefaultValue("0") int pageIndex,
                                    @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<PatientBean> beans = service.getAll(name, gender, mobile, identityCard, status, pageIndex, sizePerPage);
        logger.info("patient count is {}", beans.size());
        return Response.ok(beans).build();
    }

    @Path("/update_status")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Context HttpServletRequest request,
                           @FormParam("id") @DefaultValue("-1") long id,
//                           @FormParam("name") @DefaultValue("") String name,
//                           @FormParam("gender") @DefaultValue("2") int gender,
//                           @FormParam("birthday") @DefaultValue("") String strBirthday,
//                           @FormParam("identityCard") @DefaultValue("") String identityCard,
//                           @FormParam("mobile") @DefaultValue("") String mobile,
                           @FormParam("status") @DefaultValue("ENABLED") String status
    ) {
//        Date birthday = null;
//        long time = NumberUtil.getTime(strBirthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
//        if (time > 0) {
//            birthday = new Date(time);
//        }
        PatientBean one = service.update(id, null, -1, null, null, null, status);
        return Response.ok(one).build();
    }
}
