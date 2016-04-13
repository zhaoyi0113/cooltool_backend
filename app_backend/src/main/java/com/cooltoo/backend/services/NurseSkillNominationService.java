package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSkillNominationBean;
import com.cooltoo.backend.converter.NurseSkillNominationBeanConverter;
import com.cooltoo.backend.entities.NurseSkillNominationEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseSkillNominationRepository;
import com.cooltoo.constants.OccupationSkillType;
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
@Service("NurseSkillNominationService")
public class NurseSkillNominationService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSkillNominationService.class);

    @Autowired
    private NurseSkillNominationRepository nominationRepository;
    @Autowired
    private NurseRepository nurseRepository;
    @Autowired
    private NurseSkillNominationBeanConverter nominationConverter;


    //==========================================================================
    //           get user's all skill types(SKILL--OCCUPATION) nominated count
    //==========================================================================
    public long getUserAllSkillNominatedCount(long userId) {
        return nominationRepository.countByUserId(userId);
    }

    //==========================================================================
    //           get user's all skill types(SKILL--OCCUPATION) nominated
    //==========================================================================
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
                count ++;
                bean.setSkillNominateCount(count);
            }
        }

        Collection<NurseSkillNominationBean> tmpAbilites = id2Nominate.values();
        for(NurseSkillNominationBean ablility : tmpAbilites) {
            ablibityNominates.add(ablility);
        }
        return ablibityNominates;
    }

    //==========================================================================
    //           get user's special skill type(SKILL | OCCUPATION) nominated
    //==========================================================================
    public List<NurseSkillNominationBean> getSpecialTypeNominated(long userId, OccupationSkillType type) {
        List<NurseSkillNominationBean> ablibityNominates = new ArrayList<NurseSkillNominationBean>();

        List<NurseSkillNominationEntity>       typeNominates = nominationRepository.findByUserIdAndSkillType(userId, type);
        Map<Integer, NurseSkillNominationBean> id2Nominate   = new HashMap<Integer, NurseSkillNominationBean>();
        for (NurseSkillNominationEntity nominateE : typeNominates) {
            NurseSkillNominationBean tmp = nominationConverter.convert(nominateE);
            NurseSkillNominationBean bean = id2Nominate.get(tmp.getSkillId());
            if (null==bean) {
                tmp.setSkillNominateCount(1);
                id2Nominate.put(tmp.getSkillId(), tmp);
            }
            else {
                long count = bean.getSkillNominateCount();
                count ++;
                bean.setSkillNominateCount(count);
            }
        }

        Collection<NurseSkillNominationBean> tmpAbilites = id2Nominate.values();
        for(NurseSkillNominationBean ablility : tmpAbilites) {
            ablibityNominates.add(ablility);
        }
        return ablibityNominates;
    }

    //==========================================================================
    //           get user's special skill nominated
    //==========================================================================
    public NurseSkillNominationBean getSpecialTypeSkillNominated(long userId, int skillId, OccupationSkillType type) {
        NurseSkillNominationBean         id2Nominate   = new NurseSkillNominationBean();
        long count = nominationRepository.countByUserIdAndSkillIdAndSkillType(userId, skillId, type);
        id2Nominate.setUserId(userId);
        id2Nominate.setSkillId(skillId);
        id2Nominate.setSkillType(type);
        id2Nominate.setSkillNominateCount(count);
        return id2Nominate;
    }

    //==========================================================================
    //           get friend's special skill type's skill is nominated by user
    //==========================================================================
    public boolean isNominated(long userId, int skillId, OccupationSkillType type, long friendId) {
        List<NurseSkillNominationEntity> entities = nominationRepository.findByUserIdAndSkillIdAndNominatedIdAndSkillType(friendId, skillId, userId, type);
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
        OccupationSkillType type = OccupationSkillType.parseString(skillType);
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
        List<NurseSkillNominationEntity> existed = nominationRepository.findByUserIdAndSkillIdAndNominatedIdAndSkillType(friendId, friendSkillId, userId, type);
        if (!existed.isEmpty()) {
            nominationRepository.delete(existed);
        } else {
            addNomination(userId, friendId, friendSkillId, type);
        }

        return nominationRepository.countByUserIdAndSkillIdAndSkillType(friendId, friendSkillId, type);
    }

    private void addNomination(long userId, long friendId, int skillId, OccupationSkillType type) {
        NurseSkillNominationEntity entity = new NurseSkillNominationEntity();
        entity.setUserId(friendId);
        entity.setNominatedId(userId);
        entity.setSkillId(skillId);
        entity.setSkillType(type);
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
        OccupationSkillType ablibityType = OccupationSkillType.parseString(type);
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
        List<NurseSkillNominationEntity> allSkillsNominations = nominationRepository.findBySkillTypeAndSkillIdIn(ablibityType, arrSkillIds);
        if (null!=allSkillsNominations) {
            nominationRepository.delete(allSkillsNominations);
        }
        return skillIds;
    }
}
