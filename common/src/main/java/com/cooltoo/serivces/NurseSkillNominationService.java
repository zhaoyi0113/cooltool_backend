package com.cooltoo.serivces;

import com.cooltoo.entities.NurseSkillNominationEntity;
import com.cooltoo.entities.OccupationSkillEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.repository.NurseSkillNominationRepository;
import com.cooltoo.repository.OccupationSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yzzhao on 3/13/16.
 */
@Service("NurseSkillNominationService")
public class NurseSkillNominationService {

    @Autowired
    private NurseSkillNominationRepository nominationRepository;

    @Autowired
    private OccupationSkillRepository skillRepository;

    @Autowired
    private NurseRepository nurseRepository;

    public Map<String, Long> getSkillNominationCount(long userId){
        validateNurse(userId);
        Iterable<OccupationSkillEntity> skills = skillRepository.findAll();
        Map<String, Long> skillCount = new HashMap<String, Long>();
        for(OccupationSkillEntity entity : skills){
            long count = nominationRepository.countByUserIdAndSkillId(userId, entity.getId());
            if(!skillCount.containsKey(entity.getName())){
                skillCount.put(entity.getName(), count);
            } else {
                skillCount.put(entity.getName(), skillCount.get(entity.getName())+count);
            }
        }
        return skillCount;
    }

    @Transactional
    public void nominateNurseSkill(long userId, int skillId, long friendId){
        validateNurse(userId);
        validateNurse(friendId);
        validateSkill(skillId);
        List<NurseSkillNominationEntity> existed = nominationRepository.findByUserIdAndSkillIdAndNominatedId(userId, skillId, friendId);
        if(!existed.isEmpty()){
            nominationRepository.delete(existed.get(0));
        }else {
            addNomination(userId, skillId, friendId);
        }
    }

    private void addNomination(long userId, int skillId, long friendId) {
        NurseSkillNominationEntity entity = new NurseSkillNominationEntity();
        entity.setDateTime(Calendar.getInstance().getTime());
        entity.setNominatedId(friendId);
        entity.setUserId(userId);
        entity.setSkillId(skillId);
        nominationRepository.save(entity);
    }

    private void validateNurse(long userId){
        if(!nurseRepository.exists(userId)){
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
    }

    private void validateSkill(int skillId){
        if(!skillRepository.exists(skillId)){
            throw new BadRequestException(ErrorCode.SKILL_NOT_EXIST);
        }
    }
}
