package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseHospitalRelationBean;
import com.cooltoo.go2nurse.converter.CourseHospitalRelationBeanConverter;
import com.cooltoo.go2nurse.entities.CourseHospitalRelationEntity;
import com.cooltoo.go2nurse.repository.CourseHospitalRelationRepository;
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
 * Created by hp on 2016/6/12.
 */
@Service("CourseHospitalRelationService")
public class CourseHospitalRelationService {

    private static final Logger logger = LoggerFactory.getLogger(CourseHospitalRelationService.class);

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );
    @Autowired private CourseHospitalRelationRepository repository;
    @Autowired private CourseHospitalRelationBeanConverter beanConverter;

    //============================================================================
    //                 get
    //============================================================================
    public List<Long> getCourseInHospital(int iHospitalId, String strStatus) {
        logger.info("count course in hospital={} with status={}", iHospitalId, strStatus);
        Integer hospitalId = Integer.valueOf(iHospitalId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> courseIds = null;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                courseIds = repository.findByHospitalIdAndStatus(hospitalId, status, sort);
            }
        }
        else {
            courseIds = repository.findByHospitalIdAndStatus(hospitalId, status, sort);
        }
        if (null==courseIds) {
            courseIds = new ArrayList<>();
        }
        logger.info("count is {}", courseIds.size());
        return courseIds;
    }

    //============================================================================
    //                 update
    //============================================================================
    @Transactional
    public CourseHospitalRelationBean updateStatus(long lCourseId, int iHospitalId, String strStatus) {
        logger.info("update relation status to={} between course={} and hospital={}",
                strStatus, lCourseId, iHospitalId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        Integer hospitalId = Integer.valueOf(iHospitalId);
        Long courseId = Long.valueOf(lCourseId);
        List<CourseHospitalRelationEntity> relations = repository.findByHospitalIdAndCourseId(hospitalId, courseId, sort);
        if (VerifyUtil.isListEmpty(relations)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        CourseHospitalRelationEntity entity = relations.get(0);
        relations.remove(entity);

        entity.setStatus(status);
        entity = repository.save(entity);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        CourseHospitalRelationBean bean = beanConverter.convert(entity);
        logger.info("update relation={}", bean);
        return bean;
    }

    //============================================================================
    //                 add
    //============================================================================
    @Transactional
    public CourseHospitalRelationBean addCourseToHospital(long lCourseId, int iHospitalId) {
        logger.info("add course={} to hospital={}", lCourseId, iHospitalId);
        Integer hospitalId = Integer.valueOf(iHospitalId);
        Long courseId = Long.valueOf(lCourseId);
        CourseHospitalRelationEntity entity = null;
        List<CourseHospitalRelationEntity> relations = repository.findByHospitalIdAndCourseId(hospitalId, courseId, sort);
        if (!VerifyUtil.isListEmpty(relations)) {
            entity = relations.get(0);
            relations.remove(entity);
        }
        else {
            entity = new CourseHospitalRelationEntity();
            entity.setHospitalId(hospitalId);
            entity.setCourseId(courseId);
            entity.setTime(new Date());
        }

        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        CourseHospitalRelationBean bean = beanConverter.convert(entity);
        logger.info("add relation={}", bean);
        return bean;
    }
}
