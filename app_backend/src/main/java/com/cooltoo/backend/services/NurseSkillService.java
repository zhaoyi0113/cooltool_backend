package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSkillBean;
import com.cooltoo.backend.beans.SkillBean;
import com.cooltoo.backend.converter.NurseSkillBeanConverter;
import com.cooltoo.backend.entities.NurseSkillEntity;
import com.cooltoo.backend.repository.NurseSkillRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/3/25.
 */
@Service("NurseSkillService")
public class NurseSkillService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSkillService.class.getName());
    private static final Sort   sort   = new Sort(new Sort.Order(Sort.Direction.ASC, "skillId"));

    @Autowired private NurseSkillRepository    repository;
    @Autowired private NurseService            nurseService;
    @Autowired private SkillService            skillService;
    @Autowired private NurseSkillBeanConverter beanConverter;


    //===============================================================
    //           get nurse skill
    //===============================================================

    public List<NurseSkillBean> getAllSkills(long userId) {
        List<NurseSkillBean>   nurseSkillsB = new ArrayList<NurseSkillBean>();
        List<NurseSkillEntity> nurseSkillsE = repository.findByUserId(userId, sort);
        for (NurseSkillEntity nurseSkillE : nurseSkillsE) {
            NurseSkillBean nurseSkillB = beanConverter.convert(nurseSkillE);
            nurseSkillsB.add(nurseSkillB);
        }
        return nurseSkillsB;
    }

    public NurseSkillBean getSkill(long userId, int occupationSkillId) {
        NurseSkillEntity nurseSkillE = repository.findByUserIdAndSkillId(userId, occupationSkillId);
        if (null==nurseSkillE) {
            return null;
        }

        NurseSkillBean nurseSkillB = beanConverter.convert(nurseSkillE);
        return nurseSkillB;
    }


    //===============================================================
    //           add/delete nurse skill
    //===============================================================

    private NurseSkillEntity newNurseSkillEntity(long userId, int skillId, int point) {
        NurseSkillEntity newSkill = new NurseSkillEntity();
        newSkill.setUserId(userId);
        newSkill.setSkillId(skillId);
        newSkill.setPoint(point);
        newSkill.setStatus(CommonStatus.ENABLED);
        newSkill.setTime(new Date());
        return newSkill;
    }

    @Transactional
    public void addSkill(long userId, int skillId) {
        // is Nurse exist
        nurseService.getNurseWithoutOtherInfo(userId);
        // is Occupation skill exist
        skillService.getOneSkillById(skillId);
        // is Skill exist already, delete it
        NurseSkillEntity skillExist = repository.findByUserIdAndSkillId(userId, skillId);
        if (null!=skillExist) {
            repository.delete(skillExist);
        }
        else {
            NurseSkillEntity newSkill = newNurseSkillEntity(userId, skillId, 0);
            repository.save(newSkill);
        }
    }

    @Transactional
    public void setSkills(long userId, String skillIds) {
        if (!VerifyUtil.isIds(skillIds)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        // is Nurse exist
        if (!nurseService.existNurse(userId)) {
            logger.info("user not exist");
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }

        // judge the skills exist
        Map<Integer, SkillBean> allSkills = skillService.getAllEnableSkillId2BeanMap();
        List<Integer> ids = VerifyUtil.parseIntIds(skillIds);
        for (Integer iId : ids) {
            if (!allSkills.containsKey(iId)) {
                logger.error("The occupation skill {} is not exist!", iId);
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }

        List<NurseSkillEntity> skillExist = repository.findByUserId(userId);
        // user has skill already
        if (!VerifyUtil.isListEmpty(skillExist)) {
            List<NurseSkillEntity> skillNeedRemove = new ArrayList<>();
            for (NurseSkillEntity skill : skillExist) {
                // new skills not contains skill, need remove
                if (!ids.contains(skill.getSkillId())) {
                    skillNeedRemove.add(skill);
                }
                // contains skill, need not add
                else {
                    ids.remove(new Integer(skill.getSkillId()));
                }
            }
            // remove the skill not set
            if (!VerifyUtil.isListEmpty(skillNeedRemove)) {
                repository.delete(skillNeedRemove);
            }
        }

        for (Integer skillId : ids) {
            NurseSkillEntity newSkill = newNurseSkillEntity(userId, skillId, 0);
            repository.save(newSkill);
        }
    }

    //===============================================================
    //           delete nurse skill
    //===============================================================

    @Transactional
    public String removeSkillByUserIdAndSkillIds(long userId, String skillIds) {
        logger.info("remove nurse {}'s skill by skill ids = {}", userId, skillIds);
        if (!VerifyUtil.isIds(skillIds)) {
            logger.error("skill ids format is wrong!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<Integer> arrSkillIds = VerifyUtil.parseIntIds(skillIds);
        repository.deleteByUserIdAndSkillIdIn(userId, arrSkillIds);
        return skillIds;
    }
}
