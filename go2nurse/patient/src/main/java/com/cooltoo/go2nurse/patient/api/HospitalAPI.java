package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.services.CommonHospitalService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/8/23.
 */
@Path("/hospital")
public class HospitalAPI {

    @Autowired private CommonHospitalService hospitalService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getHospital(@Context HttpServletRequest request,
                                @QueryParam("index") @DefaultValue("0") int pageIndex,
                                @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<HospitalBean> hospitals = hospitalService.searchHospitalByConditions(true, null, null, null, null, null, 1, 1, pageIndex, sizePerPage);
        return Response.ok(hospitals).build();
    }
}
