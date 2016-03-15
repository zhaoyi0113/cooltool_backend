package com.cooltoo.backend.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseSkillNominationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by yzzhao on 3/13/16.
 */
@Path("/nurse/skill")
public class NurseSkillNominationAPI {

    @Autowired
    private NurseSkillNominationService nominationService;

    @POST
    @Path("/nominate")
    @LoginAuthentication(requireNurseLogin = true)
    public Response nominateSkill(@Context HttpServletRequest request, @FormParam("friend_id") long friendId, @FormParam("skill_id") int skillId) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        nominationService.nominateNurseSkill(userId, skillId, friendId);
        return Response.ok().build();
    }


}
