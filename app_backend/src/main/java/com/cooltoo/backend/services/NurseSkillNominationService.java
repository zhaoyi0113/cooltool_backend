package com.cooltoo.backend.services;

import com.cooltoo.backend.api.NurseSkillNominationAPI;
import com.cooltoo.backend.beans.SocialAbilitiesBean;
import com.cooltoo.backend.beans.NurseSkillNominationBean;
import com.cooltoo.backend.beans.OccupationSkillBean;
import com.cooltoo.backend.converter.NurseSkillNominationBeanConverter;
import com.cooltoo.backend.entities.NurseSkillNominationEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseSkillNominationRepository;
import com.cooltoo.beans.NurseHospitalRelationBean;
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
    private NurseRepository nurseRepository;
    @Autowired
    private NurseSkillNominationBeanConverter nominationConverter;

    public List<NurseSkillNominationBean> getAllTypeNominated(long userId) {
        List<NurseSkillNominationBean> ablibityNominates = new ArrayList<NurseSkillNominationBean>();

        List<NurseSkillNominationEntity>       allNominated = nominationRepository.findByUserId(userId);
        Map<String, NurseSkillNominationBean>  id2Nominate  = new HashMap<String, NurseSkillNominationBean>();
        for (NurseSkillNominationEntity nominateE : allNominated) {
            String key = nominateE.getSkillId()+"_"+nominateE.getSkillType().toString();
            NurseSkillNominationBean tmp  = nominationConverter.convert(nominateE);
            NurseSkillNominationBean bean = id2Nominate.get(key);
            if (null==bean) {
                tmp.setSkillNominateCount(1);
                id2Nominate.put(key, tmp);
            }
            else {
                long count = bean.getSkillNominateCount();
                bean.setSkillNominateCount(count++);
            }
        }

        Collection<NurseSkillNominationBean> tmpAbilites = id2Nominate.values();
        for(NurseSkillNominationBean ablility : tmpAbilites) {
            ablibityNominates.add(ablility);
        }
        return ablibityNominates;
    }

    public List<NurseSkillNominationBean> getSpecialTypeNominated(long userId, OccupationSkillType type) {
        List<NurseSkillNominationBean> ablibityNominates = new ArrayList<NurseSkillNominationBean>();

        List<NurseSkillNominationEntity>       typeNominates = nominationRepository.findByUserIdAndSkillType(userId, type);
        Map<Integer, NurseSkillNominationBean> id2Nominate = new HashMap<Integer, NurseSkillNominationBean>();
        for (NurseSkillNominationEntity nominateE : typeNominates) {
            NurseSkillNominationBean tmp = nominationConverter.convert(nominateE);
            NurseSkillNominationBean bean = id2Nominate.get(tmp.getSkillId());
            if (null==bean) {
                tmp.setSkillNominateCount(1);
                id2Nominate.put(tmp.getSkillId(), tmp);
            }
            else {
                long count = bean.getSkillNominateCount();
                bean.setSkillNominateCount(count++);
            }
        }

        Collection<NurseSkillNominationBean> tmpAbilites = id2Nominate.values();
        for(NurseSkillNominationBean ablility : tmpAbilites) {
            ablibityNominates.add(ablility);
        }
        return ablibityNominates;
    }

    public boolean isNominated(long userId, int skillId, OccupationSkillType type, long friendId) {
        List<NurseSkillNominationEntity> entities = nominationRepository.findByUserIdAndSkillIdAndNominatedIdAndSkillType(friendId, skillId, userId, type);
        return !(null==entities || entities.isEmpty());
    }

    @Transactional
    public long nominateNurseSkill(long userId, int skillId, OccupationSkillType type, long friendId) {
        logger.info("nominate nurse skill={} type={} friend={}", skillId, type, friendId);
        // check nurse
        validateNurse(userId);
        // check nurse friend
        validateNurse(friendId);
        // can not nominate self
        if (userId == friendId) {
            throw new BadRequestException(ErrorCode.NOMINATION_CAN_NOT_FOR_SELF);
        }
        // this is an toggle operation
        // the userId enable friend's skill
        List<NurseSkillNominationEntity> existed = nominationRepository.findByUserIdAndSkillIdAndNominatedIdAndSkillType(friendId, skillId, userId, type);
        if (!existed.isEmpty()) {
            nominationRepository.delete(existed);
        } else {
            addNomination(userId, skillId, type, friendId);
        }

        return nominationRepository.countByUserIdAndSkillIdAndSkillType(friendId, skillId, type);
    }

    private void addNomination(long userId, int skillId, OccupationSkillType type, long friendId) {
        NurseSkillNominationEntity entity = new NurseSkillNominationEntity();
        entity.setDateTime(Calendar.getInstance().getTime());
        entity.setNominatedId(userId);
        entity.setUserId(friendId);
        entity.setSkillId(skillId);
        entity.setSkillType(type);
        nominationRepository.save(entity);
    }

    private void validateNurse(long userId) {
        if (!nurseRepository.exists(userId)) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
    }

    public long getUserAllSkillNominatedCount(long userId) {
        return nominationRepository.countByUserId(userId);
    }
}
