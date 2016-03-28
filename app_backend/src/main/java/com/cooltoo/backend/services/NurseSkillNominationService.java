package com.cooltoo.backend.services;

import com.cooltoo.backend.api.NurseSkillNominationAPI;
import com.cooltoo.backend.beans.NurseOccupationSkillBean;
import com.cooltoo.backend.beans.NurseSkillNominationBean;
import com.cooltoo.backend.beans.OccupationSkillBean;
import com.cooltoo.backend.entities.NurseSkillNominationEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseSkillNominationRepository;
import com.cooltoo.constants.OccupationSkillType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by yzzhao on 3/13/16.
 */
@Service("NurseSkillNominationService")
public class NurseSkillNominationService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSkillNominationAPI.class);

    @Autowired
    private NurseSkillNominationRepository nominationRepository;

    @Autowired
    private OccupationSkillService skillService;

    @Autowired
    private NurseOccupationSkillService nurseSkillService;

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    public List<NurseSkillNominationBean> getAllNominationBeans(long userId) {
        // is Nurse exist
        validateNurse(userId);

        // get all skill
        List<OccupationSkillBean> skills = skillService.getOccupationSkillList();
        Map<Integer, OccupationSkillBean> skillIdAndSkillMap = new Hashtable<Integer, OccupationSkillBean>();
        List<Long> imgIds = new ArrayList<Long>();
        for (OccupationSkillBean skill : skills) {
            skillIdAndSkillMap.put(skill.getId(), skill);
            imgIds.add(skill.getImageId());
        }

        // cache skill's images
        Map<Long, String> imageIdAndUrlMap = storageService.getFilePath(imgIds);


        // get user's skill
        List<NurseOccupationSkillBean> nurseSkills = nurseSkillService.getAllSkills(userId);

        // get all user's skill nomination
        List<NurseSkillNominationEntity> allNoms = nominationRepository.findByUserId(userId);

        // construct all user's skill nomination count
        List<NurseSkillNominationBean> nominationCount = new ArrayList<NurseSkillNominationBean>();
        NurseSkillNominationBean nomin_count = null;
        OccupationSkillBean skill = null;
        String imageUrl = null;
        for (NurseOccupationSkillBean nurseSkill : nurseSkills) {
            nomin_count = new NurseSkillNominationBean();
            skill    = skillIdAndSkillMap.get(nurseSkill.getSkillId());
            imageUrl = imageIdAndUrlMap.get(skill.getImageId());
            nomin_count.setSkillId(skill.getId());
            nomin_count.setSkillType(skill.getType());
            nomin_count.setSkillName(skill.getName());
            nomin_count.setSkillImageUrl(imageUrl);
            nomin_count.setSkillNominateCount(0);

            int count = 0;
            for (NurseSkillNominationEntity nomin : allNoms) {
                if (nomin.getSkillId() != skill.getId()){
                    continue;
                }
                count++;
            }
            nomin_count.setSkillNominateCount(count);
            nominationCount.add(nomin_count);
        }

        NurseSkillNominationBean[] sorted = new NurseSkillNominationBean[nominationCount.size()];
        nominationCount.toArray(sorted);
        Arrays.sort(sorted, new Comparator<NurseSkillNominationBean>() {
            @Override
            public int compare(NurseSkillNominationBean o1, NurseSkillNominationBean o2) {
                return -(int)(o1.getSkillNominateCount() - o2.getSkillNominateCount());
            }
        });
        nominationCount.clear();
        for (int i = 0; i < sorted.length; i ++) {
            nominationCount.add(sorted[i]);
        }

        return nominationCount;
    }

    public List<NurseSkillNominationBean> getSkillNominationBeans(long userId) {
        List<NurseSkillNominationBean> all = getAllNominationBeans(userId);
        List<NurseSkillNominationBean> skills = new ArrayList<NurseSkillNominationBean>();
        for (NurseSkillNominationBean skill : all) {
            if (!OccupationSkillType.SKILL.equals(skill.getSkillType())) {
                continue;
            }
            skills.add(skill);
        }
        return skills;
    }

    public long getSkillNominationCount(long userId, int skillId) {
        OccupationSkillBean skill = skillService.getOccupationSkill(skillId);
        if (null == skill) {
            throw new BadRequestException(ErrorCode.SKILL_NOT_EXIST);
        }
        return nominationRepository.countByUserIdAndSkillId(userId, skillId);
    }

    @Transactional
    public long nominateNurseSkill(long userId, int skillId, long friendId) {
        logger.info("nominate nurse skill="+skillId+", friend="+friendId);
        // check nurse
        validateNurse(userId);
        // check nurse friend
        validateNurse(friendId);
        // check skill
        validateSkill(skillId);
        // can not nominate self
        if (userId==friendId) {
            throw new BadRequestException(ErrorCode.NOMINATION_CAN_NOT_FOR_SELF);
        }
        NurseOccupationSkillBean friendSkill = nurseSkillService.getSkill(friendId, skillId);
        if (null==friendSkill) {
            throw new BadRequestException(ErrorCode.NURSE_DONT_HAVE_SKILL);
        }
        OccupationSkillBean skill = skillService.getOccupationSkill(skillId);
        if (null==skill) {
            throw new BadRequestException(ErrorCode.SKILL_NOT_EXIST);
        }
        // this is an toggle operation
        // the userId enable friend's skill
        List<NurseSkillNominationEntity> existed = nominationRepository.findByUserIdAndSkillIdAndNominatedId(friendId, skillId, userId);
        int point = 0;
        if (!existed.isEmpty()) {
            nominationRepository.delete(existed.get(0));
            point = - skill.getFactor();

        } else {
            addNomination(userId, skillId, friendId);
            point = skill.getFactor();
        }
        if (point!=0) {
            point += friendSkill.getPoint();
            nurseSkillService.update(friendSkill.getUserId(), skill.getId(), point);
        }

        return nominationRepository.countByUserIdAndSkillId(friendSkill.getUserId(), skillId);
    }

    private void addNomination(long userId, int skillId, long friendId) {
        NurseSkillNominationEntity entity = new NurseSkillNominationEntity();
        entity.setDateTime(Calendar.getInstance().getTime());
        entity.setNominatedId(userId);
        entity.setUserId(friendId);
        entity.setSkillId(skillId);
        nominationRepository.save(entity);
    }

    private void validateNurse(long userId) {
        if (!nurseRepository.exists(userId)) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
    }

    private void validateSkill(int skillId) {
        OccupationSkillBean skill = skillService.getOccupationSkill(skillId);
        if (null==skill) {
            throw new BadRequestException(ErrorCode.SKILL_NOT_EXIST);
        }
    }

    public long getUserAllSkillNominatedCount(long userId){
        return nominationRepository.countByuserId(userId);
    }
}
