package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/10/9.
 */
@Path("/hospital_department")
public class HospitalDepartmentAPI {

    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private Nurse360Utility utility;

    @Path("/hospital")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getHospital(@Context HttpServletRequest request) {
        int count = (int)hospitalService.countHospitalByConditions(true, null, null, null, null, null, 1, 1);
        List<HospitalBean> hospitals = hospitalService.searchHospitalByConditions(true, null, null, null, null, null, 1, 1, 0, count);
        return Response.ok(hospitals).build();
    }

    @Path("/department/{hospital_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getHospital(@Context HttpServletRequest request,
                                @PathParam("hospital_id") @DefaultValue("0") int hospitalId
    ) {
        List<HospitalDepartmentBean> departments = departmentService.getByHospitalId(hospitalId, utility.getHttpPrefixForNurseGo());
        for (int i = 0; i<departments.size(); i++) {
            HospitalDepartmentBean department = departments.get(i);
            if (department.getParentId()>0) {
                departments.remove(i);
                i--;
            }
        }
        return Response.ok(departments).build();
    }
}
