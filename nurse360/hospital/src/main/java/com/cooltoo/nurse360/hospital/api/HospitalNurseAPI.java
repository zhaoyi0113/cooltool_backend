package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.service.NurseServiceForGo2Nurse;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.hospital.service.HospitalAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/22.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalNurseAPI {

    @Autowired private HospitalAdminService adminService;
    @Autowired private NurseServiceForGo2Nurse nurseService;


    //=============================================================
    //            Authentication of NURSE Role
    //=============================================================
    @RequestMapping(path = "/nurse/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countNurse(HttpServletRequest request,
                           @RequestParam(defaultValue = "",  name = "fuzzy_name") String fuzzyName
    ) {
        long adminId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        HospitalAdminBean admin = adminService.getAdminUser(adminId);
        long count = nurseService.countNurseByCanAnswerQuestion(fuzzyName, null, null, admin.getHospitalId(), admin.getDepartmentId(), null);
        return count;
    }

    @RequestMapping(path = "/nurse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<NurseBean> getNurse(HttpServletRequest request,
                                    @RequestParam(defaultValue = "",  name = "fuzzy_name") String fuzzyName,
                                    @RequestParam(defaultValue = "0",  name = "index") int index,
                                    @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        long adminId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        HospitalAdminBean admin = adminService.getAdminUser(adminId);
        List<NurseBean> nurses = nurseService.getNurseByCanAnswerQuestion(fuzzyName, null, null, admin.getHospitalId(), admin.getDepartmentId(), null, index, number);
        return nurses;
    }

}
