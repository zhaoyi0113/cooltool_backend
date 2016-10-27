package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.converter.CourseDepartmentRelationBeanConverter;
import com.cooltoo.go2nurse.entities.CourseDepartmentRelationEntity;
import com.cooltoo.go2nurse.repository.CourseDepartmentRelationRepository;
import com.cooltoo.go2nurse.repository.CourseRepository;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.repository.HospitalRepository;
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
@Service("CourseDepartmentRelationService")
public class CourseDepartmentRelationService {

    private static final Logger logger = LoggerFactory.getLogger(CourseDepartmentRelationService.class);

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private CourseDepartmentRelationRepository repository;
    @Autowired private CourseDepartmentRelationBeanConverter beanConverter;

    @Autowired private CourseRepository courseRepository;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private HospitalDepartmentRepository departmentRepository;

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
        if (null!=status || "ALL".equalsIgnoreCase(strStatus)) {
            validCourseIds = repository.findCourseIdByDepartmentAndStatusAndCourses(departmentId, status, coursesId, sort);
        }
        logger.info("count is {}", validCourseIds.size());
        return validCourseIds;
    }

    public List<Long> getCourseIdInDepartment(int iDepartmentId, String strStatus) {
        logger.info("get course in department={} with status={}", iDepartmentId, strStatus);
        List<Long> courseIds = new ArrayList<>();
        Integer departmentId = Integer.valueOf(iDepartmentId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status || "ALL".equalsIgnoreCase(strStatus)) {
            courseIds = repository.findCourseIdByDepartmentIdAndStatus(departmentId, status, sort);
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
        if (null!=status || "ALL".equalsIgnoreCase(strStatus)) {
            departmentIds = repository.findDepartmentIdByCourseIdAndStatus(courseIds, status, sort);
        }
        logger.info("count is {}", departmentIds.size());
        return departmentIds;
    }

    public List<Integer> getHospitalByCourseId(long courseId, String strStatus) {
        logger.info("get hospital id by course id ={} and status", courseId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Integer> hospitalIds = repository.findHospitalIdByCourseIdAndStatus(courseId, status, sort);
        logger.info("hospital id is {}", hospitalIds);
        return hospitalIds;
    }

    //=======================================================================
    //                    set
    //=======================================================================

    @Transactional
    public List<Integer> setCourseToDepartmentRelation(long courseId, List<Integer> departmentIds) {
        logger.info("set course_to_department_relationship courseId={} departmentIds={}",
                courseId, departmentIds);
        // check parameters
        if (!courseRepository.exists(courseId)) {
            logger.error("course not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (VerifyUtil.isListEmpty(departmentIds)) {
            logger.error("departmentIds is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean hasCooltoo = departmentIds.contains(Integer.valueOf(-1));
        departmentIds.remove(Integer.valueOf(-1));

        List<HospitalDepartmentEntity> departments = departmentRepository.findByIdIn(departmentIds, new Sort(new Sort.Order(Sort.Direction.ASC, "id")));
        if (VerifyUtil.isListEmpty(departments) && !hasCooltoo) {
            logger.error("department not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (hasCooltoo) {
            HospitalDepartmentEntity cooltoo = new HospitalDepartmentEntity();
            cooltoo.setId(0);
            cooltoo.setHospitalId(-1);
            departments.add(cooltoo);
        }


        List<CourseDepartmentRelationEntity> existedRelation = repository.findByCourseId(courseId);
        List<CourseDepartmentRelationEntity> relations = new ArrayList<>();
        for (HospitalDepartmentEntity tmpDep : departments) {
            CourseDepartmentRelationEntity tmp = null;
            if (VerifyUtil.isListEmpty(existedRelation)) {
                tmp = new CourseDepartmentRelationEntity();
            }
            else {
                tmp = existedRelation.remove(0);
            }
            tmp.setCourseId(courseId);
            tmp.setHospitalId(tmpDep.getHospitalId());
            tmp.setDepartmentId(tmpDep.getId());
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
            CourseDepartmentRelationEntity tmp1 = existedRelation.get(i);
            boolean exist = false;
            for (CourseDepartmentRelationEntity tmp2 : relations) {
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

        return departmentIds;
    }
}
