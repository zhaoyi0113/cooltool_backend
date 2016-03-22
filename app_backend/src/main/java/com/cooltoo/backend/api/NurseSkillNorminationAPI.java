package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseSkillNorminationBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseSkillNorminationService;
import com.cooltoo.constants.ContextKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by yzzhao on 3/13/16.
 */
@Path("/nurse/skill")
@LoginAuthentication(requireNurseLogin = true)
public class NurseSkillNorminationAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseSkillNorminationAPI.class);

    @Autowired
    private NurseSkillNorminationService norminationService;

    @POST
    @Path("/norminate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response nominateSkill(@Context HttpServletRequest request, @FormParam("friend_id") long friendId, @FormParam("skill_id") int skillId) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        long count = norminationService.nominateNurseSkill(userId, skillId, friendId);
        logger.info("get skill norminate count "+count);
        Map<String, String> ret = new Hashtable<String, String>();
        ret.put("count", count+"");
        return Response.ok(ret).build();
    }

    @GET
    @Path("/norminate/{index}/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNurseAllSkillNormination(@Context HttpServletRequest request,
                                                @PathParam("index") int index,
                                                @PathParam("number") int number) {
        long userId = Long.parseLong(request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID).toString());
        List<NurseSkillNorminationBean> allSkills = norminationService.getAllSkillsNominationCount(userId, index, number);
        return Response.ok(allSkills).build();
    }

    @GET
    @Path("/norminate/{skill_Id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNurseSkillNormination(@Context HttpServletRequest request,
                                             @PathParam("skill_id") int skillId) {
        long userId = Long.parseLong(request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID).toString());
        long count = norminationService.getSkillNorminationCount(userId, skillId);
        return Response.ok(count).build();
    }
}
