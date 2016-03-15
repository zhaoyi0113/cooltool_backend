package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSkillNorminationBean;
import com.cooltoo.backend.entities.NurseSkillNominationEntity;
import com.cooltoo.backend.entities.OccupationSkillEntity;
import com.cooltoo.backend.repository.NurseSkillNorminationRepository;
import com.cooltoo.backend.repository.OccupationSkillRepository;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by yzzhao on 3/13/16.
 */
@Service("NurseSkillNominationService")
public class NurseSkillNorminationService {

    @Autowired
    private NurseSkillNorminationRepository nominationRepository;

    @Autowired
    private OccupationSkillRepository skillRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private StorageService storageService;

    public List<NurseSkillNorminationBean> getAllSkillsNominationCount(long userId, int index, int number) {
        validateNurse(userId);
        PageRequest request =new PageRequest(index, number);
        Page<OccupationSkillEntity> skills = skillRepository.findAll(request);
        List<NurseSkillNorminationBean> norminationBeans = new ArrayList();

        for (OccupationSkillEntity entity : skills) {
            long count = nominationRepository.countByUserIdAndSkillId(userId, entity.getId());
            NurseSkillNorminationBean bean= new NurseSkillNorminationBean();
            bean.setSkillId(entity.getId());
            bean.setSkillName(entity.getName());
            bean.setSkillNominateCount(count);
            String url = storageService.getFileUrl(entity.getImageId());
            bean.setSkillImageUrl(url);
            norminationBeans.add(bean);
        }
        return norminationBeans;
    }

    public long getSkillNorminationCount(long userId, int skillId){
        return nominationRepository.countByUserIdAndSkillId(userId, skillId);
    }

    @Transactional
    public long nominateNurseSkill(long userId, int skillId, long friendId) {
        validateNurse(userId);
        validateNurse(friendId);
        validateSkill(skillId);
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
}
