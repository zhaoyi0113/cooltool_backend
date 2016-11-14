package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.constants.AdminRole;
import com.cooltoo.nurse360.hospital.service.HospitalAdminRolesService;
import com.cooltoo.nurse360.hospital.service.HospitalAdminService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/10.
 */
@Path("/admin/hospital_management/user")
public class HospitalAdminManageAPI {

    @Autowired private HospitalAdminService adminService;
    @Autowired private HospitalAdminRolesService adminRolesService;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countHospitalAdmin(@Context HttpServletRequest request,
                                       @QueryParam("name") @DefaultValue("") String name,
                                       @QueryParam("telephone") @DefaultValue("") String telephone,
                                       @QueryParam("email") @DefaultValue("") String email,
                                       @QueryParam("hospitalId") @DefaultValue("") String strHospitalId,
                                       @QueryParam("departmentId") @DefaultValue("") String strDepartmentId,
                                       @QueryParam("adminType") @DefaultValue("") String strAdminType, /* administrator, normal */
                                       @QueryParam("status") @DefaultValue("") String strStatus /* enabled, disabled */
    ) {
        Integer hospitalId   = VerifyUtil.isIds(strHospitalId)   ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        AdminUserType adminType = AdminUserType.parseString(strAdminType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = adminService.countAdminUser(name, telephone, email, hospitalId, departmentId, adminType, status);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countHospitalAdmin(@Context HttpServletRequest request,
                                       @QueryParam("name") @DefaultValue("") String name,
                                       @QueryParam("telephone") @DefaultValue("") String telephone,
                                       @QueryParam("email") @DefaultValue("") String email,
                                       @QueryParam("hospitalId") @DefaultValue("") String strHospitalId,
                                       @QueryParam("departmentId") @DefaultValue("") String strDepartmentId,
                                       @QueryParam("adminType") @DefaultValue("") String strAdminType, /* administrator, normal */
                                       @QueryParam("status") @DefaultValue("") String strStatus, /* enabled, disabled */
                                       @QueryParam("index") @DefaultValue("0") int pageIndex,
                                       @QueryParam("number") @DefaultValue("0") int sizePerPage
    ) {
        Integer hospitalId   = VerifyUtil.isIds(strHospitalId)   ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        AdminUserType adminType = AdminUserType.parseString(strAdminType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<HospitalAdminBean> adminUsers = adminService.getAdminUser(name, telephone, email, hospitalId, departmentId, adminType, status, pageIndex, sizePerPage);
        return Response.ok(adminUsers).build();
    }

    @Path("/{admin_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHospitalAdmin(@Context HttpServletRequest request,
                                     @PathParam("admin_id") @DefaultValue("0") long adminId
    ) {
        HospitalAdminBean bean = adminService.getAdminUser(adminId);
        return Response.ok(bean).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHospitalAdmin(@Context HttpServletRequest request,
                                        @FormParam("admin_id") @DefaultValue("0") long adminId,
                                        @FormParam("name") @DefaultValue("") String name,
                                        @FormParam("password") @DefaultValue("") String password,
                                        @FormParam("telephone") @DefaultValue("") String telephone,
                                        @FormParam("email") @DefaultValue("") String email,
                                        @FormParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                        @FormParam("department_id") @DefaultValue("-1") int departmentId
    ) {
        HospitalAdminBean bean = adminService.updateAdminUser(adminId, name, password, telephone, email, hospitalId, departmentId, null);
        return Response.ok(bean).build();
    }

    @Path("/status")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHospitalAdmin(@Context HttpServletRequest request,
                                        @FormParam("admin_id") @DefaultValue("0") long adminId,
                                        @FormParam("status") @DefaultValue("") String strStatus /* enabled, disabled */
    ) {
        HospitalAdminBean bean = adminService.updateAdminUser(adminId, null, null, null, null, -1, -1, CommonStatus.parseString(strStatus));
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
        long adminId = adminService.addAdminUser(name, password, telephone, email, hospitalId, departmentId);
        return Response.ok(adminId).build();
    }

    //===================================================================================================================
    //            Role Service
    //===================================================================================================================

    @Path("/role")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getManagementHttpUrl(@Context HttpServletRequest request) {
        return Response.ok(AdminRole.getAll()).build();
    }

    @Path("/role")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdminRole(@Context HttpServletRequest request,
                                 @FormParam("admin_id") @DefaultValue("0") long adminId,
                                 @FormParam("role") @DefaultValue("") String role
    ) {
        long accessUrlId = adminRolesService.addAdminRole(adminId, AdminRole.parseString(role));
        return Response.ok(accessUrlId).build();
    }

    @Path("/role")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdminRole(@Context HttpServletRequest request,
                                    @FormParam("admin_id") @DefaultValue("0") long adminId,
                                    @FormParam("role") @DefaultValue("") String role
    ) {
        List<Long> accessUrlIds = adminRolesService.deleteAdminRole(adminId, AdminRole.parseString(role));
        return Response.ok(accessUrlIds).build();
    }

}
