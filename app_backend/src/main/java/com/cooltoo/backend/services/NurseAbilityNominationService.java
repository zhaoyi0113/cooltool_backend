package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseAbilityNominationBean;
import com.cooltoo.backend.converter.NurseAbilityNominationBeanConverter;
import com.cooltoo.backend.entities.NurseAbilityNominationEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseAbilityNominationRepository;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by yzzhao on 3/13/16.
 */
@Service("NurseAbilityNominationService")
public class NurseAbilityNominationService {

    private static final Logger logger = LoggerFactory.getLogger(NurseAbilityNominationService.class);

    @Autowired
    private NurseAbilityNominationRepository nominationRepository;
    @Autowired
    private NurseRepository nurseRepository;
    @Autowired
    private NurseAbilityNominationBeanConverter nominationConverter;


    //==========================================================================
    //           get user's all skill types(SKILL--OCCUPATION) nominated count
    //==========================================================================
    public long getUserAllAbilityNominatedCount(long userId) {
        return nominationRepository.countByUserId(userId);
    }

    //==========================================================================
    //           get user's all skill types(SKILL--OCCUPATION) nominated
    //==========================================================================
    public List<NurseAbilityNominationBean> getAllTypeNominated(long userId) {
        List<NurseAbilityNominationBean> abilityNominates = new ArrayList<NurseAbilityNominationBean>();

        List<NurseAbilityNominationEntity>       allNominated = nominationRepository.findByUserId(userId);
        Map<String, NurseAbilityNominationBean>  id2Nominate  = new HashMap<String, NurseAbilityNominationBean>();
        for (NurseAbilityNominationEntity nominateE : allNominated) {
            String key = nominateE.getAbilityId()+"_"+nominateE.getAbilityType().toString();
            NurseAbilityNominationBean tmp  = nominationConverter.convert(nominateE);
            NurseAbilityNominationBean bean = id2Nominate.get(key);
            if (null==bean) {
                tmp.setAbilityNominateCount(1);
                id2Nominate.put(key, tmp);
            }
            else {
                long count = bean.getAbilityNominateCount();
                count ++;
                bean.setAbilityNominateCount(count);
            }
        }

        Collection<NurseAbilityNominationBean> tmpAbilities = id2Nominate.values();
        for(NurseAbilityNominationBean ablility : tmpAbilities) {
            abilityNominates.add(ablility);
        }
        return abilityNominates;
    }

    //==========================================================================
    //           get user's special skill type(SKILL | OCCUPATION) nominated
    //==========================================================================
    public List<NurseAbilityNominationBean> getSpecialTypeNominated(long userId, SocialAbilityType type) {
        List<NurseAbilityNominationBean> ablibityNominates = new ArrayList<NurseAbilityNominationBean>();

        List<NurseAbilityNominationEntity>       typeNominates = nominationRepository.findByUserIdAndAbilityType(userId, type);
        Map<Integer, NurseAbilityNominationBean> id2Nominate   = new HashMap<Integer, NurseAbilityNominationBean>();
        for (NurseAbilityNominationEntity nominateE : typeNominates) {
            NurseAbilityNominationBean tmp = nominationConverter.convert(nominateE);
            NurseAbilityNominationBean bean = id2Nominate.get(tmp.getAbilityId());
            if (null==bean) {
                tmp.setAbilityNominateCount(1);
                id2Nominate.put(tmp.getAbilityId(), tmp);
            }
            else {
                long count = bean.getAbilityNominateCount();
                count ++;
                bean.setAbilityNominateCount(count);
            }
        }

        Collection<NurseAbilityNominationBean> tmpAbilites = id2Nominate.values();
        for(NurseAbilityNominationBean ablility : tmpAbilites) {
            ablibityNominates.add(ablility);
        }
        return ablibityNominates;
    }

    //==========================================================================
    //           get user's special skill nominated
    //==========================================================================
    public NurseAbilityNominationBean getSpecialAbilityNominated(long userId, int abilityId, SocialAbilityType type) {
        NurseAbilityNominationBean id2Nominate   = new NurseAbilityNominationBean();
        long count = nominationRepository.countByUserIdAndAbilityIdAndAbilityType(userId, abilityId, type);
        id2Nominate.setUserId(userId);
        id2Nominate.setAbilityId(abilityId);
        id2Nominate.setAbilityType(type);
        id2Nominate.setAbilityNominateCount(count);
        return id2Nominate;
    }

