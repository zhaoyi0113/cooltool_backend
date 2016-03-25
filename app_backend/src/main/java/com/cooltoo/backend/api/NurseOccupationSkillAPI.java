package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseOccupationSkillBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseOccupationSkillService;
import com.cooltoo.constants.ContextKeys;
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
 * Created by zhaolisong on 16/3/25.
 */
@Path("/nurse/skill")
public class NurseOccupationSkillAPI {

    @Autowired
    private NurseOccupationSkillService skillService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getAllSkills(
            @Context HttpServletRequest request
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseOccupationSkillBean> skills = skillService.getAllSkills(userId);
        return Response.ok(skills).build();
    }



}
