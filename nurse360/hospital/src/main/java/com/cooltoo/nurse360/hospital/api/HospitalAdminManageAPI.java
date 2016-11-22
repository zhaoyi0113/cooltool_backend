package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.nurse360.beans.HospitalAdminAuthentication;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.constants.AdminRole;
import com.cooltoo.nurse360.hospital.service.HospitalAdminRolesService;
import com.cooltoo.nurse360.hospital.service.HospitalAdminService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/10.
 */
@RestController
@RequestMapping("/nurse360_hospital")
public class HospitalAdminManageAPI {

    @Autowired private HospitalAdminService adminService;
    @Autowired private HospitalAdminRolesService adminRolesService;


    //=============================================================
    //            Information For ALL
    //=============================================================
    @RequestMapping(path = "/information", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public HospitalAdminBean getHospitalAdmin(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        HospitalAdminBean admin = adminService.getAdminUser(adminId);
        return admin;
    }

    @RequestMapping(path = "/information", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public HospitalAdminBean updateHospitalAdmin(HttpServletRequest request,
                                                 @RequestParam(required = false, defaultValue = "",   name = "name")      String name,
                                                 @RequestParam(required = false, defaultValue = "",   name = "password")  String password,
                                                 @RequestParam(required = false, defaultValue = "",   name = "telephone") String telephone,
                                                 @RequestParam(required = false, defaultValue = "",   name = "email")     String email,
                                                 @RequestParam(required = false, defaultValue = "-1", name = "hospital_id")  int hospitalId,
                                                 @RequestParam(required = false, defaultValue = "-1", name = "department_id")int departmentId
    ) {
        Long adminId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        HospitalAdminBean bean = adminService.updateAdminUser(adminId, name, password, telephone, email, hospitalId, departmentId, null);
        return bean;
    }


    //=============================================================
    //            Authentication of ADMINISTRATOR Role
    //=============================================================
    @RequestMapping(path = "/admin/users/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countHospitalAdmin(@RequestParam(required = false, defaultValue = "", name = "name") String name,
                                       @RequestParam(required = false, defaultValue = "", name = "telephone") String telephone,
                                       @RequestParam(required = false, defaultValue = "", name = "email") String email,
                                       @RequestParam(required = false, defaultValue = "", name = "hospitalId") String strHospitalId,
                                       @RequestParam(required = false, defaultValue = "", name = "departmentId") String strDepartmentId,
                                       @RequestParam(required = false, defaultValue = "", name = "adminType") String strAdminType, /* administrator, normal, manager */
                                       @RequestParam(required = false, defaultValue = "", name = "status") String strStatus /* enabled, disabled */
    ) {
        Integer hospitalId   = VerifyUtil.isIds(strHospitalId)   ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        AdminUserType adminType = AdminUserType.parseString(strAdminType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = adminService.countAdminUser(name, telephone, email, hospitalId, departmentId, adminType, status);
        return count;
    }

    @RequestMapping(path = "/admin/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<HospitalAdminBean> getHospitalAdmin(@RequestParam(required = false, defaultValue = "", name = "name") String name,
                                                    @RequestParam(required = false, defaultValue = "", name = "telephone") String telephone,
                                                    @RequestParam(required = false, defaultValue = "", name = "email") String email,
                                                    @RequestParam(required = false, defaultValue = "", name = "hospitalId") String strHospitalId,
                                                    @RequestParam(required = false, defaultValue = "", name = "departmentId") String strDepartmentId,
                                                    @RequestParam(required = false, defaultValue = "", name = "adminType") String strAdminType, /* administrator, normal, manager */
                                                    @RequestParam(required = false, defaultValue = "", name = "status") String strStatus, /* enabled, disabled */
                                                    @RequestParam(defaultValue = "0", name = "index") int pageIndex,
                                                    @RequestParam(defaultValue = "10", name = "number") int sizePerPage
    ) {
        Integer hospitalId   = VerifyUtil.isIds(strHospitalId)   ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        AdminUserType adminType = AdminUserType.parseString(strAdminType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<HospitalAdminBean> adminUsers = adminService.getAdminUser(name, telephone, email, hospitalId, departmentId, adminType, status, pageIndex, sizePerPage);
        return adminUsers;
    }

    @RequestMapping(path = "/admin/users/{admin_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public HospitalAdminBean getHospitalAdmin(@PathVariable long admin_id) {
        HospitalAdminBean bean = adminService.getAdminUser(admin_id);
        return bean;
    }

    @RequestMapping(path = "/admin/users", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public HospitalAdminBean updateHospitalAdmin(@RequestParam(required = false, defaultValue = "0",  name = "admin_id") long adminId,
                                                 @RequestParam(required = false, defaultValue = "",   name = "name") String name,
                                                 @RequestParam(required = false, defaultValue = "",   name = "password") String password,
                                                 @RequestParam(required = false, defaultValue = "",   name = "telephone") String telephone,
                                                 @RequestParam(required = false, defaultValue = "",   name = "email") String email,
                                                 @RequestParam(required = false, defaultValue = "-1", name = "hospital_id") int hospitalId,
                                                 @RequestParam(required = false, defaultValue = "-1", name = "department_id") int departmentId
    ) {
        HospitalAdminBean bean = adminService.updateAdminUser(adminId, name, password, telephone, email, hospitalId, departmentId, null);
        return bean;
    }

    @RequestMapping(path = "/admin/users/status", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public HospitalAdminBean updateHospitalAdmin(@RequestParam(name = "admin_id") long adminId,
                                                 @RequestParam(name = "status") String strStatus /* enabled, disabled */
    ) {
        if (adminId==1/*1 is super administrator*/) { // mus be ENABLED
            strStatus = CommonStatus.ENABLED.name();
        }
        HospitalAdminBean bean = adminService.updateAdminUser(adminId, null, null, null, null, -1, -1, CommonStatus.parseString(strStatus));
        return bean;
    }

    @RequestMapping(path = "/admin/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Map<String, Long> createHospitalAdmin(@RequestParam(required = false, defaultValue = "",   name = "name")       String name,
                                                 @RequestParam(required = false, defaultValue = "",   name = "password")   String password,
                                                 @RequestParam(required = false, defaultValue = "",   name = "telephone")  String telephone,
                                                 @RequestParam(required = false, defaultValue = "",   name = "email")      String email,
                                                 @RequestParam(name = "hospital_id")   int hospitalId,
                                                 @RequestParam(name = "department_id") int departmentId,
                                                 @RequestParam(required = false, defaultValue = "",   name = "admin_type") String adminType /* administrator, manager, normal */
    ) {
        AdminUserType adminUserType = AdminUserType.parseString(adminType);
        adminUserType = null==adminUserType ? AdminUserType.NORMAL : adminUserType;
        long adminId = adminService.addAdminUser(name, password, telephone, email, hospitalId, departmentId, adminUserType);

        Map<String, Long> ret = new HashMap<>();
        ret.put("adminId", adminId);
        return ret;
    }

    //=============================================================
    //            Authentication of MANAGER Role
    //=============================================================
    @RequestMapping(path = "/manager/user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Map<String, Long> createHospitalAdmin(HttpServletRequest request,
                                                 @RequestParam(required = false, defaultValue = "",   name = "name")      String name,
                                                 @RequestParam(required = false, defaultValue = "",   name = "password")  String password,
                                                 @RequestParam(required = false, defaultValue = "",   name = "telephone") String telephone,
                                                 @RequestParam(required = false, defaultValue = "",   name = "email")     String email,
                                                 @RequestParam(name = "hospital_id")  int hospitalId,
                                                 @RequestParam(name = "department_id")int departmentId
    ) {
        Long adminId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        if (adminService.isSuperAdmin(adminId)) {
            adminId = adminService.addAdminUser(name, password, telephone, email, hospitalId, departmentId, AdminUserType.NORMAL);

            Map<String, Long> retVal = new HashMap<>();
            retVal.put("adminId", adminId);
            return retVal;
        }
        throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
    }


    //===================================================================================================================
    //            Role Service
    //===================================================================================================================

    @RequestMapping(path = "/users/role", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<AdminRole> getAdminAuthentication() {
        return AdminRole.getAll();
    }

    //=============================================================
    //            Authentication of ADMINISTRATOR Role
    //=============================================================

    @RequestMapping(path = "/admin/users/role", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Map<String, Long> addAdminRole(@RequestParam(name = "admin_id") long adminId,
                                          @RequestParam(name = "role") String role
    ) {
        long adminRoleRelationId = adminRolesService.addAdminRole(adminId, AdminRole.parseString(role));

        Map<String, Long> ret = new HashMap<>();
        ret.put("adminRoleRelationId", adminRoleRelationId);
        return ret;
    }

    @RequestMapping(path = "/admin/users/role", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
    public Map<String, List<Long>> deleteAdminRole(@RequestParam(required = true,  defaultValue = "0", name = "admin_id") long adminId,
                                                   @RequestParam(required = false, defaultValue = "",  name = "role") String role
    ) {
        List<Long> adminRoleRelationIds = adminRolesService.deleteAdminRole(adminId, AdminRole.parseString(role));

        Map<String, List<Long>> ret = new HashMap<>();
        ret.put("adminRoleRelationId", adminRoleRelationIds);
        return ret;
    }

    //=============================================================
    //            Authentication of MANAGER Role
    //=============================================================
    @RequestMapping(path = "/manager/users/role", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Map<String, Long> addNurseAdminRole(HttpServletRequest request,
                                               @RequestParam(name = "admin_id") long adminId,
                                               @RequestParam(name = "role") String role
    ) {
        HospitalAdminAuthentication authentication = (HospitalAdminAuthentication) SecurityContextHolder.getContext().getAuthentication();
        HospitalAdminBean admin = (HospitalAdminBean) authentication.getDetails();
        HospitalAdminBean modifyAdmin = adminService.getAdminUserWithoutInfo(adminId);

        if (admin.getHospitalId()>0 && admin.getDepartmentId()>0
                && admin.getHospitalId()==modifyAdmin.getHospitalId()
                && admin.getDepartmentId()==modifyAdmin.getDepartmentId()
                && AdminUserType.NORMAL.equals(modifyAdmin.getAdminType()))
        {
            long adminRoleRelationId = adminRolesService.addAdminRole(adminId, AdminRole.parseString(role));

            Map<String, Long> ret = new HashMap<>();
            ret.put("adminRoleRelationId", adminRoleRelationId);
            return ret;
        }

        throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
    }

    @RequestMapping(path = "/manager/users/role", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
    public Map<String, List<Long>> deleteNurseAdminRole(HttpServletRequest request,
                                                        @RequestParam(required = true,  defaultValue = "0", name = "admin_id") long adminId,
                                                        @RequestParam(required = false, defaultValue = "",  name = "role") String role
    ) {
        HospitalAdminAuthentication authentication = (HospitalAdminAuthentication) SecurityContextHolder.getContext().getAuthentication();
        HospitalAdminBean admin = (HospitalAdminBean) authentication.getDetails();
        HospitalAdminBean modifyAdmin = adminService.getAdminUserWithoutInfo(adminId);

        if (admin.getHospitalId()>0 && admin.getDepartmentId()>0
                && admin.getHospitalId()==modifyAdmin.getHospitalId()
                && admin.getDepartmentId()==modifyAdmin.getDepartmentId()
                && AdminUserType.NORMAL.equals(modifyAdmin.getAdminType()))
        {
            List<Long> adminRoleRelationIds = adminRolesService.deleteAdminRole(adminId, AdminRole.parseString(role));

            Map<String, List<Long>> ret = new HashMap<>();
            ret.put("adminRoleRelationId", adminRoleRelationIds);
            return ret;
        }

        throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
    }
}
