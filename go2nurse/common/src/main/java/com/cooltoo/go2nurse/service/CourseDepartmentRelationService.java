package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseDepartmentRelationBean;
import com.cooltoo.go2nurse.converter.CourseDepartmentRelationBeanConverter;
import com.cooltoo.go2nurse.entities.CourseDepartmentRelationEntity;
import com.cooltoo.go2nurse.repository.CourseDepartmentRelationRepository;
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

/**
 * Created by hp on 2016/6/12.
 */
@Service("CourseDepartmentRelationService")
public class CourseDepartmentRelationService {

    private static final Logger logger = LoggerFactory.getLogger(CourseDepartmentRelationService.class);

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );
    @Autowired private CourseRepository courseRepository;
    @Autowired private CourseDepartmentRelationRepository repository;
    @Autowired private CourseDepartmentRelationBeanConverter beanConverter;

    //============================================================================
    //                 get
    //============================================================================
    public List<Long> judgeCourseInDepartment(int iDepartmentId, List<Long> coursesId, String strStatus) {
        logger.info("judge course={} in department={} with status={}", coursesId, iDepartmentId, strStatus);
        List<Long> validCourseIds = new ArrayList<>();
        if (VerifyUtil.isListEmpty(coursesId)) {
            return validCourseIds;
        }

        Integer departmentId = Integer.valueOf(iDepartmentId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                validCourseIds = repository.findByDepartmentIdAndStatusAndCoursesId(departmentId, status, coursesId, sort);
            }
        }
        else {
            validCourseIds = repository.findByDepartmentIdAndStatusAndCoursesId(departmentId, status, coursesId, sort);
        }
        logger.info("count is {}", validCourseIds.size());
        return validCourseIds;
    }

    public List<Long> getCourseInDepartment(int iDepartmentId, String strStatus) {
        logger.info("get course in department={} with status={}", iDepartmentId, strStatus);
        List<Long> courseIds = new ArrayList<>();
        Integer departmentId = Integer.valueOf(iDepartmentId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                courseIds = repository.findByDepartmentIdAndStatus(departmentId, status, sort);
            }
        }
        else {
            courseIds = repository.findByDepartmentIdAndStatus(departmentId, status, sort);
        }
        logger.info("count is {}", courseIds.size());
        return courseIds;
    }

    public List<Integer> getDepartmentByCourseId(List<Long> courseIds, String strStatus) {
        logger.info("get department by courseId={} with status={}", courseIds, strStatus);
        if (VerifyUtil.isListEmpty(courseIds)) {
            return new ArrayList<>();
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Integer> departmentIds = new ArrayList<>();
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                departmentIds = repository.findByCourseIdAndStatus(courseIds, status, sort);
            }
        }
        else {
            departmentIds = repository.findByCourseIdAndStatus(courseIds, status, sort);
        }
        logger.info("count is {}", departmentIds.size());
        return departmentIds;
    }




    //============================================================================
    //                 update
    //============================================================================
    @Transactional
    public CourseDepartmentRelationBean updateStatus(long lCourseId, int iDepartmentId, String strStatus) {
        logger.info("update relation status to={} between course={} and department={}",
                strStatus, lCourseId, iDepartmentId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        Integer departmentId = Integer.valueOf(iDepartmentId);
        Long courseId = Long.valueOf(lCourseId);
        List<CourseDepartmentRelationEntity> relations = repository.findByDepartmentIdAndCourseId(departmentId, courseId, sort);
        if (VerifyUtil.isListEmpty(relations)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        CourseDepartmentRelationEntity entity = relations.get(0);
        relations.remove(entity);

        entity.setStatus(status);
        entity = repository.save(entity);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        CourseDepartmentRelationBean bean = beanConverter.convert(entity);
        logger.info("update relation={}", bean);
        return bean;
    }

    //============================================================================
    //                 add
    //============================================================================
    @Transactional
    public CourseDepartmentRelationBean addCourseToDepartment(long lCourseId, int iDepartmentId) {
        logger.info("add course={} to department={}", lCourseId, iDepartmentId);
        Integer departmentId = Integer.valueOf(iDepartmentId);
        Long courseId = Long.valueOf(lCourseId);
        CourseDepartmentRelationEntity entity = null;
        List<CourseDepartmentRelationEntity> relations = repository.findByDepartmentIdAndCourseId(departmentId, courseId, sort);
        if (!VerifyUtil.isListEmpty(relations)) {
            entity = relations.get(0);
            relations.remove(entity);
        }
        else {
            entity = new CourseDepartmentRelationEntity();
            entity.setDepartmentId(departmentId);
            entity.setCourseId(courseId);
            entity.setTime(new Date());
        }

        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        CourseDepartmentRelationBean bean = beanConverter.convert(entity);
        logger.info("add relation={}", bean);
        return bean;
    }

    //=======================================================================
    //                    set
    //=======================================================================

    @Transactional
    public List<Integer> setCourseToDepartmentRelation(long courseId, List<Integer> departmentIds) {
        logger.info("set course_to_department_relationship courseId={} departmentIds={}",
                courseId, departmentIds);
        if (VerifyUtil.isListEmpty(departmentIds)) {
            logger.info("department is empty");
            return departmentIds;
        }
        if (!courseRepository.exists(courseId)) {
            logger.error("course not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        List<Integer> settingDepartmentIds = new ArrayList<>();
        List<Long> courseIds = Arrays.asList(new Long[]{courseId});
        List<Integer> existedDepartmentIds = repository.findByCourseIdAndStatus(courseIds, null, sort);
        if (VerifyUtil.isListEmpty(existedDepartmentIds)) {
            for (Integer departmentId : departmentIds) {
                if (null!=addCourseToDepartment(courseId, departmentId)) {
                    settingDepartmentIds.add(departmentId);
                }
            }
        }
        else {
            for (Integer existed : existedDepartmentIds) {
                if (departmentIds.contains(existed)) {
                    if (null!=updateStatus(courseId, existed, CommonStatus.ENABLED.name())) {
                        settingDepartmentIds.add(existed);
                        departmentIds.remove(existed);
                    }
                }
                else {
                    updateStatus(courseId, existed, CommonStatus.DISABLED.name());
                }
            }
            for (Integer needAdding : departmentIds) {
                if(null!=addCourseToDepartment(courseId, needAdding)) {
                    settingDepartmentIds.add(needAdding);
                }
            }
        }
        logger.info("set department ids is {}", departmentIds);
        return settingDepartmentIds;
    }
}
