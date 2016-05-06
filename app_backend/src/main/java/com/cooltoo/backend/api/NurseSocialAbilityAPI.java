package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.SocialAbilitiesBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseHospitalRelationService;
import com.cooltoo.backend.services.NurseSkillService;
import com.cooltoo.backend.services.NurseSocialAbilitiesService;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.SocialAbilityType;
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
 * Created by zhaolisong on 16/3/25.
 */
@Path("/nurse/skill")
public class NurseSocialAbilityAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseSocialAbilityAPI.class.getName());

    @Autowired private NurseSkillService skillService;
    @Autowired private NurseSocialAbilitiesService  abilitiesService;
    @Autowired private NurseHospitalRelationService hospitalRelationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getAllAbility(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} get all social abilities.", userId);
        List<SocialAbilitiesBean> abilities = abilitiesService.getUserAllTypeAbilities(userId);
        logger.info("User {} get all social abilities {}.", abilities);
        return Response.ok(abilities).build();
    }

    @GET
    @Path("/{skill_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getSkillAbility(@Context HttpServletRequest request,
                                    @PathParam("skill_id") int skillId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} get skill {} ability.", userId, skillId);
        SocialAbilitiesBean ability = abilitiesService.getUserSpecialAbility(userId, skillId, SocialAbilityType.SKILL.name());
        logger.info("User {} get skill {} ability value={}.", userId, skillId, ability);
        return Response.ok(ability).build();
    }

    @GET
    @Path("/skill_department")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getDepartmentAbility(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} get department social ability.", userId);

        NurseHospitalRelationBean hospital = hospitalRelationService.getRelationByNurseId(userId);
        if (null==hospital || hospital.getDepartmentId()<=0) {
            return Response.ok().build();
        }

        SocialAbilitiesBean ability = abilitiesService.getUserSpecialAbility(
                                            userId, hospital.getDepartmentId(),
                                            SocialAbilityType.OCCUPATION.name());
        logger.info("User {} get department social ability. value={}.", userId, ability);

        return Response.ok(ability).build();
    }

    @GET
    @Path("/friend/{friend_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getFriendAllAbility(@Context HttpServletRequest request,
                                        @PathParam("friend_id") long friendId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} get friend {} 's all social abilities.", userId, friendId);
        List<SocialAbilitiesBean> abilities = abilitiesService.getUserAllTypeAbilities(friendId);
        logger.info("User {} get friend {} 's all social abilities {}.", abilities);
        return Response.ok(abilities).build();
    }

    @GET
    @Path("/friend/{friend_id}/{skill_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getFriendSkillAbility(@Context HttpServletRequest request,
                                          @PathParam("friend_id") long friendId,
                                          @PathParam("skill_id") int skillId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} get friend {} 's skill {} social abilities.", userId, friendId, skillId);
        SocialAbilitiesBean abilities = abilitiesService.getUserSpecialAbility(friendId, skillId, SocialAbilityType.SKILL.name());
        logger.info("User {} get friend {} 's skill {} social abilities {} .", userId, friendId, skillId, abilities);
        return Response.ok(abilities).build();
    }

    @GET
    @Path("/skill_department/{friend_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getFriendDepartmentAbility(@Context HttpServletRequest request,
                                               @PathParam("friend_id") long friendId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} get friend {} department social ability.", userId, friendId);

        NurseHospitalRelationBean hospital = hospitalRelationService.getRelationByNurseId(friendId);
        logger.info("friend {} 's hospital--department relation ship is {} ", friendId, hospital);
        if (null==hospital || hospital.getDepartmentId()<=0) {
            return Response.ok().build();
        }

        SocialAbilitiesBean ability = abilitiesService.getUserSpecialAbility(
                friendId, hospital.getDepartmentId(),
                SocialAbilityType.OCCUPATION.name());
        logger.info("User {} get friend {} department social ability. value={}", userId, friendId, ability);

        return Response.ok(ability).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addSkillAbility(@Context HttpServletRequest request,
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
    public Response addSkillAbilities(@Context HttpServletRequest request,
                                      @FormParam("skill_ids") String skillIds
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} add skills {} with batch adding.", userId, skillIds);
        skillService.addSkills(userId, skillIds);
        return Response.ok(skillIds).build();
    }

    @POST
    @Path("/nominate")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response nominateSkill(@Context HttpServletRequest request,
                                  @FormParam("friend_id") long friendId,
                                  @FormParam("skill_id") int skillId) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("User {} nominate friend {} 's skills {}.", userId, friendId, skillId);

        SocialAbilitiesBean ability = abilitiesService.nominateSocialAbility(userId, friendId, skillId, SocialAbilityType.SKILL.name());
        logger.info("User {} nominate friend {} 's skills {}. value={}", userId, friendId, skillId, ability);

        Map<String, String> ret = new Hashtable<String, String>();
        ret.put("skill_id", skillId+"");
        ret.put("count", ability.getNominatedCount()+"");
        return Response.ok(ret).build();
    }

//
// 第一版本暂不实现该功能。
//
//    @Path("/nominate_department_ability")
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @LoginAuthentication(requireNurseLogin = true)
//    public Response thumbsUpDepartment(@Context HttpServletRequest request,
//                                       @FormParam("friend_id") long friendId) {
//        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
//        logger.info("user {} nominate friend {} 's department.", userId, friendId);
//
//        NurseHospitalRelationBean relation = hospitalRelationService.getRelationByNurseId(friendId);
//        logger.info("friend's hospital_department relationship is {}" , relation);
//        if (null==relation || relation.getDepartmentId()<=0) {
//            return null;
//        }
//        SocialAbilitiesBean ability =  abilitiesService.nominateSocialAbility(userId, friendId, relation.getDepartmentId(), SocialAbilityType.OCCUPATION.name());
//        logger.info("user {} thumbs up friend {} 's department. value {}", userId, friendId, ability);
//
//        Map<String, String> ret = new Hashtable<String, String>();
//        ret.put("skill_id", relation.getDepartmentId()+"");
//        ret.put("count", ability.getNominatedCount()+"");
//        return Response.ok(ret).build();
//    }
}
