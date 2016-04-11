package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.*;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.OccupationSkillType;
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

    @Autowired private NurseService                 nurseService;
    @Autowired private OccupationSkillService       skillService;
    @Autowired private NurseOccupationSkillService  nurseSkillService;
    @Autowired private NurseSkillNominationService  nominationService;
    @Autowired private NurseHospitalRelationService nurseDepartmentService;

    //==========================================================================
    //           get all type
    //==========================================================================
    public List<String> getAllSkillTypes() {
        return OccupationSkillType.getAllValues();
    }

    //=================================================================
    //          get
    //==================================================================
    private SocialAbilitiesBean newAbilityBean(
            long userId,
            int skillId, String skillName, OccupationSkillType skillType,
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

    public List<SocialAbilitiesBean> getUserAllTypeAbilites(long userId) {
        logger.info("get user {} 's social abilities", userId);
        List<SocialAbilitiesBean> socialAbilities = new ArrayList<SocialAbilitiesBean>();

        Map<Integer, OccupationSkillBean> skillId2Bean   = skillService.getAllSkillId2BeanMap();
        List<NurseOccupationSkillBean>    nurseSkills    = nurseSkillService.getAllSkills(userId);
        List<NurseSkillNominationBean>    skillNominate  = nominationService.getAllTypeNominated(userId);
        NurseHospitalRelationBean         nurseHospDepart= nurseDepartmentService.getRelationByNurseId(userId);

        // department social ability
        if (null!=nurseHospDepart) {
            HospitalDepartmentBean department = nurseHospDepart.getDepartment();
            if (null!=nurseHospDepart.getDepartment()) {
                boolean hasDepartmentNominate = false;
                for (NurseSkillNominationBean nomination : skillNominate) {
                    if (nomination.getSkillType() != OccupationSkillType.OCCUPATION) {
                        continue;
                    }
                    if (nomination.getSkillId() != nurseHospDepart.getDepartmentId()) {
                        continue;
                    }
                    SocialAbilitiesBean abilityBean = newAbilityBean(
                            userId,
                            nomination.getSkillId(), department.getName(), OccupationSkillType.OCCUPATION,
                            1, nomination.getSkillNominateCount(),
                            department.getImageId(), department.getImageUrl(),
                            department.getDisableImageId(), department.getDisableImageUrl());
                    socialAbilities.add(abilityBean);
                    hasDepartmentNominate = true;
                }
                if (!hasDepartmentNominate) {
                    SocialAbilitiesBean abilityBean = newAbilityBean(
                            userId,
                            nurseHospDepart.getDepartmentId(), department.getName(), OccupationSkillType.OCCUPATION,
                            1, 0,
                            department.getImageId(), department.getImageUrl(),
                            department.getDisableImageId(), department.getDisableImageUrl());
                    socialAbilities.add(abilityBean);
                }
            }
        }
        // skill social abilities
        List<SocialAbilitiesBean> nurseSkillAbilities = new ArrayList<>();
        for (NurseOccupationSkillBean nurseSkill : nurseSkills) {
            boolean hasNominate = false;
            for (NurseSkillNominationBean nomination : skillNominate) {
                if (nomination.getSkillType()!=nurseSkill.getType()) {
                    continue;
                }
                if (nomination.getSkillId()!=nurseSkill.getSkillId()) {
                    continue;
                }
                if (nomination.getUserId()!=nurseSkill.getUserId()) {
                    continue;
                }
                OccupationSkillBean skill = skillId2Bean.get(nurseSkill.getSkillId());
                SocialAbilitiesBean abilityBean = newAbilityBean(
                        nurseSkill.getUserId(),
                        nomination.getSkillId(), skill.getName(), OccupationSkillType.SKILL,
                        skill.getFactor(), nomination.getSkillNominateCount(),
                        skill.getImageId(), skill.getImageUrl(),
                        skill.getDisableImageId(), skill.getDisableImageUrl());
                nurseSkillAbilities.add(abilityBean);
                hasNominate = true;
            }
            if (!hasNominate) {
                OccupationSkillBean skill = skillId2Bean.get(nurseSkill.getSkillId());
                SocialAbilitiesBean abilityBean = newAbilityBean(
                        nurseSkill.getUserId(),
                        nurseSkill.getSkillId(), skill.getName(), OccupationSkillType.SKILL,
                        skill.getFactor(), 0,
                        skill.getImageId(), skill.getImageUrl(),
                        skill.getDisableImageId(), skill.getDisableImageUrl());
                nurseSkillAbilities.add(abilityBean);
            }
        }
        // sort skill social abilities by Point
        SocialAbilitiesBean[] arrNurseSkillAbilities = nurseSkillAbilities.toArray(new SocialAbilitiesBean[nurseSkillAbilities.size()]);
        Arrays.sort(arrNurseSkillAbilities, new Comparator<SocialAbilitiesBean>() {
            @Override
            public int compare(SocialAbilitiesBean o1, SocialAbilitiesBean o2) {
                return o2.getPoint() - o1.getPoint();
            }
        });
        for (SocialAbilitiesBean nurseSocialAbility : arrNurseSkillAbilities) {
            socialAbilities.add(nurseSocialAbility);
        }

        logger.info("get user {} 's social abilities is {}", userId, socialAbilities);
        return socialAbilities;
    }

    public SocialAbilitiesBean getUserSpecialAbility(long userId, int skillId, String type){
        logger.info("get user {} 's special social abilities skill id={} type={}", userId, skillId, type);
        // is nurse exist
        NurseBean                 nurse           = nurseService.getNurse(userId);
        // is type exist
        OccupationSkillType       socialAbilityType = OccupationSkillType.parseString(type);
        if (null==socialAbilityType) {
            logger.error("the skill type is not exist");
            return null;
        }

        if (OccupationSkillType.OCCUPATION==socialAbilityType) {
            NurseSkillNominationBean  skillNominate  = nominationService.getSpecialTypeSkillNominated(userId, skillId, socialAbilityType);
            NurseHospitalRelationBean nurseHospDepart= nurseDepartmentService.getRelationByNurseId(userId);

            // department social ability
            if (null!=nurseHospDepart) {
                HospitalDepartmentBean department = nurseHospDepart.getDepartment();
                if (null!=department) {
                    int  departId      = nurseHospDepart.getDepartmentId();
                    long nominateCount = null==skillNominate ? 0 : skillNominate.getSkillNominateCount();
                    SocialAbilitiesBean ability = newAbilityBean(
                            userId,
                            departId, department.getName(), OccupationSkillType.OCCUPATION,
                            1, nominateCount,
                            department.getImageId(), department.getImageUrl(),
                            department.getDisableImageId(), department.getDisableImageUrl());
                    return ability;
                }
            }
        }
        else if (OccupationSkillType.SKILL==socialAbilityType) {

            Map<Integer, OccupationSkillBean> skillId2Bean   = skillService.getAllSkillId2BeanMap();
            NurseSkillNominationBean          skillNominate = nominationService.getSpecialTypeSkillNominated(userId, skillId, socialAbilityType);

            // department social ability
            OccupationSkillBean skill         = skillId2Bean.get(skillId);
            long                nominateCount = null==skillNominate ? 0 : skillNominate.getSkillNominateCount();
            SocialAbilitiesBean ability = newAbilityBean(
                    userId,
                    skillId, skill.getName(), OccupationSkillType.OCCUPATION,
                    1, nominateCount,
                    skill.getImageId(), skill.getImageUrl(),
                    skill.getDisableImageId(), skill.getDisableImageUrl());
            return ability;
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

    public String deleteSpecialSocialAbilit(String skillIds, String skillType) {
        String ids = nominationService.deleteBySkillIdsAndType(skillIds, skillType);
        return ids+"_"+skillType;
    }
}
