package com.cooltoo.nurse360.hospital.api;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zhaolisong on 2016/10/20.
 */
@Path("/hospital_management")
public class HospitalManagementTestAPI {

    private static final Logger logger = LoggerFactory.getLogger(HospitalManagementTestAPI.class);

    @Path("/test_get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response testGet(@Context HttpServletRequest request,
                            @QueryParam("p1") @DefaultValue("") String p1
    ) {
        logger.info("test_get param={}", p1);
        return Response.ok(p1).build();
    }

    @Path("/test_post1")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response testPost1(@Context HttpServletRequest request,
                              @FormDataParam("p1") @DefaultValue("") InputStream p1
    ) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(p1));
        String line = reader.readLine();
        logger.info("test_post1 param={}", line);
        return Response.ok(p1).build();
    }

    @Path("/test_post2")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response testPost2(@Context HttpServletRequest request,
                              @FormParam("p1") @DefaultValue("") String p1
    ) {
        logger.info("test_post2 param={}", p1);
        return Response.ok(p1).build();
    }

    @Path("/test_put")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response testPut(@Context HttpServletRequest request,
                            @FormParam("p1") @DefaultValue("") String p1
    ) {
        logger.info("test_put param={}", p1);
        return Response.ok(p1).build();
    }

    @Path("/test_delete")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response testDelete(@Context HttpServletRequest request,
                               @QueryParam("p1") @DefaultValue("") String p1
    ) {
        logger.info("test_delete param={}", p1);
        return Response.ok(p1).build();
    }

}
