package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.hospital.service.HospitalAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * Created by zhaolisong on 2016/11/10.
 */
@RestController
@RequestMapping("/nurse360_hospital/information")
public class HospitalAdminAPI {

    @Autowired private HospitalAdminService adminService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public HospitalAdminBean getHospitalAdmin(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        HospitalAdminBean admin = adminService.getAdminUser(adminId);
        return admin;
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
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

}
