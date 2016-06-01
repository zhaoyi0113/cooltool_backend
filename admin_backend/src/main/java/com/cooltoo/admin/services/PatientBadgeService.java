package com.cooltoo.admin.services;
import com.cooltoo.admin.beans.PatientBadgeBean;
import com.cooltoo.admin.converter.PatientBadgeBeanConverter;
import com.cooltoo.admin.converter.PatientBadgeEntityConverter;
import com.cooltoo.admin.entities.PatientBadgeEntity;
import com.cooltoo.repository.BadgeRepository;
import com.cooltoo.admin.repository.PatientBadgeRepository;
import com.cooltoo.admin.repository.PatientRepository;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/3.
 */
@Service("PatientBadgeService")
public class PatientBadgeService{

    @Autowired
    private PatientBadgeRepository repository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private PatientBadgeBeanConverter beanConverter;
    @Autowired
    private PatientBadgeEntityConverter entityConverter;

    public List<PatientBadgeBean> getAll() {
        Iterable<PatientBadgeEntity> iterable = repository.findAll();
        List<PatientBadgeBean> all = new ArrayList<PatientBadgeBean>();
        for (PatientBadgeEntity entity : iterable) {
            PatientBadgeBean bean = beanConverter.convert(entity);
            all.add(bean);
        }
        return all;
    }

    public PatientBadgeBean getOneById(Integer id) {
        PatientBadgeEntity entity = repository.findOne(id);
        if (null == entity) {
            return null;
        }
        return beanConverter.convert(entity);
    }

    @Transactional
    public PatientBadgeBean deleteById(Integer id) {
        PatientBadgeEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        repository.delete(id);
        return beanConverter.convert(entity);
    }

    @Transactional
    public PatientBadgeBean update(PatientBadgeBean bean) {
        if(!repository.exists(bean.getId())) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!badgeRepository.exists(bean.getBadgeId())) {
            throw new BadRequestException(ErrorCode.BADGE_NOT_EXIST);
        }
        if (!patientRepository.exists(bean.getPatientId())) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        PatientBadgeEntity entity = entityConverter.convert(bean);
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public PatientBadgeBean update(int id, long patientId, int badgeId) {
        PatientBadgeBean bean = new PatientBadgeBean();
        bean.setId(id);
        bean.setBadgeId(badgeId);
        bean.setPatientId(patientId);
        return update(bean);
    }

    @Transactional
    public Integer newOne(PatientBadgeBean bean) {
        if (!badgeRepository.exists(bean.getBadgeId())) {
            throw new BadRequestException(ErrorCode.BADGE_NOT_EXIST);
        }
        if (!patientRepository.exists(bean.getPatientId())) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        PatientBadgeEntity entity = entityConverter.convert(bean);
        entity = repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public Integer newOne(long patientId, int badgeId) {
        if (!badgeRepository.exists(badgeId)) {
            throw new BadRequestException(ErrorCode.BADGE_NOT_EXIST);
        }
        if (!patientRepository.exists(patientId)) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        PatientBadgeBean bean = new PatientBadgeBean();
        bean.setBadgeId(badgeId);
        bean.setPatientId(patientId);
        return newOne(bean);
    }
}
