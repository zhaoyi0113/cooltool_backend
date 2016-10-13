package com.cooltoo.backend.api;

import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.services.CommonDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/3/30.
 */
@Path("/nurse/department")
public class NurseDepartmentAPI {

    @Autowired
    private CommonDepartmentService service;

    @Path("/{hospital_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getAllTopLevelDepartment(@PathParam("hospital_id") @DefaultValue("0") int hospitalId) {
        List<HospitalDepartmentBean> all = service.getAllTopLevelDepartmentEnable(hospitalId, "");
        return Response.ok(all).build();
    }

    @Path("/{hospital_id}/{parent_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSecondLevelDepartment(@PathParam("hospital_id") @DefaultValue("0") int hospitalId,
                                              @PathParam("parent_id") @DefaultValue("0") int parentId
    ) {
        List<HospitalDepartmentBean> all = service.getSecondLevelDepartmentEnable(hospitalId, parentId, "");
        return Response.ok(all).build();
    }
}
