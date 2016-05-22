package com.cooltoo.backend.api;

import com.cooltoo.constants.PlatformType;
import com.cooltoo.services.PlatformVersionService;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private PlatformVersionService versionService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAppVersion(){
        Map<String, String> version = new HashMap<>();
        version.put("version", "1.1");
        versionService.getPlatformLatestVersion(PlatformType.IOS);
        return Response.ok(version).build();
    }
}
