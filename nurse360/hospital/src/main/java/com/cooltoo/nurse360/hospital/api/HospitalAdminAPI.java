package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.service.hospital.HospitalAdminService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/10.
 */
@Path("/hospital_management/user")
public class HospitalAdminAPI {

    @Autowired private HospitalAdminService adminService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHospitalAdmin(@Context HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        HospitalAdminBean admin = adminService.getAdminUser(adminId);
        return Response.ok(admin).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHospitalAdmin(@Context HttpServletRequest request,
                                        @FormParam("name") @DefaultValue("") String name,
                                        @FormParam("password") @DefaultValue("") String password,
                                        @FormParam("telephone") @DefaultValue("") String telephone,
                                        @FormParam("email") @DefaultValue("") String email,
                                        @FormParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                        @FormParam("department_id") @DefaultValue("-1") int departmentId
    ) {
        Long adminId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        HospitalAdminBean bean = adminService.updateAdminUser(adminId, name, password, telephone, email, hospitalId, departmentId, null);
        return Response.ok(bean).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHospitalAdmin(@Context HttpServletRequest request,
                                        @FormParam("name") @DefaultValue("") String name,
                                        @FormParam("password") @DefaultValue("") String password,
                                        @FormParam("telephone") @DefaultValue("") String telephone,
                                        @FormParam("email") @DefaultValue("") String email,
                                        @FormParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                        @FormParam("department_id") @DefaultValue("-1") int departmentId
    ) {
        Long adminId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        if (adminService.isSuperAdmin(adminId)) {
            adminId = adminService.addAdminUser(name, password, telephone, email, hospitalId, departmentId);

            Map<String, Long> retVal = new HashMap<>();
            retVal.put("isLogin", adminId);
            return Response.ok(retVal).build();
        }
        throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
    }

}
