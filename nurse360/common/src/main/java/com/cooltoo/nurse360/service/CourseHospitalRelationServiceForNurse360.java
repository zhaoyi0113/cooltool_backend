package com.cooltoo.nurse360.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.nurse360.beans.Nurse360CourseHospitalRelationBean;
import com.cooltoo.nurse360.converters.Nurse360CourseHospitalRelationBeanConverter;
import com.cooltoo.nurse360.entities.Nurse360CourseHospitalRelationEntity;
import com.cooltoo.nurse360.repository.Nurse360CourseHospitalRelationRepository;
import com.cooltoo.nurse360.repository.Nurse360CourseRepository;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
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
@Service("CourseHospitalRelationServiceForNurse360")
public class CourseHospitalRelationServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(CourseHospitalRelationServiceForNurse360.class);

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private Nurse360CourseRepository courseRepository;
    @Autowired private Nurse360CourseHospitalRelationRepository repository;
    @Autowired private Nurse360CourseHospitalRelationBeanConverter beanConverter;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private HospitalDepartmentRepository departmentRepository;
    @Autowired private Nurse360Utility utility;

    //============================================================================
    //                 get
    //============================================================================
    public List<HospitalBean> getHospitalByCourseId(long courseId, String strStatus) {
        logger.info("get hospital by course id={} and status={}", courseId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Integer> hospitalIds = repository.findHospitalIdByCourseIdAndStatus(courseId, status, sort);
        List<HospitalBean> hospitals = hospitalService.getHospitalByIds(hospitalIds);
        logger.info("hospital is {}", hospitals);
        return hospitals;
    }

    public List<HospitalDepartmentBean> getDepartmentByCourseId(long courseId, String strStatus) {
        logger.info("get department by course id={} and status={}", courseId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Integer> departmentIds = repository.findDepartmentIdByCourseIdAndStatus(courseId, status, sort);
        List<HospitalDepartmentBean> departments = departmentService.getByIds(departmentIds, utility.getHttpPrefixForNurseGo());
        logger.info("department is {}", departments);
        return departments;
    }

    public List<Long> getCourseInHospitalAndDepartment(Integer hospitalId, Integer departmentId, String strStatus) {
        logger.info("get courseIds in hospital={} department={} with status={}", hospitalId, departmentId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> courseIds = new ArrayList<>();
        if (null==status && !"ALL".equalsIgnoreCase(strStatus)) {
        }
        else {
            if (null==departmentId) {
                courseIds = repository.findByHospitalIdAndStatus(hospitalId, status);
            }
            else {
                courseIds = repository.findByHospitalIdAndDepartmentIdAndStatus(hospitalId, departmentId, status);
            }
        }
        logger.info("count is {}", courseIds.size());
        return courseIds;
    }

    private List<Nurse360CourseHospitalRelationBean> entitiesToBeans(Iterable<Nurse360CourseHospitalRelationEntity> entities) {
        List<Nurse360CourseHospitalRelationBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }

        for (Nurse360CourseHospitalRelationEntity tmp : entities) {
            beans.add(beanConverter.convert(tmp));
        }
        return beans;
    }

    //============================================================================
    //                 set
    //============================================================================
    @Transactional
    public long deleteRelationByCourseIds(List<Long> courseIds) {
        logger.info("delete relation by course={}", courseIds);
        long count = 0;
        if (VerifyUtil.isListEmpty(courseIds)) {
            logger.info("delete relation by size={}", count);
            return count;
        }

        List<Nurse360CourseHospitalRelationEntity> relations = repository.findByCourseIdIn(courseIds);
        if (!VerifyUtil.isListEmpty(relations)) {
            count = relations.size();
        }
        repository.delete(relations);

        logger.info("delete relation by size={}", count);
        return count;
    }

    //============================================================================
    //                 set
    //============================================================================
    @Transactional
    public List<Nurse360CourseHospitalRelationBean> setCourseToHospital(long lCourseId, List<Integer> departmentIds) {
        logger.info("set course={} to departments={}", lCourseId, departmentIds);
        Long courseId = Long.valueOf(lCourseId);
        if (!courseRepository.exists(courseId)) {
            logger.error("the course not exist!");
            return new ArrayList<>();
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

        List<Nurse360CourseHospitalRelationEntity> relations = new ArrayList<>();
        List<Nurse360CourseHospitalRelationEntity> existedRelations = repository.findByCourseId(courseId, sort);
        // add to department
        for (HospitalDepartmentEntity tmpDep : departments) {
            Nurse360CourseHospitalRelationEntity tmp = null;
            if (VerifyUtil.isListEmpty(existedRelations)) {
                tmp = new Nurse360CourseHospitalRelationEntity();
            }
            else {
                tmp = existedRelations.remove(0);
            }
            tmp.setCourseId(courseId);
            tmp.setHospitalId(tmpDep.getHospitalId());
            tmp.setDepartmentId(tmpDep.getId());
            tmp.setStatus(CommonStatus.ENABLED);
            tmp.setTime(new Date());
            relations.add(tmp);
        }

        relations = repository.save(relations);

        if (!VerifyUtil.isListEmpty(existedRelations)) {
            repository.delete(existedRelations);
        }

        existedRelations = repository.findByCourseId(courseId, new Sort(new Sort.Order(Sort.Direction.ASC, "id")));
        for (int i = 0; i < existedRelations.size(); i ++) {
            Nurse360CourseHospitalRelationEntity tmp1 = existedRelations.get(i);
            boolean exist = false;
            for (Nurse360CourseHospitalRelationEntity tmp2 : relations) {
                if (tmp1.getId() == tmp2.getId()) {
                    exist = true;
                    break;
                }
            }
            if (exist) {
                existedRelations.remove(tmp1);
                i--;
            }
        }
        if (!VerifyUtil.isListEmpty(existedRelations)) {
            repository.delete(existedRelations);
        }

        List<Nurse360CourseHospitalRelationBean> beans = entitiesToBeans(relations);
        logger.info("set relation={}", beans);
        return beans;
    }
}
