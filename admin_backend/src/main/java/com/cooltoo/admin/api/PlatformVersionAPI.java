package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.services.PlatformVersionService;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by yzzhao on 5/22/16.
 */
@Path("/admin/platform/version")
public class PlatformVersionAPI {

    @Autowired
    private PlatformVersionService platformVersionService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllPlatformVersions(){
        return Response.ok(platformVersionService.getAllPlatformVersions()).build();
    }

    @GET
    @Path("/type/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getPlatformVersionByType(@PathParam("type") String type){
        return Response.ok(platformVersionService.getPlatformVersionByType(type)).build();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response addNewVersion(@FormParam("type") String type, @FormParam("version") String version, @FormParam("link") String link){
        return Response.ok(platformVersionService.addPlatformVersion(type, version, link)).build();
    }

    @POST
    @Path("/edit")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editPlatformVersion(@FormDataParam("id") int id, @FormParam("type") String type, @FormParam("version") String version,@FormParam("link") String link){
        return Response.ok(platformVersionService.editPlatformVersion(id, type, version,link)).build();
    }

}
