package com.cooltoo.serivces;

import com.cooltoo.entities.NurseSkillRelationEntity;
import com.cooltoo.entities.OccupationSkillEntity;
import com.cooltoo.repository.NurseSkillNominationRepository;
import com.cooltoo.repository.NurseSkillRelationRepository;
import com.cooltoo.repository.OccupationSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private NurseSkillRelationRepository relationRepository;

    @Autowired
    private OccupationSkillRepository skillRepository;

    public Map<String, Long> getSkillNominationCount(long userId){
        List<OccupationSkillEntity> skills = skillRepository.findSkillEntityByUserId(userId);
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
}
