package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseSkillNominationBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseSkillNominationService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.OccupationSkillType;
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
public class NurseSkillNominationAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseSkillNominationAPI.class);

    @Autowired
    private NurseSkillNominationService nominationService;

    @POST
    @Path("/nominate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response nominateSkill(@Context HttpServletRequest request,
                                  @FormParam("friend_id") long friendId,
                                  @FormParam("skill_id") int skillId,
                                  @FormParam("type") String type) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        OccupationSkillType occupationType = OccupationSkillType.parseString(type);
        long count = nominationService.nominateNurseSkill(userId, skillId, occupationType, friendId);
        logger.info("get skill nominate count "+count);

        Map<String, String> ret = new Hashtable<String, String>();
        ret.put("skill_id", skillId+"");
        ret.put("count", count+"");
        return Response.ok(ret).build();
    }

    @GET
    @Path("/nominate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNurseAllSkillNomination(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseSkillNominationBean> allSkills = nominationService.getAllTypeNominated(userId);
        return Response.ok(allSkills).build();
    }

}
