package com.cooltoo.backend.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yzzhao on 5/22/16.
 */
@Path("/app/version")
public class AppVersionAPI {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAppVersion(){
        Map<String, String> version = new HashMap<>();
        version.put("APP_VERSION", "1.1");
        return Response.ok(version).build();
    }
}
