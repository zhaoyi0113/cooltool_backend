package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.beans.DoctorBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.DoctorService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.jpa.repository.Query;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/7/25.
 */
@Path("/user/doctor")
public class UserDoctorAPI {

    @Autowired private DoctorService doctorService;

    @Path("/count/by_hospital_department")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response countByHospitalDepartment(@Context HttpServletRequest request,
                                              @QueryParam("hospital_id") @DefaultValue("") String strHospitalId,
                                              @QueryParam("department_id") @DefaultValue("") String strDepartmentId,
                                              @QueryParam("index") @DefaultValue("0") int pageIndex,
                                              @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        Integer hospitalId = !VerifyUtil.isIds(strHospitalId) ? null : VerifyUtil.parseIntIds(strHospitalId).get(0);
        Integer departmentId = !VerifyUtil.isIds(strDepartmentId) ? null : VerifyUtil.parseIntIds(strDepartmentId).get(0);
        List<DoctorBean> doctors = doctorService.getDoctor(hospitalId, departmentId, statuses, pageIndex, sizePerPage);
        return Response.ok(doctors).build();
    }
}
