package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.nurse360.hospital.service.HospitalAdminAccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/10.
 */
@RestController
@RequestMapping("/nurse360_hospital")
public class HospitalAdminLoginAPI {

    @Autowired private HospitalAdminAccessTokenService adminAccessTokenService;


    @RequestMapping(path = "/logout", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public Map<String, Boolean> logout(HttpServletRequest request) {

        String adminToken = (String) request.getAttribute(ContextKeys.ADMIN_USER_TOKEN);
        adminAccessTokenService.setTokenDisable(adminToken);

        Map<String, Boolean> ret = new HashMap<>();
        ret.put("result", Boolean.TRUE);
        return ret;
    }


    @RequestMapping(path = "/logined", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public Map<String, Boolean> isLogin(HttpServletRequest request) {

        String token = (String) request.getAttribute(ContextKeys.ADMIN_USER_TOKEN);
        boolean login = adminAccessTokenService.isTokenEnable(token);

        Map<String, Boolean> retVal = new HashMap<>();
        retVal.put("logined", login);
        return retVal;
    }
}
