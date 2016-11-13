package com.cooltoo.nurse360.hospital.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaoyi0113 on 13/11/2016.
 */
@RestController
@RequestMapping("/nurse360_hospital")
public class HospitalAdminController {

    @RequestMapping(value = "/user", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public Map<String, String> user() {
        Map<String, String> resp = new HashMap<>();
        resp.put("name", "user");
        return resp;
    }

    @RequestMapping(value = "/admin", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public Map<String, String> admin() {
        Map<String, String> resp = new HashMap<>();
        resp.put("name", "admin");
        return resp;
    }


}
