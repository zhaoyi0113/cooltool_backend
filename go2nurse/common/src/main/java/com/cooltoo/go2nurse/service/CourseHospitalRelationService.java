package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseHospitalRelationBean;
import com.cooltoo.go2nurse.converter.CourseHospitalRelationBeanConverter;
import com.cooltoo.go2nurse.entities.CourseHospitalRelationEntity;
import com.cooltoo.go2nurse.repository.CourseHospitalRelationRepository;
import com.cooltoo.go2nurse.repository.CourseRepository;
import com.cooltoo.repository.HospitalRepository;
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
@Service("CourseHospitalRelationService")
public class CourseHospitalRelationService {

    private static final Logger logger = LoggerFactory.getLogger(CourseHospitalRelationService.class);

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private CourseHospitalRelationRepository repository;
    @Autowired private CourseHospitalRelationBeanConverter beanConverter;
    @Autowired private CourseRepository courseRepository;
    @Autowired private HospitalRepository hospitalRepository;

    //============================================================================
    //                 get
    //============================================================================
    public List<Integer> getHospitalByCourseId(long courseId, String strStatus) {
        logger.info("get hospital id by course id ={} and status", courseId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Integer> hospitalIds = repository.findByCourseIdAndStatus(courseId, status, sort);
        logger.info("hospital id is {}", hospitalIds);
        return hospitalIds;
    }

    public List<Long> getCourseInHospital(int iHospitalId, String strStatus) {
        logger.info("count course in hospital={} with status={}", iHospitalId, strStatus);
        Integer hospitalId = Integer.valueOf(iHospitalId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> courseIds = new ArrayList<>();
        if (null==status && !"ALL".equalsIgnoreCase(strStatus)) {
        }
        else {
            courseIds = repository.findByHospitalIdAndStatus(hospitalId, status, sort);
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

    //=======================================================================
    //                    set
    //=======================================================================

    @Transactional
    public List<Integer> setCourseToHospitalRelation(long courseId, List<Integer> hospitalIds) {
        logger.info("set course_to_hospital_relationship courseId={} hospitalIds={}",
                courseId, hospitalIds);
        if (VerifyUtil.isListEmpty(hospitalIds)) {
            logger.info("hospitalIds is empty");
            return hospitalIds;
        }
        if (!courseRepository.exists(courseId)) {
            logger.error("course not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<HospitalEntity> existsHospital = hospitalRepository.findByIdIn(hospitalIds);
        if (VerifyUtil.isListEmpty(existsHospital)) {
            if (!hospitalIds.contains(new Integer(-1)/*cooltoo*/)) {
                throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
            }
            hospitalIds.clear();
            hospitalIds.add(new Integer(-1));
        }
        else {
            if (hospitalIds.contains(new Integer(-1)/*cooltoo*/)) {
                hospitalIds.clear();
                hospitalIds.add(new Integer(-1));
            }
            for (HospitalEntity hospital : existsHospital) {
                hospitalIds.add(hospital.getId());
            }
        }

        List<Integer> settingHospitalIds = new ArrayList<>();
        List<Integer> existedHospitalIds = repository.findByCourseIdAndStatus(courseId, null, sort);
        if (VerifyUtil.isListEmpty(existedHospitalIds)) {
            for (Integer hospitalId : hospitalIds) {
                if (null!=addCourseToHospital(courseId, hospitalId)) {
                    settingHospitalIds.add(hospitalId);
                }
            }
        }
        else {
            for (Integer existed : existedHospitalIds) {
                if (hospitalIds.contains(existed)) {
                    if (null!=updateStatus(courseId, existed, CommonStatus.ENABLED.name())) {
                        settingHospitalIds.add(existed);
                        hospitalIds.remove(existed);
                    }
                }
                else {
                    updateStatus(courseId, existed, CommonStatus.DELETED.name());
                }
            }
            for (Integer needAdding : hospitalIds) {
                if(null!=addCourseToHospital(courseId, needAdding)) {
                    settingHospitalIds.add(needAdding);
                }
            }
        }
        logger.info("set hospitalIds ids is {}", settingHospitalIds);
        return settingHospitalIds;
    }
}
