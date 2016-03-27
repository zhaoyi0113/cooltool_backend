package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseOccupationSkillBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseOccupationSkillService;
import com.cooltoo.constants.ContextKeys;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
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

    @GET
    @Path("/{skill_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSkills(
            @Context HttpServletRequest request,
            @PathParam("skill_id") int skillId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseOccupationSkillBean skill = skillService.getSkill(userId, skillId);
        return Response.ok(skill).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addSkill(
            @Context HttpServletRequest request,
            @FormParam("skill_id") int skillId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        skillService.addSkill(userId, skillId);
        return Response.ok(skillId).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response deleteSkill(
            @Context HttpServletRequest request,
            @FormParam("skill_id") int skillId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        skillService.removeSkill(userId, skillId);
        return Response.ok(skillId).build();
    }

    @DELETE
    @Path("/{id}")
    @LoginAuthentication(requireNurseLogin = true)
    public Response deleteSkillById(
            @Context HttpServletRequest request,
            @PathParam("id") int id
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        skillService.removeSkill(id);
        return Response.ok(id).build();
    }
}
