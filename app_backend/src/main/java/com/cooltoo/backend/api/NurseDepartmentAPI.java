package com.cooltoo.backend.api;

import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.HospitalDepartmentService;
import com.cooltoo.beans.HospitalDepartmentBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by zhaolisong on 16/3/30.
 */
@Path("/nurse/department")
public class NurseDepartmentAPI {

    private static final Logger logger = Logger.getLogger(NurseHospitalAPI.class.getName());

    @Autowired
    private HospitalDepartmentService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getAllTopLevelDepartment() {
        List<HospitalDepartmentBean> all = service.getAllTopLevelDepartmentEnable();
        return Response.ok(all).build();
    }

    @Path("/{parent_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSecondLevelDepartment(@PathParam("parent_id") int parentId) {
        List<HospitalDepartmentBean> all = service.getSecondLevelDepartmentEnable(parentId);
        return Response.ok(all).build();
    }
}
