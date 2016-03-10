package com.cooltoo.api;

import com.cooltoo.serivces.OccupationSkillService;
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

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addNewOccupationSkill(@FormDataParam("name") String name,
                                          @FormDataParam("file") InputStream inputStream) {
        skillService.addNewOccupationSkill(name, inputStream);
        return Response.ok().build();
    }

    @DELETE
    public Response deleteOccupationSkill(int id) {
        skillService.deleteOccupationSkill(id);
        return Response.ok().build();
    }

    @POST
    @Path("/edit")
    public Response editOccupationSkill(@FormDataParam("id") int id,
                                        @FormDataParam("name") String name,
                                        @FormDataParam("file") InputStream inputStream) {
        skillService.editOccupationSkill(id, name, inputStream);
        return Response.ok().build();
    }

    @POST
    @Path("/edit_without_image")
    public Response editOccupationSkill(@FormDataParam("id") int id,
                                        @FormDataParam("name") String name
                                        ) {
        skillService.editOccupationSkillWithoutImage(id, name);
        return Response.ok().build();
    }
}
