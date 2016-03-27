package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseOccupationSkillBean;
import com.cooltoo.backend.beans.OccupationSkillBean;
import com.cooltoo.backend.converter.NurseOccupationSkillBeanConverter;
import com.cooltoo.backend.entities.NurseOccupationSkillEntity;
import com.cooltoo.backend.repository.NurseOccupationSkillRepository;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 16/3/25.
 */
@Service("NurseOccupationSkillService")
public class NurseOccupationSkillService {

    @Autowired
    private NurseOccupationSkillRepository nurseSkillRepository;

    @Autowired
    private NurseService nurseService;

    @Autowired
    private OccupationSkillService skillService;

    @Autowired
    private NurseOccupationSkillBeanConverter beanConverter;

    private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "point"));

    public List<NurseOccupationSkillBean> getAllSkills(long userId) {
        List<NurseOccupationSkillEntity> entities = nurseSkillRepository.findSkillRelationByUserId(userId, sort);
        List<NurseOccupationSkillBean> skills = new ArrayList<NurseOccupationSkillBean>();
        for (NurseOccupationSkillEntity skill : entities) {
            skills.add(beanConverter.convert(skill));
        }
        return skills;
    }

    public NurseOccupationSkillBean getSkill(long userId, int occupationSkillId) {
        NurseOccupationSkillEntity skill = nurseSkillRepository.findSkillRelationByUserIdAndSkillId(userId, occupationSkillId);
        if (null==skill) {
            return null;
        }
        return beanConverter.convert(skill);
    }

    @Transactional
    public void addSkill(long userId, int occupationSkillId) {
        // is Nurse exist
        nurseService.getNurse(userId);
        // is Occupation skill exist
        skillService.getOccupationSkill(occupationSkillId);
        // is Skill exist already
        NurseOccupationSkillBean skillExist = getSkill(userId, occupationSkillId);
        if (null!=skillExist) {
            throw new BadRequestException(ErrorCode.SKILL_EXIST);
        }
        NurseOccupationSkillEntity newSkill = new NurseOccupationSkillEntity();
        newSkill.setUserId(userId);
        newSkill.setSkillId(occupationSkillId);
        newSkill.setPoint(0);
        nurseSkillRepository.save(newSkill);
    }


    @Transactional
    public void removeSkill(long userId, int skillId) {
        NurseOccupationSkillEntity skill = nurseSkillRepository.findSkillRelationByUserIdAndSkillId(userId, skillId);
        if (null==skill) {
            throw new BadRequestException(ErrorCode.SKILL_NOT_EXIST);
        }

        nurseSkillRepository.delete(skill);
    }


    @Transactional
    public void removeSkill(int id) {
        nurseSkillRepository.delete(id);
    }

}
