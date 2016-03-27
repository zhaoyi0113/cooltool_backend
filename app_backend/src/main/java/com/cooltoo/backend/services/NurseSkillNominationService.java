package com.cooltoo.backend.services;

import com.cooltoo.backend.api.NurseSkillNominationAPI;
import com.cooltoo.backend.beans.NurseSkillNominationBean;
import com.cooltoo.backend.beans.OccupationSkillBean;
import com.cooltoo.backend.entities.NurseSkillNominationEntity;
import com.cooltoo.backend.entities.OccupationSkillEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseSkillNominationRepository;
import com.cooltoo.backend.repository.OccupationSkillRepository;
import com.cooltoo.constants.OccupationSkillType;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
@Service("NurseSkillNominationService")
public class NurseSkillNominationService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSkillNominationAPI.class);

    @Autowired
    private NurseSkillNominationRepository nominationRepository;

    @Autowired
    private OccupationSkillRepository skillRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private NurseSpeakService speakService;

    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    public List<NurseSkillNominationBean> getAllSkillsNominationCount(long userId, int index, int number) {
        // is Nurse exist
        validateNurse(userId);

        // get Page {index} NumberOfPage {number}
        PageRequest request =new PageRequest(index, number);
        Page<OccupationSkillEntity> skills = skillRepository.findAll(request);

        // convert to Bean
        List<NurseSkillNominationBean> nominationBeans = new ArrayList();
        for (OccupationSkillEntity entity : skills) {
            long count = nominationRepository.countByUserIdAndSkillId(userId, entity.getId());
            NurseSkillNominationBean bean= new NurseSkillNominationBean();
            bean.setSkillId(entity.getId());
            bean.setSkillName(entity.getName());
            bean.setSkillNominateCount(count);
            String url = storageService.getFilePath(entity.getImageId());
            bean.setSkillImageUrl(url);
            nominationBeans.add(bean);
        }
        return nominationBeans;
    }

    public long getSkillNominationCount(long userId, int skillId) {
        OccupationSkillEntity skill = skillRepository.getOne(skillId);
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

        // this is an toggle operation
        List<NurseSkillNominationEntity> existed = nominationRepository.findByUserIdAndSkillIdAndNominatedId(userId, skillId, friendId);
        if (!existed.isEmpty()) {
            nominationRepository.delete(existed.get(0));
        } else {
            addNomination(userId, skillId, friendId);
        }
        return nominationRepository.countByUserIdAndSkillId(userId, skillId);
    }

    private void addNomination(long userId, int skillId, long friendId) {
        NurseSkillNominationEntity entity = new NurseSkillNominationEntity();
        entity.setDateTime(Calendar.getInstance().getTime());
        entity.setNominatedId(friendId);
        entity.setUserId(userId);
        entity.setSkillId(skillId);
        nominationRepository.save(entity);
    }

    private void validateNurse(long userId) {
        if (!nurseRepository.exists(userId)) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
    }

    private void validateSkill(int skillId) {
        if (!skillRepository.exists(skillId)) {
            throw new BadRequestException(ErrorCode.SKILL_NOT_EXIST);
        }
    }

    public long getUserAllSkillNorminatedCount(long userId){
        return nominationRepository.countByuserId(userId);
    }
}
