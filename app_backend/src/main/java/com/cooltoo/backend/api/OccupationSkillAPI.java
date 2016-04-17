package com.cooltoo.backend.api;

import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.SkillService;
import com.cooltoo.constants.OccupationSkillStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by yzzhao on 3/10/16.
 */
@Path("/occupation")
public class OccupationSkillAPI {

    @Autowired
    private SkillService skillService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getOccupationSkillList() {
        String enableSkill = OccupationSkillStatus.ENABLE.name();
        return Response.ok(skillService.getSkillByStatus(OccupationSkillStatus.ENABLE.name())).build();
    }
}
