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
import java.util.*;

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
    public Response getHospital(@Context HttpServletRequest request) {
        int count = (int)hospitalService.countHospitalByConditions(true, null, null, null, null, null, 1, 1);
        List<HospitalBean> hospitals = hospitalService.searchHospitalByConditions(true, null, null, null, null, null, 1, 1, 0, count);
        return Response.ok(hospitals).build();
    }

    @Path("/department/{hospital_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHospital(@Context HttpServletRequest request,
                                @PathParam("hospital_id") @DefaultValue("0") int hospitalId
    ) {
        List<HospitalDepartmentBean> departments = departmentService.getByHospitalId(hospitalId, utility.getHttpPrefixForNurseGo());

        Map<Integer, HospitalDepartmentBean> parentIdToBean = new HashMap<>();
        for (int i = 0; i<departments.size(); i++) {
            HospitalDepartmentBean department = departments.get(i);

            // top department
            if (department.getParentId()<=0) {
                parentIdToBean.put(department.getId(), department);
                continue;
            }

            // sub department
            HospitalDepartmentBean parent = parentIdToBean.get(department.getParentId());
            if (null==parent) {
                continue;
            }
            List<HospitalDepartmentBean> subDep = parent.getSubDepartment();
            if (null==subDep) {
                subDep = new ArrayList<>();
                parent.setSubDepartment(subDep);
            }
            subDep.add(department);
        }

        departments.clear();
        SortedMap<Integer, HospitalDepartmentBean> sort = new TreeMap<>(parentIdToBean);
        Set<Map.Entry<Integer, HospitalDepartmentBean>> set = sort.entrySet();
        Iterator<Map.Entry<Integer, HospitalDepartmentBean>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, HospitalDepartmentBean> entry=it.next();
            departments.add(entry.getValue());
        }
        return Response.ok(departments).build();
    }
}
