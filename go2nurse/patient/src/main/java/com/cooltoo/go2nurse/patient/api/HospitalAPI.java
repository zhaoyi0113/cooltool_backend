package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
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
 * Created by hp on 2016/8/23.
 */
@Path("/hospital")
public class HospitalAPI {

    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private Go2NurseUtility utility;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getHospital(@Context HttpServletRequest request) {
        int count = (int)hospitalService.countHospitalByConditions(true, null, null, null, null, null, 1, 1);
        List<HospitalBean> hospitals = hospitalService.searchHospitalByConditions(true, null, null, null, null, null, 1, 1, 0, count);
        return Response.ok(hospitals).build();
    }

    @Path("/{hospital_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getHospital(@Context HttpServletRequest request,
                                @PathParam("hospital_id") int hospitalId
    ) {
        HospitalBean hospital = hospitalService.getHospital(hospitalId);
        return Response.ok(hospital).build();
    }

    @Path("/department/{department_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getDepartment(@Context HttpServletRequest request,
                                  @PathParam("department_id") int departmentId
    ) {
        HospitalDepartmentBean department = departmentService.getById(departmentId, utility.getHttpPrefixForNurseGo());
        return Response.ok(department).build();
    }

    @Path("/department/top/{hospital_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getTopLevelDepartment(@Context HttpServletRequest request,
                                          @PathParam("hospital_id") int hospitalId
    ) {
        List<HospitalDepartmentBean> topDep = departmentService.getAllTopLevelDepartmentEnable(hospitalId, utility.getHttpPrefixForNurseGo());
        return Response.ok(topDep).build();
    }

    @Path("/department/second/{hospital_id}/{top_department_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getSecondLevelDepartment(@Context HttpServletRequest request,
                                             @PathParam("hospital_id") int hospitalId,
                                             @PathParam("top_department_id") int topDepId
    ) {
        List<HospitalDepartmentBean> secondLevelDep = departmentService.getSecondLevelDepartmentEnable(hospitalId, topDepId, utility.getHttpPrefixForNurseGo());
        return Response.ok(secondLevelDep).build();
    }


}
