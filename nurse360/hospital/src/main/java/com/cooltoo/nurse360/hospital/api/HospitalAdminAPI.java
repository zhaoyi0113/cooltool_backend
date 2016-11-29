package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import com.cooltoo.nurse360.service.NurseServiceForNurse360;
import com.cooltoo.services.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

/**
 * Created by zhaolisong on 2016/11/29.
 */
@RestController
@RequestMapping("/nurse360_hospital")
public class HospitalAdminAPI {

    @Autowired private AdminUserService adminUserService;
    @Autowired private NurseServiceForNurse360 nurseService;

    //=============================================================
    //            Permit ALL Role
    //=============================================================
    @RequestMapping(path = "/information", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public Object getAdminInformation() {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());

        if (userDetails.isAdmin()) {
            return adminUserService.getUser(userDetails.getId());
        }
        else if (userDetails.isNurse() || userDetails.isNurseManager()) {
            return nurseService.getNurseById(userDetails.getId());
        }

        return null;
    }
}
