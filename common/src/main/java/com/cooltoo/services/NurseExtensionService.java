package com.cooltoo.services;

import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.converter.NurseExtensionBeanConverter;
import com.cooltoo.entities.NurseExtensionEntity;
import com.cooltoo.repository.NurseExtensionRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/8/11.
 */
@Service("NurseExtensionService")
public class NurseExtensionService {

    private static final Logger logger = LoggerFactory.getLogger(NurseExtensionService.class);

    @Autowired private NurseExtensionRepository repository;
    @Autowired private NurseExtensionBeanConverter beanConverter;

    //================================================================================
    //                  getting
    //================================================================================

    public NurseExtensionBean getExtensionByNurseId(long nurseId) {
        logger.info("get nurse extension information by nurseId={}", nurseId);
        List<NurseExtensionEntity> entities = repository.findByNurseId(nurseId);
        for (NurseExtensionEntity tmp : entities) {
            return beanConverter.convert(tmp);
        }
        return null;
    }

    public Map<Long, NurseExtensionBean> getExtensionByNurseIds(List<Long> nurseIds) {
        int count = VerifyUtil.isListEmpty(nurseIds) ? 0 : nurseIds.size();
        logger.info("get nurse extension information by nurseIds, count is {}", nurseIds);
        Map<Long, NurseExtensionBean> map = new HashMap<>();
        if (0 == count) {
            return map;
        }

        List<NurseExtensionEntity> entities = repository.findByNurseIdIn(nurseIds);
        for (NurseExtensionEntity tmp : entities) {
            NurseExtensionBean bean = beanConverter.convert(tmp);
            map.put(bean.getNurseId(), bean);
        }
        return map;
    }

    public YesNoEnum canAnswerNursingQuestion(long nurseId) {
        logger.info("can nurse can answer nursing question, nurseId={}", nurseId);
        List<NurseExtensionEntity> entities = repository.findByNurseId(nurseId);
        for (NurseExtensionEntity tmp : entities) {
            return tmp.getAnswerNursingQuestion();
        }
        return YesNoEnum.NO;
    }


    //================================================================================
    //                  setting
    //================================================================================
    @Transactional
    public NurseExtensionBean setExtension(long nurseId, YesNoEnum answerNursingQuestion, String beGoodAt, String jobTitle) {
        logger.info("set nurse extension. nurseId={} answerNursingQuestion={} beGoodAt={} jobTitle={}",
                nurseId, answerNursingQuestion, beGoodAt, jobTitle);
        if (null==answerNursingQuestion) {
            logger.warn("answerNursingQuestion is empty");
        }
        if (VerifyUtil.isStringEmpty(beGoodAt)) {
            logger.warn("beGoodAt is empty");
        }
        if (VerifyUtil.isStringEmpty(jobTitle)) {
            logger.warn("jobTitle is empty");
        }

        NurseExtensionEntity entity;
        List<NurseExtensionEntity> entities = repository.findByNurseId(nurseId);

        int extensionCount = 0;
        if (VerifyUtil.isListEmpty(entities)) {
            entity = new NurseExtensionEntity();
            entity.setTime(new Date());
            entity.setStatus(CommonStatus.ENABLED);
            entity.setNurseId(nurseId);
            if (null!=answerNursingQuestion) {
                entity.setAnswerNursingQuestion(answerNursingQuestion);
            }
            if (!VerifyUtil.isStringEmpty(beGoodAt)) {
                entity.setGoodAt(beGoodAt.trim());
            }
            if (!VerifyUtil.isStringEmpty(jobTitle)) {
                entity.setJobTitle(jobTitle);
            }
        }
        else {
            entity = entities.get(0);
            if (null!=answerNursingQuestion) {
                entity.setAnswerNursingQuestion(answerNursingQuestion);
            }
            if (!VerifyUtil.isStringEmpty(beGoodAt)) {
                entity.setGoodAt(beGoodAt.trim());
            }
            if (!VerifyUtil.isStringEmpty(jobTitle)) {
                entity.setJobTitle(jobTitle);
            }
            extensionCount = entities.size();
        }
        entity = repository.save(entity);
        if (extensionCount>1) {
            for (int i=0; i<extensionCount; i ++) {
                if (entities.get(i).getId()==entity.getId()) {
                    entities.remove(i);
                    break;
                }
            }
            repository.delete(entities);
        }
        return beanConverter.convert(entity);
    }
}
