package com.cooltoo.nurse360.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.beans.Nurse360CourseHospitalRelationBean;
import com.cooltoo.nurse360.converters.Nurse360CourseHospitalRelationBeanConverter;
import com.cooltoo.nurse360.entities.Nurse360CourseHospitalRelationEntity;
import com.cooltoo.nurse360.repository.Nurse360CourseHospitalRelationRepository;
import com.cooltoo.nurse360.repository.Nurse360CourseRepository;
import com.cooltoo.nurse360.util.Nurse360Utility;
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
    public List<Nurse360CourseHospitalRelationBean> setCourseToHospital(long lCourseId, int iHospitalId, List<Integer> departmentIds) {
        logger.info("set course={} to hospital={} and departments={}", lCourseId, iHospitalId, departmentIds);
        Integer hospitalId = Integer.valueOf(iHospitalId);
        Long courseId = Long.valueOf(lCourseId);
        if (!courseRepository.exists(courseId)) {
            logger.error("the course not exist!");
            return new ArrayList<>();
        }
        if (!hospitalService.existHospital(hospitalId) && -1==hospitalId/*cooltoo's course*/){
            logger.error("the hospital not exist!");
            return new ArrayList<>();
        }

        List<Nurse360CourseHospitalRelationEntity> entities = new ArrayList<>();
        List<Nurse360CourseHospitalRelationEntity> relations = repository.findByCourseId(courseId, sort);
        // just add to hospital
        if (VerifyUtil.isListEmpty(departmentIds) || departmentIds.contains(0)) {
            Nurse360CourseHospitalRelationEntity entity;
            if (!VerifyUtil.isListEmpty(relations)) {
                entity = relations.get(0);
                relations.remove(entity);
            } else {
                entity = new Nurse360CourseHospitalRelationEntity();
                entity.setCourseId(courseId);
                entity.setTime(new Date());
            }
            entity.setHospitalId(hospitalId);
            entity.setDepartmentId(0);
            entity.setStatus(CommonStatus.ENABLED);
            entities.add(entity);
        }
        // add to department
        else {
            Nurse360CourseHospitalRelationEntity entity = null;
            for (Integer tmpId : departmentIds) {
                if (null==tmpId && tmpId<0) {
                    continue;
                }
                for (Nurse360CourseHospitalRelationEntity tmp : relations) {
                    if (tmp.getDepartmentId()==tmpId && tmp.getHospitalId()==hospitalId) {
                        entity = tmp;
                        break;
                    }
                }

                if (null!=entity) {
                    relations.remove(entity);
                } else {
                    entity = new Nurse360CourseHospitalRelationEntity();
                    entity.setHospitalId(hospitalId);
                    entity.setCourseId(courseId);
                    entity.setDepartmentId(tmpId);
                    entity.setTime(new Date());
                }
                entity.setStatus(CommonStatus.ENABLED);
                entities.add(entity);
                entity = null;
            }
        }

        entities = repository.save(entities);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        List<Nurse360CourseHospitalRelationBean> beans = entitiesToBeans(entities);
        logger.info("set relation={}", beans);
        return beans;
    }
}