    //==========================================================================
    //           get friend's special skill type's skill is nominated by user
    //==========================================================================
    public boolean isNominated(long userId, int skillId, SocialAbilityType type, long friendId) {
        List<NurseAbilityNominationEntity> entities = nominationRepository.findByUserIdAndAbilityIdAndNominatedIdAndAbilityType(friendId, skillId, userId, type);
        return !(null==entities || entities.isEmpty());
    }

    //==========================================================================
    //           user nominates(or not) friend's special skill type's skill
    //==========================================================================
    @Transactional
    public long nominateNurseSkill(long userId, long friendId, int friendSkillId, String skillType) {
        logger.info("user {} nominate friend={} skill={} type={}", userId, friendId, friendSkillId, skillType);
        // check nurse
        validateNurse(userId);
        // check nurse friend
        validateNurse(friendId);
        SocialAbilityType type = SocialAbilityType.parseString(skillType);
        if (null==type) {
            logger.error("the type is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        // can not nominate self
        if (userId == friendId) {
            throw new BadRequestException(ErrorCode.NOMINATION_CAN_NOT_FOR_SELF);
        }
        // this is an toggle operation
        // the userId enable friend's skill
        List<NurseAbilityNominationEntity> existed = nominationRepository.findByUserIdAndAbilityIdAndNominatedIdAndAbilityType(friendId, friendSkillId, userId, type);
        if (!existed.isEmpty()) {
            nominationRepository.delete(existed);
        } else {
            addNomination(userId, friendId, friendSkillId, type);
        }

        return nominationRepository.countByUserIdAndAbilityIdAndAbilityType(friendId, friendSkillId, type);
    }

    private void addNomination(long userId, long friendId, int skillId, SocialAbilityType type) {
        NurseAbilityNominationEntity entity = new NurseAbilityNominationEntity();
        entity.setUserId(friendId);
        entity.setNominatedId(userId);
        entity.setAbilityId(skillId);
        entity.setAbilityType(type);
        entity.setDateTime(Calendar.getInstance().getTime());
        nominationRepository.save(entity);
    }

    private void validateNurse(long userId) {
        if (!nurseRepository.exists(userId)) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
    }

    //=======================================================
    //          delete nomination
    //=======================================================

    @Transactional
    public String deleteByUserIds(String userIds) {
        logger.info("delete skill nomination by userids = {}", userIds);
        if (!VerifyUtil.isIds(userIds)) {
            logger.error("user id format is error");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<Long> arrUserIds = new ArrayList<>();
        String[]   strUserIds = userIds.split(",");
        for (String strUserId : strUserIds) {
            long lUserId = Long.parseLong(strUserId);
            arrUserIds.add(lUserId);
        }
        nominationRepository.deleteByUserIdOrNominatedIdIn(arrUserIds, arrUserIds);
        return userIds;
    }

    @Transactional
    public String deleteBySkillIdsAndType(String skillIds, String type) {
        logger.info("delete skill nomination by skill ids={}  type={}", skillIds, type);
        SocialAbilityType ablibityType = SocialAbilityType.parseString(type);
        if (null==ablibityType) {
            logger.info("type is not exist", skillIds);
        }
        if (!VerifyUtil.isIds(skillIds)) {
            logger.error("skill ids format is error");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<Integer> arrSkillIds = new ArrayList<>();
        String[]      strSkillIds = skillIds.split(",");
        for (String strSkillId : strSkillIds) {
            int lSkillId = Integer.parseInt(strSkillId);
            arrSkillIds.add(lSkillId);
        }
        List<NurseAbilityNominationEntity> allSkillsNominations = nominationRepository.findByAbilityTypeAndAbilityIdIn(ablibityType, arrSkillIds);
        if (null!=allSkillsNominations) {
            nominationRepository.delete(allSkillsNominations);
        }
        return skillIds;
    }
}
