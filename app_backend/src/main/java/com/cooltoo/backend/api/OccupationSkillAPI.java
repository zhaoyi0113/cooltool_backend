package com.cooltoo.backend.api;

import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.OccupationSkillService;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by yzzhao on 3/10/16.
 */
@Path("/occupation")
public class OccupationSkillAPI {

    @Autowired
    private OccupationSkillService skillService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getOccupationSkillList() {
        return Response.ok(skillService.getOccupationSkillList()).build();
    }
}
