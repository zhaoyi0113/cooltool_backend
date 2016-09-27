package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NurseDoctorScoreBean;
import com.cooltoo.go2nurse.constants.ReasonType;
import com.cooltoo.go2nurse.converter.NurseDoctorScoreBeanConverter;
import com.cooltoo.go2nurse.entities.NurseDoctorScoreEntity;
import com.cooltoo.go2nurse.repository.NurseDoctorScoreRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * Created by zhaolisong on 16/9/27.
 */
@Service("NurseDoctorScoreService")
public class NurseDoctorScoreService {

    private static final Logger logger = LoggerFactory.getLogger(NurseDoctorScoreService.class);

    @Autowired private NurseDoctorScoreRepository repository;
    @Autowired private NurseDoctorScoreBeanConverter beanConverter;

    //==============================================================
    //                  getter
    //==============================================================

    public Map<Long, Float> getScoreByReceiverTypeAndIds(UserType receiverType, List<Long> receiverIds) {
        logger.info("get score by receiverType={} and receiverId_size={}", receiverType, null==receiverIds ? 0 : receiverIds.size());
        Map<Long, Float> score = new HashMap<>();
        Map<Long, Integer> count = new HashMap<>();
        if (!VerifyUtil.isListEmpty(receiverIds)) {
            Long tmpReceiverId;
            Float tmpScore;
            Integer tmpCount;
            List<NurseDoctorScoreEntity> allScore = repository.findByReceiverTypeAndReceiverIdIn(receiverType, receiverIds);
            for (NurseDoctorScoreEntity tmp : allScore) {
                tmpReceiverId = tmp.getReceiverId();
                if (!score.containsKey(tmpReceiverId)) {
                    score.put(tmpReceiverId, 0F);
                    count.put(tmpReceiverId, 0);
                }
                tmpScore = score.get(tmpReceiverId);
                tmpCount = count.get(tmpReceiverId);
                tmpScore += tmp.getScore();
                tmpCount += 1;
                score.put(tmpReceiverId, tmpScore);
                count.put(tmpReceiverId, tmpCount);
            }

            Set<Long> ids = score.keySet();
            for (Long id : ids) {
                tmpScore = score.get(id);
                tmpCount = count.get(id);
                if (tmpCount<=0) {
                    score.put(id, 0F);
                }
                else {
                    score.put(id, tmpScore/tmpCount);
                }
            }
        }
        return score;
    }

    public Float getScoreByReceiverTypeAndId(UserType receiverType, Long receiverId) {
        logger.info("get score by receiverType={} and receiverId={}", receiverType, receiverId);

        float tmpScore = 0F;
        int tmpCount = 0;
        List<NurseDoctorScoreEntity> allScore = repository.findByReceiverTypeAndReceiverId(receiverType, receiverId);
        for (NurseDoctorScoreEntity tmp : allScore) {
            tmpScore += tmp.getScore();
            tmpCount += 1;
        }

        if (tmpCount>0) {
            tmpScore /= tmpCount;
        }

        logger.info("score={}", tmpScore);
        return tmpScore;
    }

    //=================================================================
    //         update
    //=================================================================

    //=================================================================
    //         add
    //=================================================================
    @Transactional
    public NurseDoctorScoreBean addScore(UserType receiverType, Long receiverId, Long userId, ReasonType reasonType, Long reasonId, float score, int weight) {
        logger.info("userId={} add score={} weight={} for reasonType={} reasonId={} to receiverType={} receiverId={}",
                userId, score, weight, reasonType, reasonId, receiverType, receiverId);
        if (null==receiverType || null==reasonType
                || (null==receiverId || receiverId<0)
                || (null==userId || userId<0)
                || (null==reasonId || reasonId<0)
                || score<=0
                || weight<=0) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        NurseDoctorScoreEntity entity = new NurseDoctorScoreEntity();
        entity.setReceiverType(receiverType);
        entity.setReceiverId(receiverId);
        entity.setUserId(userId);
        entity.setReasonType(reasonType);
        entity.setReasonId(reasonId);
        entity.setScore(score);
        entity.setWeight(weight);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        List<NurseDoctorScoreEntity> allEntity = repository.findByReceiverTypeAndReceiverIdAndUserIdAndReasonTypeAndReasonId(
                receiverType, receiverId, userId, reasonType, reasonId
        );
        for (int i = 0; i<allEntity.size(); i ++) {
            NurseDoctorScoreEntity tmp = allEntity.get(i);
            if (tmp.getId()==entity.getId()) {
                allEntity.remove(i);
                break;
            }
        }
        repository.delete(allEntity);

        NurseDoctorScoreBean bean = beanConverter.convert(entity);
        return bean;
    }
}
