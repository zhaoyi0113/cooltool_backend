package com.cooltoo.nurse360.hospital.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zhaolisong on 2016/10/20.
 */
@RestController
@RequestMapping("/nurse360_hospital/test")
public class HospitalManagementTestAPI {

    ///////////////////////////////////////////////////////////////////
    //            supported
    ///////////////////////////////////////////////////////////////////
    @RequestMapping(path = "/get1",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public Object testGet1(@RequestParam(name = "p1", required = false, defaultValue = "") String p1) {
        String ret = "test_get1 param="+ p1;
        return ret;
    }

    ///////////////////////////////////////////////////////////////////
    //            supported
    ///////////////////////////////////////////////////////////////////
    @RequestMapping(path = "/get2/{p2}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public Object testGet2(@PathVariable String p2) {
        String ret = "test_get2 param="+ p2;
        return ret;
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
        String line = reader.readLine();
        line = "test_post1 param="+line;
        return line;
    }

    ///////////////////////////////////////////////////////////////////
    //            supported
    ///////////////////////////////////////////////////////////////////
    @RequestMapping(path = "/post2",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON)
    public Object testPost2(@RequestParam(name = "p1", required = false, defaultValue = "") String p1) {
        String ret = "test_post2 param="+p1;
        return ret;
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
        String line = reader.readLine();
        line = "test_put2 param="+line;
        return line;
    }

    @RequestMapping(path = "/put2",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON)
    public Object testPut2(@RequestParam(name = "p1", required = false, defaultValue = "") String p1) {
        String ret = "test_put1 param="+p1;
        return ret;
    }

    @RequestMapping(path = "/delete",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON)
    public Object testDelete(@RequestParam(name = "p1", required = false, defaultValue = "") String p1) {
        String ret = "test_delete param="+p1;
        return ret;
    }

}
