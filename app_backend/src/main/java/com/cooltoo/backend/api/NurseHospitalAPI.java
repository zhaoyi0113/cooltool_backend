package com.cooltoo.backend.api;

import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.HospitalService;
import com.cooltoo.beans.HospitalBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by zhaolisong on 16/3/29.
 */@Path("/nurse/hospital")
   public class NurseHospitalAPI {

    private static final Logger logger = Logger.getLogger(NurseHospitalAPI.class.getName());

    @Autowired
    private HospitalService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication( requireNurseLogin = true)
    public Response getAll() {
        List<HospitalBean> all = service.getAll();
        return Response.ok(all).build();
    }
}
