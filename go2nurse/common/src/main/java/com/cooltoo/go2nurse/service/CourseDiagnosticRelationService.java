package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseDiagnosticRelationBean;
import com.cooltoo.go2nurse.converter.CourseDiagnosticRelationBeanConverter;
import com.cooltoo.go2nurse.entities.CourseDiagnosticRelationEntity;
import com.cooltoo.go2nurse.repository.CourseDiagnosticRelationRepository;
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
@Service("CourseDiagnosticRelationService")
public class CourseDiagnosticRelationService {

    private static final Logger logger = LoggerFactory.getLogger(CourseDiagnosticRelationService.class);

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );
    @Autowired private CourseDiagnosticRelationRepository repository;
    @Autowired private CourseDiagnosticRelationBeanConverter beanConverter;

    //============================================================================
    //                 get
    //============================================================================
    public List<Long> judgeCourseInDiagnostic(long lDiagnosticId, List<Long> coursesId, String strStatus) {
        logger.info("judge course={} in diagnostic={} with status={}", coursesId, lDiagnosticId, strStatus);
        Long diagnosticId = Long.valueOf(lDiagnosticId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> validCourseIds = null;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                validCourseIds = repository.findByDiagnosticIdAndStatusAndCoursesId(diagnosticId, status, coursesId, sort);
            }
        }
        else {
            validCourseIds = repository.findByDiagnosticIdAndStatusAndCoursesId(diagnosticId, status, coursesId, sort);
        }
        if (null==validCourseIds) {
            validCourseIds = new ArrayList<>();
        }
        logger.info("count is {}", validCourseIds.size());
        return validCourseIds;
    }

    public List<Long> getCourseInDiagnostic(long lDiagnosticId, String strStatus) {
        logger.info("count course in diagnostic={} with status={}", lDiagnosticId, strStatus);
        Long diagnosticId = Long.valueOf(lDiagnosticId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> courseIds = null;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                courseIds = repository.findByDiagnosticIdAndStatus(diagnosticId, status, sort);
            }
        }
        else {
            courseIds = repository.findByDiagnosticIdAndStatus(diagnosticId, status, sort);
        }
        if (null==courseIds) {
            courseIds = new ArrayList<>();
        }
        logger.info("count is {}", courseIds.size());
        return courseIds;
    }

    public List<Long> getDiagnosticByCourseId(List<Long> courseIds, String strStatus) {
        logger.info("count diagnostic by courseId={} with status={}", courseIds, strStatus);
        if (VerifyUtil.isListEmpty(courseIds)) {
            return new ArrayList<>();
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> diagnosticIds = null;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                diagnosticIds = repository.findByCourseIdAndStatus(courseIds, status, sort);
            }
        }
        else {
            diagnosticIds = repository.findByCourseIdAndStatus(courseIds, status, sort);
        }
        if (null==diagnosticIds) {
            diagnosticIds = new ArrayList<>();
        }
        logger.info("count is {}", diagnosticIds.size());
        return diagnosticIds;
    }


    //============================================================================
    //                 update
    //============================================================================
    @Transactional
    public CourseDiagnosticRelationBean updateStatus(long lCourseId, long lDiagnosticId, String strStatus) {
        logger.info("update relation status to={} between course={} and diagnostic={}",
                strStatus, lCourseId, lDiagnosticId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        Long diagnosticId = Long.valueOf(lDiagnosticId);
        Long courseId = Long.valueOf(lCourseId);
        List<CourseDiagnosticRelationEntity> relations = repository.findByDiagnosticIdAndCourseId(diagnosticId, courseId, sort);
        if (VerifyUtil.isListEmpty(relations)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        CourseDiagnosticRelationEntity entity = relations.get(0);
        relations.remove(entity);

        entity.setStatus(status);
        entity = repository.save(entity);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        CourseDiagnosticRelationBean bean = beanConverter.convert(entity);
        logger.info("update relation={}", bean);
        return bean;
    }

    //============================================================================
    //                 add
    //============================================================================
    @Transactional
    public CourseDiagnosticRelationBean addCourseToDiagnostic(long lCourseId, long lDiagnosticId) {
        logger.info("add course={} to diagnostic={}", lCourseId, lDiagnosticId);
        Long diagnosticId = Long.valueOf(lDiagnosticId);
        Long courseId = Long.valueOf(lCourseId);
        CourseDiagnosticRelationEntity entity = null;
        List<CourseDiagnosticRelationEntity> relations = repository.findByDiagnosticIdAndCourseId(diagnosticId, courseId, sort);
        if (!VerifyUtil.isListEmpty(relations)) {
            entity = relations.get(0);
            relations.remove(entity);
        }
        else {
            entity = new CourseDiagnosticRelationEntity();
            entity.setDiagnosticId(diagnosticId);
            entity.setCourseId(courseId);
            entity.setTime(new Date());
        }

        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        CourseDiagnosticRelationBean bean = beanConverter.convert(entity);
        logger.info("add relation={}", bean);
        return bean;
    }
}
