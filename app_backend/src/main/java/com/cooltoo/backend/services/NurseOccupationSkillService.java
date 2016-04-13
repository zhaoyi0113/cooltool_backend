package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseOccupationSkillBean;
import com.cooltoo.backend.beans.OccupationSkillBean;
import com.cooltoo.backend.converter.NurseOccupationSkillBeanConverter;
import com.cooltoo.backend.entities.NurseOccupationSkillEntity;
import com.cooltoo.backend.repository.NurseOccupationSkillRepository;
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
@Service("NurseOccupationSkillService")
public class NurseOccupationSkillService {

    private static final Logger logger = LoggerFactory.getLogger(NurseOccupationSkillService.class.getName());
    private static final Sort   sort   = new Sort(new Sort.Order(Sort.Direction.DESC, "point"));

    @Autowired private NurseOccupationSkillRepository    repository;
    @Autowired private NurseService                      nurseService;
    @Autowired private OccupationSkillService            skillService;
    @Autowired private NurseOccupationSkillBeanConverter beanConverter;


    //===============================================================
    //           get nurse skill
    //===============================================================

    public List<NurseOccupationSkillBean> getAllSkills(long userId) {
        List<NurseOccupationSkillBean>   nurseSkillsB = new ArrayList<NurseOccupationSkillBean>();
        List<NurseOccupationSkillEntity> nurseSkillsE = repository.findByUserId(userId, sort);
        for (NurseOccupationSkillEntity nurseSkillE : nurseSkillsE) {
            NurseOccupationSkillBean nurseSkillB = beanConverter.convert(nurseSkillE);
            nurseSkillsB.add(nurseSkillB);
        }
        return nurseSkillsB;
    }

    public NurseOccupationSkillBean getSkill(long userId, int occupationSkillId) {
        NurseOccupationSkillEntity nurseSkillE = repository.findByUserIdAndSkillId(userId, occupationSkillId);
        if (null==nurseSkillE) {
            return null;
        }

        NurseOccupationSkillBean nurseSkillB = beanConverter.convert(nurseSkillE);
        return nurseSkillB;
    }


    //===============================================================
    //           add/delete nurse skill
    //===============================================================

    private NurseOccupationSkillEntity newNurseSkillEntity(long userId, int skillId, int point) {
        NurseOccupationSkillEntity newSkill = new NurseOccupationSkillEntity();
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
        NurseOccupationSkillEntity skillExist = repository.findByUserIdAndSkillId(userId, skillId);
        if (null!=skillExist) {
            repository.delete(skillExist);
        }
        else {
            NurseOccupationSkillEntity newSkill = newNurseSkillEntity(userId, skillId, 0);
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
        Map<Integer, OccupationSkillBean> allSkills = skillService.getAllSkillId2BeanMap();
        List<Integer> ids    = new ArrayList<Integer>();
        String[]      idsStr = skillIds.split(",");
        for (String id : idsStr) {
            int iId = Integer.parseInt(id);
            OccupationSkillBean skillB = allSkills.get(iId);
            if (!allSkills.containsKey(iId)) {
                logger.error("The occupation skill {} is not exist!", iId);
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
            ids.add(iId);
        }

        for (Integer skillId : ids) {
            // Skill is exist already, delete it;
            NurseOccupationSkillEntity skillExist = repository.findByUserIdAndSkillId(userId, skillId);
            if (null != skillExist) {
                repository.delete(skillExist);
            } else {
                NurseOccupationSkillEntity newSkill = newNurseSkillEntity(userId, skillId, 0);
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
