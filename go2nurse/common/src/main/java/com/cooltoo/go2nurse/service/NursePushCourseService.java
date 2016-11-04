package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.NursePushCourseBean;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.converter.NursePushCourseBeanConverter;
import com.cooltoo.go2nurse.entities.NursePushCourseEntity;
import com.cooltoo.go2nurse.repository.NursePushCourseRepository;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/4.
 */
@Service("NursePushCourseService")
public class NursePushCourseService {

    private static final Logger logger = LoggerFactory.getLogger(NursePushCourseService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time")
    );

    @Autowired private NursePushCourseRepository repository;
    @Autowired private NursePushCourseBeanConverter beanConverter;

    @Autowired private CourseService courseService;
    @Autowired private UserService userService;
    @Autowired private NurseRepository nurseRepository;

    //==========================================================================
    //              getter
    //==========================================================================

    public long countCoursePushed(Long nurseId, Long userId, Long patientId) {
        logger.info("count course pushed by nurseId={} to user={} patient={}", nurseId, userId, patientId);

        long count = 0;
        if (null==nurseId && null==userId && null==patientId) {
            return 0;
        }
        else {
            count = repository.countPushCourseByNurseIdAndUserIdAndRead(nurseId, userId, patientId, null);
        }

        logger.info("count course pushed by nurseId={} to user={} patient={}; count={}", nurseId, userId, patientId, count);
        return count;
    }

    public List<NursePushCourseBean> getCoursePushed(Long nurseId, Long userId, Long patientId, int pageIndex, int sizePerPage, boolean byPage) {
        logger.info("get course pushed by nurseId={} to user={} patient={}; at page={} sizePerPage={}, byPage={}",
                nurseId, userId, patientId, pageIndex, sizePerPage, byPage);

        Iterable<NursePushCourseEntity> orders = null;
        if (null==nurseId && null==userId && null==patientId) {
        }
        else if (byPage) {
            orders = repository.findPushCourseByNurseIdAndUserIdAndRead(nurseId, userId, patientId, null, sort);
        }
        else {
            PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
            orders = repository.findPushCourseByNurseIdAndUserIdAndRead(nurseId, userId, patientId, null, page);
        }

        List<NursePushCourseBean> beans = entitiesToBeans(orders);
        fillOtherProperties(beans);
        logger.info("get course pushed by nurseId={} to user={} patient={}; at page={} sizePerPage={}, byPage={}; size={}",
                nurseId, userId, patientId, pageIndex, sizePerPage, byPage, beans.size());
        return beans;
    }


    private List<NursePushCourseBean> entitiesToBeans(Iterable<NursePushCourseEntity> entities) {
        List<NursePushCourseBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (NursePushCourseEntity tmp : entities) {
            NursePushCourseBean tmpBean = beanConverter.convert(tmp);
            beans.add(tmpBean);
        }
        return beans;
    }

    private void fillOtherProperties(List<NursePushCourseBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }
        List<Long> coursesId = new ArrayList<>();
        for (NursePushCourseBean bean :beans) {
            long courseId = bean.getCourseId();
            if (!coursesId.contains(courseId)) {
                coursesId.add(courseId);
            }
        }

        Map<Long, CourseBean> courseIdToBean = courseService.getCourseMapByStatusAndIds(CourseStatus.ENABLE, coursesId, null, null);
        for (NursePushCourseBean tmp : beans) {
            CourseBean course = courseIdToBean.get(tmp.getCourseId());
            if (null!=course) {
                tmp.setCourse(course);
            }
        }
    }

    //==========================================================================
    //              delete
    //==========================================================================
    @Transactional
    public List<Long> deletePushedCourseReadStatus(Long nurseId, Long userId, Long patientId) {
        List<Long> count = new ArrayList<>();
        if (null==nurseId && null==userId && null==patientId) {
        }
        else {
            List<NursePushCourseEntity> set = repository.findPushCourseByNurseIdAndUserIdAndRead(nurseId, userId, patientId, null, sort);
            for (NursePushCourseEntity tmp : set) {
                count.add(tmp.getId());
            }
            repository.delete(set);
        }

        logger.info("delete pushed courses by nurseId={} userId={} patientId={}, effected size={}",
                nurseId, userId, patientId, count);
        return count;
    }

    @Transactional
    public boolean deletePushCourseReadStatus(long coursePushedId) {
        boolean success = false;
        NursePushCourseEntity one = repository.findOne(coursePushedId);
        if (null!=one) {
            repository.delete(one);
            success = true;
        }
        logger.info("delete pushed courses by coursePushedId={}, success={}", coursePushedId, success);

        return success;
    }

    //==========================================================================
    //              update
    //==========================================================================
    @Transactional
    public List<Long> updatePushedCourseReadStatus(Long userId, Long patientId, Long courseId, ReadingStatus read) {
        List<Long> count = new ArrayList<>();
        if (null==userId && null==courseId && null==read) {
        }
        else {
            List<NursePushCourseEntity> set = repository.findPushCourseByNurseIdAndUserIdAndRead(null, userId, patientId, read, sort);
            for (NursePushCourseEntity tmp : set) {
                tmp.setRead(read);
                count.add(tmp.getId());
            }
            repository.save(set);
        }

        logger.info("change reading status by userId={} patientId={} courseId={} to readingStatus={}, effected size={}",
                userId, patientId, courseId, read, count);
        return count;
    }

    @Transactional
    public boolean updatePushedCourseReadStatus(long coursePushedId, ReadingStatus read) {
        boolean success = false;
        NursePushCourseEntity one = repository.findOne(coursePushedId);
        if (null!=one) {
            one.setRead(read);
            repository.save(one);
            success = true;
        }
        logger.info("change reading status by coursePushedId={} to readingStatus={}, success={}", coursePushedId, read, success);

        return success;
    }

    //==========================================================================
    //                       add
    //==========================================================================
    @Transactional
    public NursePushCourseBean pushCourseToUser(long nurseId, long userId, long patientId, long courseId) {
        logger.info("add course pushed by nurseId={} to user={} patient={}; course={}", nurseId, userId, patientId, courseId);

        //==================================================================
        //                   check parameters
        //==================================================================
        if (!userService.existUser(userId)) {
            logger.error("userId not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!courseService.existCourse(courseId)) {
            logger.error("course not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!nurseRepository.exists(nurseId)) {
            logger.error("nurse not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        //=====================================================
        //                   get order exist
        //=====================================================
        NursePushCourseEntity coursePushed = new NursePushCourseEntity();
        coursePushed.setNurseId(nurseId);
        coursePushed.setUserId(userId);
        coursePushed.setPatientId(patientId);
        coursePushed.setCourseId(courseId);
        coursePushed.setRead(ReadingStatus.UNREAD);
        coursePushed.setTime(new Date());
        coursePushed = repository.save(coursePushed);

        NursePushCourseBean bean = beanConverter.convert(coursePushed);
        logger.info("doctor order in department is=={}", bean);
        return bean;
    }
}
