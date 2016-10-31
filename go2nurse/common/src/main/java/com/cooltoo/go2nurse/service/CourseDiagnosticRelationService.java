package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseDiagnosticRelationBean;
import com.cooltoo.go2nurse.converter.CourseDiagnosticRelationBeanConverter;
import com.cooltoo.go2nurse.entities.CourseDiagnosticRelationEntity;
import com.cooltoo.go2nurse.repository.CourseDiagnosticRelationRepository;
import com.cooltoo.go2nurse.repository.CourseRepository;
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
    @Autowired private CourseRepository courseRepository;
    @Autowired private CourseDiagnosticRelationRepository repository;
    @Autowired private CourseDiagnosticRelationBeanConverter beanConverter;

    //============================================================================
    //                 get
    //============================================================================

    public List<Long> getDiagnosticByCourseId(List<Long> courseIds) {
        logger.info("count diagnostic by courseId={} with status={}", courseIds);
        if (VerifyUtil.isListEmpty(courseIds)) {
            return new ArrayList<>();
        }
        List<Long> diagnosticIds = repository.findDiagnosticIdByStatusCourseId(courseIds, null, sort);
        logger.info("count is {}", diagnosticIds.size());
        return diagnosticIds;
    }


    //============================================================================
    //                 delete relation permanently
    //============================================================================

    @Transactional
    public List<Long> deleteRelationPermanentlyByCourseIds(List<Long> courseIds) {
        logger.info("delete relation by courseIds={}", courseIds);
        if (VerifyUtil.isListEmpty(courseIds)) {
            return new ArrayList<>();
        }
        List<CourseDiagnosticRelationEntity> set = repository.findByStatusCoursesIds(null, courseIds);
        repository.delete(set);
        return courseIds;
    }


    //============================================================================
    //                 update
    //============================================================================
    @Transactional
    public CourseDiagnosticRelationBean updateStatus(long lCourseId, long lDiagnosticId, String strStatus) {
        logger.info("update relation status to={} between course={} and diagnostic={}",
                strStatus, lCourseId, lDiagnosticId);

        CourseDiagnosticRelationEntity entity = null;
        Long diagnosticId = Long.valueOf(lDiagnosticId);
        Long courseId = Long.valueOf(lCourseId);

        List<CourseDiagnosticRelationEntity> relations = repository.findByDiagnosticIdAndCourseId(diagnosticId, courseId, sort);
        if (VerifyUtil.isListEmpty(relations)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        entity = relations.get(0);
        if (!CommonStatus.ENABLED.name().equalsIgnoreCase(strStatus)) {
            repository.delete(relations);
        }
        else {
            relations.remove(entity);
            entity.setStatus(CommonStatus.ENABLED);
            repository.save(entity);
            if (!VerifyUtil.isListEmpty(relations)) {
                repository.delete(relations);
            }
        }

        CourseDiagnosticRelationBean bean = beanConverter.convert(entity);
        bean.setStatus(CommonStatus.parseString(strStatus));
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

    //=======================================================================
    //                    set
    //=======================================================================

    @Transactional
    public List<Long> setCourseToDiagnosticRelation(long courseId, List<Long> diagnosticIds) {
        logger.info("set course_to_diagnostic_relationship courseId={} diagnosticIds={}",
                courseId, diagnosticIds);
        if (VerifyUtil.isListEmpty(diagnosticIds)) {
            logger.info("diagnostics is empty");
            return diagnosticIds;
        }
        if (!courseRepository.exists(courseId)) {
            logger.error("course not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        List<CourseDiagnosticRelationEntity> existedRelation = repository.findByCourseId(courseId);
        List<CourseDiagnosticRelationEntity> relations = new ArrayList<>();
        for (Long diagId : diagnosticIds) {
            CourseDiagnosticRelationEntity tmp = null;
            if (VerifyUtil.isListEmpty(existedRelation)) {
                tmp = new CourseDiagnosticRelationEntity();
            }
            else {
                tmp = existedRelation.remove(0);
            }
            tmp.setCourseId(courseId);
            tmp.setDiagnosticId(diagId);
            tmp.setStatus(CommonStatus.ENABLED);
            tmp.setTime(new Date());
            relations.add(tmp);
        }

        relations = repository.save(relations);
        if (!VerifyUtil.isListEmpty(existedRelation)) {
            repository.delete(existedRelation);
        }

        existedRelation = repository.findByCourseId(courseId);
        for (int i = 0; i < existedRelation.size(); i ++) {
            CourseDiagnosticRelationEntity tmp1 = existedRelation.get(i);
            boolean exist = false;
            for (CourseDiagnosticRelationEntity tmp2 : relations) {
                if (tmp1.getId() == tmp2.getId()) {
                    exist = true;
                    break;
                }
            }
            if (exist) {
                existedRelation.remove(tmp1);
                i--;
            }
        }
        if (!VerifyUtil.isListEmpty(existedRelation)) {
            repository.delete(existedRelation);
        }
        logger.info("set diagnostics ids is {}", relations);
        return diagnosticIds;
    }
}
