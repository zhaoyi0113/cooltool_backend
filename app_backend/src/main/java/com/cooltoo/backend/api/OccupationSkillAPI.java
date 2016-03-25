package com.cooltoo.backend.api;

import com.cooltoo.backend.services.OccupationSkillService;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Created by yzzhao on 3/10/16.
 */
@Path("/occupation")
public class OccupationSkillAPI {

    @Autowired
    private OccupationSkillService skillService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOccupationSkillList() {
        return Response.ok(skillService.getOccupationSkillList()).build();
    }

    @GET
    @Path("/types")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOccupationSkillTypes() {
        return Response.ok(skillService.getAllSkillTypes()).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addNewOccupationSkill(
            @FormDataParam("name") String name,
            @FormDataParam("type") String type,
            @FormDataParam("file") InputStream inputStream) {
        skillService.addNewOccupationSkill(name, type, inputStream);
        return Response.ok().build();
    }

    @DELETE
    public Response deleteOccupationSkill(int id) {
        skillService.deleteOccupationSkill(id);
        return Response.ok().build();
    }

    @POST
    @Path("/edit")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response editOccupationSkill(@FormDataParam("id") int id,
                                        @FormDataParam("type") String type,
                                        @FormDataParam("name") String name,
                                        @FormDataParam("file") InputStream inputStream) {
        skillService.editOccupationSkill(id, name, type, inputStream);
        return Response.ok().build();
    }

    @POST
    @Path("/edit_without_image")
    public Response editOccupationSkill(@FormParam("id") int id,
                                        @FormParam("name") String name,
                                        @FormParam("type") String type
                                        ) {
        skillService.editOccupationSkillWithoutImage(id, name, type);
        return Response.ok().build();
    }
}
