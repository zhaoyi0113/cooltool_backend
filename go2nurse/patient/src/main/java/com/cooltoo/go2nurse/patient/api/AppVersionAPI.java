package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.beans.PlatformVersionBean;
import com.cooltoo.services.PlatformVersionService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by yzzhao on 5/22/16.
 */
@Path("/app/version")
public class AppVersionAPI {

    @Autowired
    private PlatformVersionService versionService;

    @GET
    @Path("/{platform}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAppVersion(@PathParam("platform") String platform){
        PlatformVersionBean version = versionService.getPlatformLatestVersion(platform);
        return Response.ok(version).build();
    }
}
