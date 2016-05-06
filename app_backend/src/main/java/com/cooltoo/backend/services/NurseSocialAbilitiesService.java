package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.*;
import com.cooltoo.beans.BadgeBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.OccupationSkillStatus;
import com.cooltoo.constants.SocialAbilityType;
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
    @Autowired private NurseHospitalRelationService  nurseHospitalService;
    @Autowired private BadgeService                  badgeService;

    //==========================================================================
    //           get all type
    //==========================================================================
    public List<String> getAllSkillTypes() {
        return SocialAbilityType.getAllValues();
    }

    //=================================================================
    //          get
    //==================================================================
    private SocialAbilitiesBean newAbilityBean(
            long userId,
            int skillId, String skillName, SocialAbilityType skillType,
            long factor, long nominateCount,
            long imageId, String imagePath,
            long disableImageId, String disableImagePath
    ) {
        SocialAbilitiesBean bean = new SocialAbilitiesBean();
        bean.setUserId(userId);
        bean.setSkillId(skillId);
        bean.setSkillName(skillName);
        bean.setSkillType(skillType);
        bean.setFactor(factor);
        bean.setNominatedCount(nominateCount);
        bean.setPoint((int)factor*(int)nominateCount);
        bean.setImageId(imageId);
        bean.setImageUrl(imagePath);
        bean.setDisableImageId(disableImageId);
        bean.setDisableImagePath(disableImagePath);
        return bean;
    }

    public List<SocialAbilitiesBean> getUserAllTypeAbilities(long userId) {
        logger.info("get user {} 's social abilities", userId);
        List<SocialAbilitiesBean> socialAbilities = new ArrayList<SocialAbilitiesBean>();

        Map<Integer, SkillBean>          skillId2Bean   = skillService.getAllSkillId2BeanMap();
        List<NurseSkillBean>             nurseSkills    = nurseSkillService.getAllSkills(userId);
        List<NurseAbilityNominationBean> abilityNominate= nominationService.getAllTypeNominated(userId);
        NurseHospitalRelationBean        nurseHospDepart= nurseHospitalService.getRelationByNurseId(userId);
        List<SpeakTypeBean>              speakTypes     = speakTypeService.getAllSpeakType();

        // department social ability
        if (null!=nurseHospDepart) {
            HospitalDepartmentBean department = nurseHospDepart.getDepartment();
            if (null!=nurseHospDepart.getDepartment()) {
                boolean hasDepartmentNominate = false;
                for (NurseAbilityNominationBean nomination : abilityNominate) {
                    if (nomination.getAbilityType() != SocialAbilityType.OCCUPATION) {
                        continue;
                    }
                    if (nomination.getAbilityId() != nurseHospDepart.getDepartmentId()) {
                        continue;
                    }
                    SocialAbilitiesBean abilityBean = newAbilityBean(
                            userId,
                            nomination.getAbilityId(), department.getName(), SocialAbilityType.OCCUPATION,
                            1, nomination.getAbilityNominateCount(),
                            department.getImageId(), department.getImageUrl(),
                            department.getDisableImageId(), department.getDisableImageUrl());
                    socialAbilities.add(abilityBean);
                    hasDepartmentNominate = true;
                }
                if (!hasDepartmentNominate) {
                    SocialAbilitiesBean abilityBean = newAbilityBean(
                            userId,
                            nurseHospDepart.getDepartmentId(), department.getName(), SocialAbilityType.OCCUPATION,
                            1, 0,
                            department.getImageId(), department.getImageUrl(),
                            department.getDisableImageId(), department.getDisableImageUrl());
                    socialAbilities.add(abilityBean);
                }
            }
        }
        // skill social abilities
        List<SocialAbilitiesBean> nurseSkillAbilities = new ArrayList<>();
        for (NurseSkillBean nurseSkill : nurseSkills) {
            SkillBean skill = skillId2Bean.get(nurseSkill.getSkillId());
            if (null==skill) {
                continue;
            }
            if (OccupationSkillStatus.DISABLE.equals(skill.getStatus())) {
                continue;
            }
            boolean hasNominate = false;
            for (NurseAbilityNominationBean nomination : abilityNominate) {
                if (nomination.getAbilityType()!=nurseSkill.getType()) {
                    continue;
                }
                if (nomination.getAbilityId()!=nurseSkill.getSkillId()) {
                    continue;
                }
                if (nomination.getUserId()!=nurseSkill.getUserId()) {
                    continue;
                }
                SocialAbilitiesBean abilityBean = newAbilityBean(
                        nurseSkill.getUserId(),
                        nomination.getAbilityId(), skill.getName(), SocialAbilityType.SKILL,
                        skill.getFactor(), nomination.getAbilityNominateCount(),
                        skill.getImageId(), skill.getImageUrl(),
                        skill.getDisableImageId(), skill.getDisableImageUrl());
                nurseSkillAbilities.add(abilityBean);
                hasNominate = true;
            }
            if (!hasNominate) {
                SocialAbilitiesBean abilityBean = newAbilityBean(
                        nurseSkill.getUserId(),
                        nurseSkill.getSkillId(), skill.getName(), SocialAbilityType.SKILL,
                        skill.getFactor(), 0,
                        skill.getImageId(), skill.getImageUrl(),
                        skill.getDisableImageId(), skill.getDisableImageUrl());
                BadgeBean badge = badgeService.getBadgeByPointAndAbilityIdAndType(abilityBean.getPoint(), abilityBean.getSkillId(), SocialAbilityType.SKILL.name());
                abilityBean.setBadge(badge);
                if (null!=badge) {
                    abilityBean.setSkillName(badge.getName());
                    abilityBean.setImageUrl(badge.getImageUrl());
                }
                nurseSkillAbilities.add(abilityBean);
            }
        }

        // add nurse speak abilities(smug/cathart/ask_question)
        for (SpeakTypeBean speakType : speakTypes) {
            long countOfSpeakType = speakService.countSpeak(true, userId, speakType.getType().name());
            if (countOfSpeakType>0) {
                SocialAbilitiesBean abilityBean = newAbilityBean(
                        userId,
                        speakType.getId(), speakType.getName(), SocialAbilityType.COMMUNITY,
                        speakType.getFactor(), countOfSpeakType,
                        speakType.getImageId(), speakType.getImageUrl(),
                        speakType.getDisableImageId(), speakType.getDisableImageUrl());
                BadgeBean badge = badgeService.getBadgeByPointAndAbilityIdAndType(abilityBean.getPoint(), abilityBean.getSkillId(), SocialAbilityType.COMMUNITY.name());
                abilityBean.setBadge(badge);
                if (null!=badge) {
                    abilityBean.setSkillName(badge.getName());
                    abilityBean.setImageUrl(badge.getImageUrl());
                }
                nurseSkillAbilities.add(abilityBean);
            }
        }
        // add thumbs up user count
        long countThumbsUpUser   = thumbsUpService.countOthersThumbsUpUser(userId);
        if (countThumbsUpUser>0) {
            SocialAbilitiesBean abilityBean = newAbilityBean(
                    userId,
                    0, "", SocialAbilityType.THUMBS_UP_ME,
                    1, countThumbsUpUser,
                    0, "",
                    0, "");
            BadgeBean badge = badgeService.getBadgeByPointAndAbilityIdAndType(abilityBean.getPoint(), abilityBean.getSkillId(), SocialAbilityType.THUMBS_UP_ME.name());
            abilityBean.setBadge(badge);
            if (null!=badge) {
                abilityBean.setSkillName(badge.getName());
                abilityBean.setImageUrl(badge.getImageUrl());
            }
            nurseSkillAbilities.add(abilityBean);
        }
        // add thumbs up others count
        long countThumbsUpOthers = thumbsUpService.countUserThumbsUpOthers(userId);
        if (countThumbsUpOthers>0) {
            SocialAbilitiesBean abilityBean = newAbilityBean(
                    userId,
                    0, "", SocialAbilityType.THUMBS_UP_OTHERS,
                    1, countThumbsUpOthers,
                    0, "",
                    0, "");
            BadgeBean badge = badgeService.getBadgeByPointAndAbilityIdAndType(abilityBean.getPoint(), abilityBean.getSkillId(), SocialAbilityType.THUMBS_UP_OTHERS.name());
            abilityBean.setBadge(badge);
            if (null!=badge) {
                abilityBean.setSkillName(badge.getName());
                abilityBean.setImageUrl(badge.getImageUrl());
            }
            nurseSkillAbilities.add(abilityBean);
        }
        // add comment made by user
        long countCommentMade = commentService.countCommentUserMake(userId);
        if (countCommentMade>0) {
            SocialAbilitiesBean abilityBean = newAbilityBean(
                    userId,
                    0, "", SocialAbilityType.COMMENT_MADE,
                    1, countCommentMade,
                    0, "",
                    0, "");
            BadgeBean badge = badgeService.getBadgeByPointAndAbilityIdAndType(abilityBean.getPoint(), abilityBean.getSkillId(), SocialAbilityType.COMMENT_MADE.name());
            abilityBean.setBadge(badge);
            if (null!=badge) {
                abilityBean.setSkillName(badge.getName());
                abilityBean.setImageUrl(badge.getImageUrl());
            }
            nurseSkillAbilities.add(abilityBean);
        }

        // sort skill social abilities by Point
        SocialAbilitiesBean[] arrNurseSkillAbilities = nurseSkillAbilities.toArray(new SocialAbilitiesBean[nurseSkillAbilities.size()]);
        Arrays.sort(arrNurseSkillAbilities, new Comparator<SocialAbilitiesBean>() {
            @Override
            public int compare(SocialAbilitiesBean o1, SocialAbilitiesBean o2) {
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

    public SocialAbilitiesBean getUserSpecialAbility(long userId, int abilityId, String type){
        logger.info("get user {} 's special social abilities id={} type={}", userId, abilityId, type);
        // is nurse exist
        NurseBean         nurse             = nurseService.getNurse(userId);
        // is type exist
        SocialAbilityType socialAbilityType = SocialAbilityType.parseString(type);
        if (null==socialAbilityType) {
            logger.error("the skill type is not exist");
            return null;
        }

        if (SocialAbilityType.OCCUPATION==socialAbilityType) {
            NurseAbilityNominationBean abilityNominate = nominationService.getSpecialAbilityNominated(userId, abilityId, socialAbilityType);
            NurseHospitalRelationBean  nurseHospDepart = nurseHospitalService.getRelationByNurseId(userId);

            // department social ability
            if (null!=nurseHospDepart) {
                HospitalDepartmentBean department = nurseHospDepart.getDepartment();
                if (null!=department) {
                    int  departId      = nurseHospDepart.getDepartmentId();
                    long nominateCount = null==abilityNominate ? 0 : abilityNominate.getAbilityNominateCount();
                    SocialAbilitiesBean ability = newAbilityBean(
                            userId,
                            departId, department.getName(), SocialAbilityType.OCCUPATION,
                            1, nominateCount,
                            department.getImageId(), department.getImageUrl(),
                            department.getDisableImageId(), department.getDisableImageUrl());
                    return ability;
                }
            }
        }
        else if (SocialAbilityType.SKILL==socialAbilityType) {

            Map<Integer, SkillBean>    skillId2Bean    = skillService.getAllSkillId2BeanMap();
            NurseAbilityNominationBean abilityNominate = nominationService.getSpecialAbilityNominated(userId, abilityId, socialAbilityType);

            // skill social ability
            SkillBean skill         = skillId2Bean.get(abilityId);
            long      nominateCount = null==abilityNominate ? 0 : abilityNominate.getAbilityNominateCount();
            SocialAbilitiesBean abilityBean = newAbilityBean(
                    userId,
                    abilityId, skill.getName(), SocialAbilityType.OCCUPATION,
                    skill.getFactor(), nominateCount,
                    skill.getImageId(), skill.getImageUrl(),
                    skill.getDisableImageId(), skill.getDisableImageUrl());
            BadgeBean badge = badgeService.getBadgeByPointAndAbilityIdAndType(abilityBean.getPoint(), abilityBean.getSkillId(), SocialAbilityType.SKILL.name());
            abilityBean.setBadge(badge);
            return abilityBean;
        }
        else if (SocialAbilityType.COMMUNITY==socialAbilityType) {
            SpeakTypeBean speakType        = speakTypeService.getSpeakType(abilityId);
            long          countOfSpeakType = speakService.countSpeak(true, userId, speakType.getType().name());
            // speak social ability
            if (countOfSpeakType>0) {
                SocialAbilitiesBean abilityBean = newAbilityBean(
                        userId,
                        speakType.getId(), speakType.getName(), SocialAbilityType.COMMUNITY,
                        speakType.getFactor(), countOfSpeakType,
                        speakType.getImageId(), speakType.getImageUrl(),
                        speakType.getDisableImageId(), speakType.getDisableImageUrl());
                BadgeBean badge = badgeService.getBadgeByPointAndAbilityIdAndType(abilityBean.getPoint(), abilityBean.getSkillId(), SocialAbilityType.COMMUNITY.name());
                abilityBean.setBadge(badge);
                return abilityBean;
            }
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
