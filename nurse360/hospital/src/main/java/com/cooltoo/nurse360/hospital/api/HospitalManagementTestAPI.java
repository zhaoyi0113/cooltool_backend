package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.ContextKeys;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/10/20.
 */
@RestController
@RequestMapping("/nurse360_hospital/test")
public class HospitalManagementTestAPI {

    ///////////////////////////////////////////////////////////////////
    //            supported
    ///////////////////////////////////////////////////////////////////
    @RequestMapping(path = "/get0",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public Object testGet1(HttpServletRequest request) {
        String ret = "testing get0 param="+ request.getAttribute(ContextKeys.ADMIN_USER_TOKEN);
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("return", ret);
        return obj;
    }

    ///////////////////////////////////////////////////////////////////
    //            supported
    ///////////////////////////////////////////////////////////////////
    @RequestMapping(path = "/get1",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public Object testGet1(@RequestParam(name = "p1", required = false, defaultValue = "") String p1) {
        String ret = "testing get1 param="+ p1;
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("return", ret);
        return obj;
    }

    ///////////////////////////////////////////////////////////////////
    //            supported
    ///////////////////////////////////////////////////////////////////
    @RequestMapping(path = "/get2/{p2}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public Object testGet2(@PathVariable String p2) {
        String ret = "testing get2 param="+ p2;
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("return", ret);
        return obj;
    }

    ///////////////////////////////////////////////////////////////////
    //            supported
    ///////////////////////////////////////////////////////////////////
    @RequestMapping(path = "/post1",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.MULTIPART_FORM_DATA)
    public Object testPost1(@RequestPart(name = "p1", required = false) MultipartFile p1) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
        String ret = reader.readLine();
        ret = "testing post1 param="+ret;
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("return", ret);
        return obj;
    }

    ///////////////////////////////////////////////////////////////////
    //            supported
    ///////////////////////////////////////////////////////////////////
    @RequestMapping(path = "/post2",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON)
    public Object testPost2(@RequestParam(name = "p1", required = false, defaultValue = "") String p1) {
        String ret = "testing post2 param="+p1;
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("return", ret);
        return obj;
    }


    ///////////////////////////////////////////////////////////////////
    //            not supported
    ///////////////////////////////////////////////////////////////////
    @RequestMapping(path = "/put1",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.MULTIPART_FORM_DATA)
    public Object testPut1(@RequestPart(name = "p1", required = false) MultipartFile p1) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
        String ret = reader.readLine();
        ret = "testing put2 param="+ret;
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("return", ret);
        return obj;
    }

    ///////////////////////////////////////////////////////////////////
    //            supported
    ///////////////////////////////////////////////////////////////////
    @RequestMapping(path = "/put2",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON)
    public Object testPut2(@RequestParam(name = "p1", required = false, defaultValue = "") String p1) {
        String ret = "testing put1 param="+p1;
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("return", ret);
        return obj;
    }

    ///////////////////////////////////////////////////////////////////
    //            supported
    ///////////////////////////////////////////////////////////////////
    @RequestMapping(path = "/delete",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON)
    public Object testDelete(@RequestParam(name = "p1", required = false, defaultValue = "") String p1) {
        String ret = "testing delete param="+p1;
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("return", ret);
        return obj;
    }

}
