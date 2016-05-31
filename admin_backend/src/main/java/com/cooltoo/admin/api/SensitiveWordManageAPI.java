package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.services.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/5/31.
 */
@Path("/admin/sensitive_word")
public class SensitiveWordManageAPI {

    @Autowired private SensitiveWordService wordService;

    @Path("/type")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin =  true)
    public Response getAllType(@Context HttpServletRequest request) {
        List<String> allType = wordService.getAllType();
        return Response.ok(allType).build();
    }


}
