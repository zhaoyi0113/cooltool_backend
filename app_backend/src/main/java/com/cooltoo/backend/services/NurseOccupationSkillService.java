package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseOccupationSkillBean;
import com.cooltoo.backend.beans.OccupationSkillBean;
import com.cooltoo.backend.converter.NurseOccupationSkillBeanConverter;
import com.cooltoo.backend.entities.NurseOccupationSkillEntity;
import com.cooltoo.backend.repository.NurseOccupationSkillRepository;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.VerifyUtil;
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
        List<OccupationSkillBean> skillsB = skillService.getOccupationSkillList();
        List<NurseOccupationSkillEntity> nurseSkillsE = nurseSkillRepository.findSkillRelationByUserId(userId, sort);
        List<NurseOccupationSkillBean> nurseSkillsB = new ArrayList<NurseOccupationSkillBean>();
        for (NurseOccupationSkillEntity nurseSkillE : nurseSkillsE) {
            NurseOccupationSkillBean
            nurseSkillB = beanConverter.convert(nurseSkillE);
            nurseSkillsB.add(nurseSkillB);

            for (OccupationSkillBean skillB : skillsB) {
                if (skillB.getId()==nurseSkillB.getSkillId()) {
                    nurseSkillB.setSkill(skillB);
                    break;
                }
            }
        }
        return nurseSkillsB;
    }

    public NurseOccupationSkillBean getSkill(long userId, int occupationSkillId) {
        NurseOccupationSkillEntity nurseSkillE = nurseSkillRepository.findSkillRelationByUserIdAndSkillId(userId, occupationSkillId);
        if (null==nurseSkillE) {
            return null;
        }

        NurseOccupationSkillBean nurseSkillB = beanConverter.convert(nurseSkillE);

        OccupationSkillBean skillB = skillService.getOccupationSkill(occupationSkillId);
         if (null!=skillB) {
            nurseSkillB.setSkill(skillB);
        }

        return nurseSkillB;
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
            nurseSkillRepository.delete(skillExist.getId());
        }
        else {
            NurseOccupationSkillEntity newSkill = new NurseOccupationSkillEntity();
            newSkill.setUserId(userId);
            newSkill.setSkillId(occupationSkillId);
            newSkill.setPoint(0);
            nurseSkillRepository.save(newSkill);
        }
    }

    @Transactional
    public void addSkills(long userId, String occupationSkillIds) {
        if (!VerifyUtil.isOccupationSkillIds(occupationSkillIds)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        // judge the skill ids exist
        List<Integer> ids = new ArrayList<Integer>();
        String[] idsStr = occupationSkillIds.split(",");
        List<OccupationSkillBean> allSkill = skillService.getOccupationSkillList();
        for (String id : idsStr) {
            int idL = Integer.parseInt(id);
            boolean exist = false;
            for (OccupationSkillBean skill : allSkill) {
                exist = skill.getId()==idL;
                if (exist) {
                    break;
                }
            }
            if (!exist) {
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
            ids.add(idL);
        }

        // is Nurse exist
        nurseService.getNurse(userId);
        for (Integer idI : ids) {
            // is Skill exist already
            NurseOccupationSkillBean skillExist = getSkill(userId, idI);
            if (null != skillExist) {
                nurseSkillRepository.delete(skillExist.getId());
            } else {
                NurseOccupationSkillEntity newSkill = new NurseOccupationSkillEntity();
                newSkill.setUserId(userId);
                newSkill.setSkillId(idI);
                newSkill.setPoint(0);
                nurseSkillRepository.save(newSkill);
            }
        }
    }

    @Transactional
    public void update(long userId, int occupationSkillId, int skillPoint) {
        NurseOccupationSkillEntity skill = nurseSkillRepository.findSkillRelationByUserIdAndSkillId(userId, occupationSkillId);
        if (null==skill) {
            throw new BadRequestException(ErrorCode.NURSE_DONT_HAVE_SKILL);
        }
        if (skillPoint>0) {
            skill.setPoint(skillPoint);
            nurseSkillRepository.save(skill);
        }
        return;
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
