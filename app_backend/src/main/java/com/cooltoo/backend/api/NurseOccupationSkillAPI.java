package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.SocialAbilitiesBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseOccupationSkillService;
import com.cooltoo.constants.ContextKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(NurseOccupationSkillAPI.class.getName());

    @Autowired
    private NurseOccupationSkillService skillService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getAllSkills(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} get all skills.", userId);
        List<SocialAbilitiesBean> skills = skillService.getAllSkills(userId);
        return Response.ok(skills).build();
    }

    @GET
    @Path("/{skill_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSkills(@Context HttpServletRequest request,
                              @PathParam("skill_id") int skillId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} get skill {}.", userId, skillId);
        SocialAbilitiesBean skill = skillService.getSkill(userId, skillId);
        return Response.ok(skill).build();
    }

    @GET
    @Path("/friend/{friend_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getFriendSkill(@Context HttpServletRequest request,
                                   @PathParam("friend_id") long friendId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} get friend {} 's skills.", userId, friendId);
        List<SocialAbilitiesBean> skills = skillService.getAllSkills(friendId);
        return Response.ok(skills).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addSkill(@Context HttpServletRequest request,
                             @FormParam("skill_id") int skillId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} add skill {}.", userId, skillId);
        skillService.addSkill(userId, skillId);
        return Response.ok(skillId).build();
    }

    @Path("/batch_add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addSkills(@Context HttpServletRequest request,
                              @FormParam("skill_ids") String skillIds
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} add skills {} with batch adding.", userId, skillIds);
        skillService.addSkills(userId, skillIds);
        return Response.ok(skillIds).build();
    }
}
