package com.cooltoo.services;

import com.cooltoo.beans.NurseAuthorizationBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.converter.NurseAuthorizationBeanConverter;
import com.cooltoo.entities.NurseAuthorizationEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseAuthorizationRepository;
import com.cooltoo.repository.NurseRepository;
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
 * Created by zhaolisong on 19/12/2016.
 */
@Service("CommonNurseAuthorizationService")
public class CommonNurseAuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(CommonNurseAuthorizationService.class);

    @Autowired private NurseAuthorizationRepository repository;
    @Autowired private NurseAuthorizationBeanConverter beanConverter;

    @Autowired private NurseRepository nurseRepository;


    //================================================================================
    //                  getting
    //================================================================================

    public NurseAuthorizationBean getAuthorizationByNurseId(long nurseId) {
        logger.info("get nurse authorization by nurseId={}", nurseId);
        List<NurseAuthorizationEntity> entities = repository.findByNurseId(nurseId);
        for (NurseAuthorizationEntity tmp : entities) {
            return beanConverter.convert(tmp);
        }
        return defaultAuth(nurseId);
    }

    public Map<Long, NurseAuthorizationBean> getAuthorizationByNurseIds(List<Long> nurseIds) {
        int count = VerifyUtil.isListEmpty(nurseIds) ? 0 : nurseIds.size();
        logger.info("get nurse authorization by nurseIds, count is {}", nurseIds);
        Map<Long, NurseAuthorizationBean> map = new HashMap<>();
        if (0 == count) {
            return map;
        }

        List<NurseAuthorizationEntity> entities = repository.findByNurseIdIn(nurseIds);
        for (NurseAuthorizationEntity tmp : entities) {
            NurseAuthorizationBean bean = beanConverter.convert(tmp);
            map.put(bean.getNurseId(), bean);
        }

        for (Long tmpId : nurseIds) {
            if (!map.containsKey(tmpId)) {
                map.put(tmpId, defaultAuth(tmpId));
            }
        }

        return map;
    }

    private NurseAuthorizationBean defaultAuth(long nurseId) {
        NurseAuthorizationBean bean = new NurseAuthorizationBean();
        bean.setId(0);
        bean.setTime(new Date());
        bean.setStatus(CommonStatus.ENABLED);
        bean.setNurseId(nurseId);
        bean.setAuthOrderHeadNurse(UserAuthority.AGREE_ALL);
        bean.setAuthOrderAdmin(UserAuthority.AGREE_ALL);
        bean.setAuthNotificationHeadNurse(UserAuthority.DENY_ALL);
        bean.setAuthConsultationHeadNurse(UserAuthority.AGREE_ALL);
        return bean;
    }


    //================================================================================
    //                  setting
    //================================================================================
    @Transactional
    public NurseAuthorizationBean setAuthorization(long nurseId,
                                                   UserAuthority authOrderHeadNurse,
                                                   UserAuthority authOrderAdmin,
                                                   UserAuthority authNotificationHeadNurse,
                                                   UserAuthority authConsultationHeadNurse,
                                                   UserAuthority authConsultationAdmin) {
        logger.info("set nurse authorization. nurseId={} authOrderHeadNurse={} authOrderAdmin={} authNotificationHeadNurse={} authConsultationHeadNurse={} authConsultationAdmin={}",
                nurseId, authOrderHeadNurse, authOrderAdmin, authNotificationHeadNurse, authConsultationHeadNurse, authConsultationAdmin);
        if (!nurseRepository.exists(nurseId)) {
            logger.warn("nurse not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        NurseAuthorizationEntity entity;
        List<NurseAuthorizationEntity> entities = repository.findByNurseId(nurseId);
        if (VerifyUtil.isListEmpty(entities)) {
            entity = new NurseAuthorizationEntity();
            entity.setTime(new Date());
            entity.setStatus(CommonStatus.ENABLED);
            entity.setNurseId(nurseId);
            entity.setAuthOrderHeadNurse(UserAuthority.AGREE_ALL);
            entity.setAuthOrderAdmin(UserAuthority.AGREE_ALL);
            entity.setAuthNotificationHeadNurse(UserAuthority.DENY_ALL);
            entity.setAuthConsultationHeadNurse(UserAuthority.AGREE_ALL);
        }
        else {
            entity = entities.get(0);
        }

        if (null!=authOrderHeadNurse && !authOrderHeadNurse.equals(entity.getAuthOrderHeadNurse())) {
            entity.setAuthOrderHeadNurse(authOrderHeadNurse);
        }
        if (null!=authOrderAdmin && !authOrderAdmin.equals(entity.getAuthOrderAdmin())) {
            entity.setAuthOrderAdmin(authOrderAdmin);
        }
        if (null!=authNotificationHeadNurse && !authNotificationHeadNurse.equals(entity.getAuthNotificationHeadNurse())) {
            entity.setAuthNotificationHeadNurse(authNotificationHeadNurse);
        }
        if (null!=authConsultationHeadNurse && !authConsultationHeadNurse.equals(entity.getAuthConsultationHeadNurse())) {
            entity.setAuthConsultationHeadNurse(authConsultationHeadNurse);
        }

        entity = repository.save(entity);
        entities = repository.findByNurseId(nurseId);
        if (!VerifyUtil.isListEmpty(entities)) {
            for (int i=0; i<entities.size(); i ++) {
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
