package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ReExaminationStrategyBean;
import com.cooltoo.go2nurse.converter.ReExaminationStrategyBeanConverter;
import com.cooltoo.go2nurse.entities.ReExaminationStrategyEntity;
import com.cooltoo.go2nurse.repository.ReExaminationStrategyRepository;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/8/26.
 */
@Service("ReExaminationStrategyService")
public class ReExaminationStrategyService {

    private static final Logger logger = LoggerFactory.getLogger(ReExaminationStrategyService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private ReExaminationStrategyRepository repository;
    @Autowired private ReExaminationStrategyBeanConverter beanConverter;
    @Autowired private CommonDepartmentService departmentService;

    //===========================================================================
    //                        getting
    //===========================================================================
//    public

    private List<ReExaminationStrategyBean> entitiesToBeans(Iterable<ReExaminationStrategyEntity> entities) {
        List<ReExaminationStrategyBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (ReExaminationStrategyEntity tmp : entities) {
            ReExaminationStrategyBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<ReExaminationStrategyBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Integer> departmentIds = new ArrayList<>();
        for (ReExaminationStrategyBean tmp : beans) {
            departmentIds.add(tmp.getDepartmentId());
        }

//        Map<Integer, HospitalDepartmentBean> departmentIdToBean
    }

    //===========================================================================
    //                        updating
    //===========================================================================
    @Transactional
    public ReExaminationStrategyBean updateReExaminationStrategyForDepartment(long strategyId, Boolean isRecycled, String strategyDay, String status) {
        logger.info("update re-examination strategy={} 's day={}, isRecycled={}", strategyId, strategyDay, isRecycled);
        ReExaminationStrategyEntity entity = repository.findOne(strategyId);

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(strategyDay) && VerifyUtil.isIds(strategyDay)) {
            entity.setReExaminationDay(strategyDay);
            changed = true;
        }

        if (null!=isRecycled) {
            entity.setRecycled(isRecycled ? YesNoEnum.YES : YesNoEnum.NO);
            changed = true;
        }

        CommonStatus strategyStatus = CommonStatus.parseString(status);
        if (null!=strategyStatus && !strategyStatus.equals(entity.getStatus())) {
            entity.setStatus(strategyStatus);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }
        logger.info("update is {}", entity);
        return beanConverter.convert(entity);
    }

    //===========================================================================
    //                        adding
    //===========================================================================
    @Transactional
    public ReExaminationStrategyBean addReExaminationStrategyForDepartment(int departmentId, boolean isRecycled, String strategyDay) {
        logger.info("add re-examination strategy day={} for department={}, isRecycled={}", strategyDay, departmentId, isRecycled);
        if (VerifyUtil.isStringEmpty(strategyDay) || !VerifyUtil.isIds(strategyDay)) {
            logger.error("re-examination strategy day format is error");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!departmentService.existsDepartment(departmentId)) {
            logger.error("department is not existed");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // add or modify
        List<ReExaminationStrategyEntity> entities = repository.findByDepartmentId(departmentId, sort);
        ReExaminationStrategyEntity entity;
        if (VerifyUtil.isListEmpty(entities)) {
            entity = new ReExaminationStrategyEntity();
            entity.setDepartmentId(departmentId);
            entity.setRecycled(isRecycled ? YesNoEnum.YES : YesNoEnum.NO);
            entity.setReExaminationDay(strategyDay);
            entity.setTime(new Date());
            entity.setStatus(CommonStatus.ENABLED);
        }
        else {
            entity = entities.get(0);
            entities.remove(0);

            entity.setRecycled(isRecycled ? YesNoEnum.YES : YesNoEnum.NO);
            entity.setReExaminationDay(strategyDay);
            entity.setTime(new Date());
            entity.setStatus(CommonStatus.ENABLED);
        }
        entity = repository.save(entity);

        // delete others
        entities = repository.findByDepartmentId(departmentId, sort);
        for (int i=0; i<entities.size(); i++) {
            ReExaminationStrategyEntity tmp = entities.get(i);
            if (tmp.getId() == entity.getId()) {
                entities.remove(i);
                continue;
            }
            tmp.setStatus(CommonStatus.DELETED);
        }
        repository.save(entities);
        repository.delete(entities);


        logger.info("added is {}", entity);
        return beanConverter.convert(entity);
    }
}
