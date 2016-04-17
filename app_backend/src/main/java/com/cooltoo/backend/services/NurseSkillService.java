package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSkillBean;
import com.cooltoo.backend.beans.SkillBean;
import com.cooltoo.backend.converter.NurseSkillBeanConverter;
import com.cooltoo.backend.entities.NurseSkillEntity;
import com.cooltoo.backend.repository.NurseSkillRepository;
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
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/3/25.
 */
@Service("NurseSkillService")
public class NurseSkillService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSkillService.class.getName());
    private static final Sort   sort   = new Sort(new Sort.Order(Sort.Direction.DESC, "point"));

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
        return newSkill;
    }

    @Transactional
    public void addSkill(long userId, int skillId) {
        // is Nurse exist
        nurseService.getNurse(userId);
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
    public void addSkills(long userId, String skillIds) {
        if (!VerifyUtil.isIds(skillIds)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        // is Nurse exist
        nurseService.getNurse(userId);

        // judge the skills exist
        Map<Integer, SkillBean> allSkills = skillService.getAllSkillId2BeanMap();
        List<Integer> ids    = new ArrayList<Integer>();
        String[]      idsStr = skillIds.split(",");
        for (String id : idsStr) {
            int iId = Integer.parseInt(id);
            SkillBean skillB = allSkills.get(iId);
            if (!allSkills.containsKey(iId)) {
                logger.error("The occupation skill {} is not exist!", iId);
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
            ids.add(iId);
        }

        for (Integer skillId : ids) {
            // Skill is exist already, delete it;
            NurseSkillEntity skillExist = repository.findByUserIdAndSkillId(userId, skillId);
            if (null != skillExist) {
                repository.delete(skillExist);
            } else {
                NurseSkillEntity newSkill = newNurseSkillEntity(userId, skillId, 0);
                repository.save(newSkill);
            }
        }
    }

    //===============================================================
    //           delete nurse skill
    //===============================================================

    @Transactional
    public String removeSkillByUserIds(String userIds) {
        logger.info("remove nurse skill by user ids = {}", userIds);
        if (!VerifyUtil.isIds(userIds)) {
            logger.error("user ids format is wrong!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<Long> arrUserIds = new ArrayList<>();
        String[]   strUserIds = userIds.split(",");
        for (String strUserId : strUserIds) {
            long lUserId = Long.parseLong(strUserId);
            arrUserIds.add(lUserId);
        }
        repository.deleteByUserIdIn(arrUserIds);
        return userIds;
    }

    @Transactional
    public String removeSkillBySkillIds(String skillIds) {
        logger.info("remove nurse skill by skill ids = {}", skillIds);
        if (!VerifyUtil.isIds(skillIds)) {
            logger.error("skill ids format is wrong!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<Integer> arrSkillIds = new ArrayList<>();
        String[]      strSkillIds = skillIds.split(",");
        for (String strSkillId : strSkillIds) {
            int lSkillId = Integer.parseInt(strSkillId);
            arrSkillIds.add(lSkillId);
        }
        repository.deleteBySkillIdIn(arrSkillIds);
        return skillIds;
    }
}
