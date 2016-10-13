package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.converter.social_ability.CommentAbilityTypeConverter;
import com.cooltoo.backend.converter.social_ability.SpeakAbilityTypeConverter;
import com.cooltoo.backend.converter.social_ability.ThumbsUpAbilityTypeConverter;
import com.cooltoo.beans.BadgeBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.*;
import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by hp on 2016/4/10.
 */
@Service("NurseSocialAbilitiesService")
public class NurseSocialAbilitiesService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSocialAbilitiesService.class.getName());

    @Autowired private NurseService                  nurseService;
    @Autowired private SkillService                  skillService;
    @Autowired private NurseSkillService             nurseSkillService;
    @Autowired private NurseAbilityNominationService nominationService;
    @Autowired private NurseSpeakService             speakService;
    @Autowired private NurseSpeakThumbsUpService     thumbsUpService;
    @Autowired private NurseSpeakCommentService      commentService;
    @Autowired private SpeakTypeService              speakTypeService;
    @Autowired private CommonNurseHospitalRelationService nurseHospitalService;
    @Autowired private BadgeService                  badgeService;
    @Autowired private NurseIntegrationService       integrationService;

    @Autowired private SpeakAbilityTypeConverter     speakTypeConverter;
    @Autowired private ThumbsUpAbilityTypeConverter  thumbsUpTypeConverter;
    @Autowired private CommentAbilityTypeConverter   commentTypeConverter;


    //=================================================================
    //          get
    //==================================================================
    private SocialAbilitiesBean newAbilityBean(
            long userId,
            int skillId, String skillName, String skillDescription, SocialAbilityType skillType,
            long factor, int point,
            long imageId, String imagePath,
            long disableImageId, String disableImagePath
    ) {
        SocialAbilitiesBean bean = new SocialAbilitiesBean();
        bean.setUserId(userId);
        bean.setSkillId(skillId);
        bean.setSkillName(skillName);
        bean.setSkillDescription(skillDescription);
        bean.setSkillType(skillType);
        bean.setFactor(factor);
        bean.setPoint(point);
        bean.setImageId(imageId);
        bean.setImageUrl(imagePath);
        bean.setDisableImageId(disableImageId);
        bean.setDisableImagePath(disableImagePath);
        return bean;
    }

    public List<SocialAbilitiesBean> getUserAllTypeAbilities(long userId) {
        logger.info("get user {} 's social abilities", userId);
        List<SocialAbilitiesBean> socialAbilities = new ArrayList<SocialAbilitiesBean>();

        List<NurseIntegrationBean> allIntegration = integrationService.getIntegrationSorted(userId, UserType.NURSE, CommonStatus.ENABLED);

        Map<Integer, SkillBean>   skillId2Bean   = skillService.getAllEnableSkillId2BeanMap();
        List<NurseSkillBean>      nurseSkills    = nurseSkillService.getAllSkills(userId);

        NurseHospitalRelationBean nurseHospDepart= nurseHospitalService.getRelationByNurseId(userId, "");

        // department social ability
        SocialAbilitiesBean departmentAbility = getUserDepartment(allIntegration, UserType.NURSE, userId);
        if (null!=departmentAbility) {
            socialAbilities.add(departmentAbility);
        }

        // skill social abilities
        List<SocialAbilitiesBean> nurseSkillAbilities;
        nurseSkillAbilities = getUserSkillAbility(allIntegration, UserType.NURSE, userId);

        // add nurse speak abilities(smug/cathart/ask_question)
        List<SpecificSocialAbility> specialAbilities = speakTypeConverter.getItems();
        nurseSkillAbilities = getUserAbility(specialAbilities, allIntegration, nurseSkillAbilities, UserType.NURSE, userId);

        // add thumbs up
        specialAbilities = thumbsUpTypeConverter.getItems();
        nurseSkillAbilities = getUserAbility(specialAbilities, allIntegration, nurseSkillAbilities, UserType.NURSE, userId);

        // add comment made by user
        specialAbilities = commentTypeConverter.getItems();
        nurseSkillAbilities = getUserAbility(specialAbilities, allIntegration, nurseSkillAbilities, UserType.NURSE, userId);

        // sort skill social abilities by Point
        SocialAbilitiesBean[] arrNurseSkillAbilities = nurseSkillAbilities.toArray(new SocialAbilitiesBean[nurseSkillAbilities.size()]);
        Arrays.sort(arrNurseSkillAbilities, new Comparator<SocialAbilitiesBean>() {
            @Override public int compare(SocialAbilitiesBean o1, SocialAbilitiesBean o2) {
                return o2.getPoint() - o1.getPoint();
            }
        });

        // add nurse social ability together
        for (SocialAbilitiesBean nurseSocialAbility : arrNurseSkillAbilities) {
            socialAbilities.add(nurseSocialAbility);
        }

        logger.info("get user {} 's social abilities is {}", userId, socialAbilities);
        return socialAbilities;
    }

    private SocialAbilitiesBean getUserDepartment(List<NurseIntegrationBean> allIntegration, UserType userType, long userId) {
        NurseHospitalRelationBean nurseHospDepart= nurseHospitalService.getRelationByNurseId(userId, "");
        if (null == nurseHospDepart) {
            return null;
        }
        // department social ability
        HospitalDepartmentBean department = nurseHospDepart.getDepartment();
        HospitalDepartmentBean parentDepart = nurseHospDepart.getParentDepart();
        if (null==department && null==parentDepart) {
            return null;
        }
        department = null!=department ? department : parentDepart;
        int point = integrationService.getIntegration(allIntegration, UserType.NURSE, userId, SocialAbilityType.OCCUPATION, nurseHospDepart.getDepartmentId());
        SocialAbilitiesBean abilityBean = newAbilityBean(
                userId,
                nurseHospDepart.getDepartmentId(), department.getName(), department.getDescription(), SocialAbilityType.OCCUPATION,
                1, point,
                department.getImageId(), department.getImageUrl(),
                department.getDisableImageId(), department.getDisableImageUrl());
        abilityBean.setFetchTime(nurseHospDepart.getTime());
        if (VerifyUtil.isStringEmpty(abilityBean.getImageUrl())) {
            if (null != parentDepart && !VerifyUtil.isStringEmpty(parentDepart.getImageUrl())) {
                abilityBean.setImageUrl(parentDepart.getImageUrl());
                abilityBean.setImageId(parentDepart.getImageId());
            }
        }
        BadgeBean badge = badgeService.getBadgeByPointAndAbilityIdAndType(point, abilityBean.getSkillId(), abilityBean.getSkillType().name());abilityBean.setBadge(badge);
        if (null != badge) {
            abilityBean.setSkillName(badge.getName());
            abilityBean.setImageUrl(badge.getImageUrl());
            abilityBean.setSkillDescription(badge.getDescription());
            Date fetchTime = integrationService.firstTimeFetchPoint(allIntegration, userType, userId, abilityBean.getSkillType(), abilityBean.getSkillId(), badge.getPoint());
            abilityBean.setFetchTime(fetchTime);
        }
        return abilityBean;
    }

    public List<SocialAbilitiesBean> getUserSkillAbility(long userId) {
        List<NurseIntegrationBean> allIntegration = integrationService.getIntegrationSorted(userId, UserType.NURSE, CommonStatus.ENABLED);

        // skill social abilities
        List<SocialAbilitiesBean> nurseSkillAbilities;
        nurseSkillAbilities = getUserSkillAbility(allIntegration, UserType.NURSE, userId);
        return nurseSkillAbilities;
    }

    private List<SocialAbilitiesBean> getUserSkillAbility(List<NurseIntegrationBean> integrations, UserType userType, long userId) {
        Map<Integer, SkillBean>   skillId2Bean   = skillService.getAllSkillId2BeanMap();
        List<NurseSkillBean>      nurseSkills    = nurseSkillService.getAllSkills(userId);

        List<SocialAbilitiesBean> nurseSkillAbilities = new ArrayList<>();
        if (VerifyUtil.isListEmpty(nurseSkills)) {
            return nurseSkillAbilities;
        }

        // skill social abilities
        for (NurseSkillBean nurseSkill : nurseSkills) {
            SkillBean skill = skillId2Bean.get(nurseSkill.getSkillId());
            if (null==skill) {
                continue;
            }
            if (OccupationSkillStatus.DISABLE.equals(skill.getStatus())) {
                continue;
            }
            int point = integrationService.getIntegration(integrations, userType, userId, SocialAbilityType.SKILL, skill.getId());
            SocialAbilitiesBean abilityBean = newAbilityBean(
                    nurseSkill.getUserId(),
                    skill.getId(), skill.getName(), skill.getDescription(), SocialAbilityType.SKILL,
                    skill.getFactor(), point,
                    skill.getImageId(), skill.getImageUrl(),
                    skill.getDisableImageId(), skill.getDisableImageUrl());
            abilityBean.setFetchTime(nurseSkill.getTime());
            BadgeBean badge = badgeService.getBadgeByPointAndAbilityIdAndType(abilityBean.getPoint(), abilityBean.getSkillId(), abilityBean.getSkillType().name());
            abilityBean.setBadge(badge);
            if (null!=badge) {
                abilityBean.setSkillName(badge.getName());
                abilityBean.setImageUrl(badge.getImageUrl());
                abilityBean.setSkillDescription(badge.getDescription());
                Date fetchTime = integrationService.firstTimeFetchPoint(integrations, userType, userId, abilityBean.getSkillType(), skill.getId(), badge.getPoint());
                abilityBean.setFetchTime(fetchTime);
            }
            nurseSkillAbilities.add(abilityBean);
        }
        return nurseSkillAbilities;
    }

    private List<SocialAbilitiesBean> getUserAbility(List<SpecificSocialAbility> abilities,
                                List<NurseIntegrationBean> integrations,
                                List<SocialAbilitiesBean> retValues,
                                UserType userType, long userId) {
        if (null==retValues) {
            retValues = new ArrayList<>();
        }
        if (VerifyUtil.isListEmpty(abilities) || VerifyUtil.isListEmpty(integrations)) {
            return retValues;
        }
        for (SpecificSocialAbility ability : abilities) {
            int point = integrationService.getIntegration(integrations, userType, userId, ability.getAbilityType(), ability.getAbilityId());
            if (point > 0) {
                SocialAbilitiesBean abilityBean = newAbilityBean(
                        userId,
                        ability.getAbilityId(), ability.getAbilityName(), "", ability.getAbilityType(),
                        ability.getFactor(), point,
                        0, "", /* there is no image */
                        0, ""  /* there is no disable image */);
                BadgeBean badge = badgeService.getBadgeByPointAndAbilityIdAndType(point, ability.getAbilityId(), ability.getAbilityType().name());
                abilityBean.setBadge(badge);
                if (null != badge) {
                    abilityBean.setSkillName(badge.getName());
                    abilityBean.setImageUrl(badge.getImageUrl());
                    abilityBean.setSkillDescription(badge.getDescription());
                    Date fetchTime = integrationService.firstTimeFetchPoint(integrations, userType, userId, ability.getAbilityType(), ability.getAbilityId(), badge.getPoint());
                    abilityBean.setFetchTime(fetchTime);
                    retValues.add(abilityBean);
                }
            }
        }
        return retValues;
    }

    public SocialAbilitiesBean getUserSpecialAbility(long userId, int abilityId, String type){
        logger.info("get user {} 's special social abilities id={} type={}", userId, abilityId, type);
        // is nurse exist
        if (nurseService.existNurse(userId)) {
            logger.error("nurse not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        // is type exist
        SocialAbilityType socialAbilityType = SocialAbilityType.parseString(type);
        if (null==socialAbilityType) {
            logger.error("the skill type is not exist");
            return null;
        }

        List<NurseIntegrationBean> allIntegration = integrationService.getIntegrationSorted(userId, UserType.NURSE, CommonStatus.ENABLED);
        List<SpecificSocialAbility> otherAbility = new ArrayList<>();
        if (SocialAbilityType.OCCUPATION==socialAbilityType) {
            return  getUserDepartment(allIntegration, UserType.NURSE, userId);
        }
        else if (SocialAbilityType.SKILL==socialAbilityType) {
            List<SocialAbilitiesBean> skillAbilities = getUserSkillAbility(allIntegration, UserType.NURSE, userId);
            for (SocialAbilitiesBean skillAbility : skillAbilities) {
                if (skillAbility.getSkillId() == abilityId) {
                    return skillAbility;
                }
            }
            return null;
        }
        else if (SocialAbilityType.COMMUNITY==socialAbilityType) {
            SpecificSocialAbility ability = speakTypeConverter.getItem(abilityId);
            otherAbility.add(ability);
            List<SocialAbilitiesBean> retVal = getUserAbility(otherAbility, allIntegration, null, UserType.NURSE, userId);
            return VerifyUtil.isListEmpty(retVal) ? null : retVal.get(0);
        }
        else if (SocialAbilityType.THUMBS_UP==socialAbilityType) {
            SpecificSocialAbility ability = thumbsUpTypeConverter.getItem(abilityId);
            otherAbility.add(ability);
            List<SocialAbilitiesBean> retVal = getUserAbility(otherAbility, allIntegration, null, UserType.NURSE, userId);
            return VerifyUtil.isListEmpty(retVal) ? null : retVal.get(0);
        }
        else if (SocialAbilityType.COMMENT==socialAbilityType) {
            SpecificSocialAbility ability = commentTypeConverter.getItem(abilityId);
            otherAbility.add(ability);
            List<SocialAbilitiesBean> retVal = getUserAbility(otherAbility, allIntegration, null, UserType.NURSE, userId);
            return VerifyUtil.isListEmpty(retVal) ? null : retVal.get(0);
        }

        return null;
    }

    //===========================================================================
    //          nominate/un_nominate skill
    //===========================================================================

    public SocialAbilitiesBean nominateSocialAbility(long userId, long friendId, int friendSkillId, String skillType) {
        logger.info("user {} nominate friend={} skill={} type={}", userId, friendId, friendSkillId, skillType);
        long nominateCount = nominationService.nominateNurseSkill(userId, friendId, friendSkillId, skillType);
        logger.info("user {} nominate friend={} skill={} type={} nominateCount={}", userId, friendId, friendSkillId, skillType, nominateCount);
        return getUserSpecialAbility(friendId, friendSkillId, skillType);
    }

    //=============================================================================
    //        delete
    //=============================================================================

    public String deleteSpecialSocialAbilities(String skillIds, String skillType) {
        String ids = nominationService.deleteBySkillIdsAndType(skillIds, skillType);
        return ids+"_"+skillType;
    }
}
