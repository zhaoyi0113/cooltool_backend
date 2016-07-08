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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    public List<Long> judgeCourseInDiagnostic(long lDiagnosticId, List<Long> coursesId, String strStatus) {
        logger.info("judge course={} in diagnostic={} with status={}", coursesId, lDiagnosticId, strStatus);
        List<Long> validCourseIds = new ArrayList<>();
        if (VerifyUtil.isListEmpty(coursesId)) {
            return validCourseIds;
        }

        Long diagnosticId = Long.valueOf(lDiagnosticId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                validCourseIds = repository.findByDiagnosticIdAndStatusAndCoursesId(diagnosticId, status, coursesId, sort);
            }
        }
        else {
            validCourseIds = repository.findByDiagnosticIdAndStatusAndCoursesId(diagnosticId, status, coursesId, sort);
        }
        logger.info("count is {}", validCourseIds.size());
        return validCourseIds;
    }

    public List<Long> getCourseInDiagnostic(long lDiagnosticId, String strStatus) {
        logger.info("count course in diagnostic={} with status={}", lDiagnosticId, strStatus);
        Long diagnosticId = Long.valueOf(lDiagnosticId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> courseIds = new ArrayList<>();
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                courseIds = repository.findByDiagnosticIdAndStatus(diagnosticId, status, sort);
            }
        }
        else {
            courseIds = repository.findByDiagnosticIdAndStatus(diagnosticId, status, sort);
        }
        logger.info("count is {}", courseIds.size());
        return courseIds;
    }

    public Map<Long, List<Long>> getDiagnosticToCourseIds(List<Long> courseIds, CommonStatus status) {
        logger.info("get diagnostic to course ids map by courseIds={} status={}", courseIds, status);
        if (VerifyUtil.isListEmpty(courseIds)) {
            return new HashMap<>();
        }

        List<CourseDiagnosticRelationEntity> relations = repository.findByStatusAndCourseIdIn(status, courseIds, sort);
        Map<Long, List<Long>> diagnosticToCourses = new HashMap<>();
        if (!VerifyUtil.isListEmpty(relations)) {
            for (CourseDiagnosticRelationEntity relation : relations) {
                Long diagnosticId = relation.getDiagnosticId();
                Long courseId = relation.getCourseId();
                List<Long> courseIdsInDiagnostic = diagnosticToCourses.get(diagnosticId);
                if (null==courseIdsInDiagnostic) {
                    courseIdsInDiagnostic = new ArrayList<>();
                    diagnosticToCourses.put(diagnosticId, courseIdsInDiagnostic);
                }
                if (!courseIdsInDiagnostic.contains(courseId)) {
                    courseIdsInDiagnostic.add(courseId);
                }
            }
        }
        logger.info("diagnostic to courses map is {}", diagnosticToCourses);
        return diagnosticToCourses;
    }

    public List<Long> getDiagnosticByCourseId(List<Long> courseIds, String strStatus) {
        logger.info("count diagnostic by courseId={} with status={}", courseIds, strStatus);
        if (VerifyUtil.isListEmpty(courseIds)) {
            return new ArrayList<>();
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> diagnosticIds = new ArrayList<>();
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                diagnosticIds = repository.findByCourseIdAndStatus(courseIds, status, sort);
            }
        }
        else {
            diagnosticIds = repository.findByCourseIdAndStatus(courseIds, status, sort);
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

        List<Long> settingDiagnosticIds = new ArrayList<>();
        List<Long> courseIds = Arrays.asList(new Long[]{courseId});
        List<Long> existedDiagnosticIds = repository.findByCourseIdAndStatus(courseIds, null, sort);
        if (VerifyUtil.isListEmpty(existedDiagnosticIds)) {
            for (Long diagnosticId : diagnosticIds) {
                if (null!=addCourseToDiagnostic(courseId, diagnosticId)) {
                    settingDiagnosticIds.add(diagnosticId);
                }
            }
        }
        else {
            for (Long existed : existedDiagnosticIds) {
                if (diagnosticIds.contains(existed)) {
                    if (null!=updateStatus(courseId, existed, CommonStatus.ENABLED.name())) {
                        settingDiagnosticIds.add(existed);
                        diagnosticIds.remove(existed);
                    }
                }
                else {
                    updateStatus(courseId, existed, CommonStatus.DISABLED.name());
                }
            }
            for (Long needAdding : diagnosticIds) {
                if(null!=addCourseToDiagnostic(courseId, needAdding)) {
                    settingDiagnosticIds.add(needAdding);
                }
            }
        }
        logger.info("set diagnostics ids is {}", settingDiagnosticIds);
        return settingDiagnosticIds;
    }
}
