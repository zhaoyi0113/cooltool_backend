package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.UserCourseRelationBean;
import com.cooltoo.go2nurse.beans.UserHospitalizedRelationBean;
import com.cooltoo.go2nurse.converter.UserCourseRelationBeanConverter;
import com.cooltoo.go2nurse.entities.UserCourseRelationEntity;
import com.cooltoo.go2nurse.repository.CourseRepository;
import com.cooltoo.go2nurse.repository.UserCourseRelationRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
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
 * Created by hp on 2016/6/15.
 */
@Service("UserCourseRelationService")
public class UserCourseRelationService {

    private static final Logger logger = LoggerFactory.getLogger(UserCourseRelationService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private UserDiagnosticPointRelationService userDiagnosticRelationService;
    @Autowired private UserHospitalizedRelationService userHospitalizedRelationService;
    @Autowired private CourseService courseService;

    @Autowired private UserCourseRelationRepository repository;
    @Autowired private UserCourseRelationBeanConverter beanConverter;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;

    //===================================================
    //               getting for user
    //===================================================

    public void setCourseReadStatus(Long userId, List<CourseBean> courses) {
        logger.info("set user={} course read status", userId);
        if (VerifyUtil.isListEmpty(courses) || null==userId) {
            return;
        }

        List<Long> readCourseIds = getRelationCourseId(userId, "read", CommonStatus.ENABLED.name());
        if (VerifyUtil.isListEmpty(readCourseIds) || null==readCourseIds) {
            return;
        }

        for (CourseBean course : courses) {
            course.setReading(readCourseIds.contains(course.getId()) ? ReadingStatus.READ : ReadingStatus.UNREAD);
        }
    }

    public boolean isUserSelectedHospitalCoursesNow(long userId) {
        logger.info("Is user={} selected hospital courses now", userId);
        boolean userHasSelectedHospitalCourse = false;
        // get current diagnostic group ID
        long currentDiagnosticGroupId = userDiagnosticRelationService.getUserCurrentGroupId(userId);
        // the hospital that user hospitalized
        List<UserHospitalizedRelationBean> userHospitalized =
                userHospitalizedRelationService.getUserHospitalizedRelationByGroupId(userId, currentDiagnosticGroupId);
        for (UserHospitalizedRelationBean hospitalized : userHospitalized) {
            // has leave this department
            if (!YesNoEnum.YES.equals(hospitalized.getHasLeave())) {
                userHasSelectedHospitalCourse = true;
                break;
            }
        }
        logger.info("user has selected={} now", userHasSelectedHospitalCourse);
        return userHasSelectedHospitalCourse;
    }

    // 历史课程
    public List<CourseBean> getUserAllCoursesRead(long userId) {
        logger.info("get all courses read by userId={}", userId);

        // get user read courses
        List<ReadingStatus> readAndUnread = ReadingStatus.getAllStatus();
        readAndUnread.remove(ReadingStatus.DELETED);
        readAndUnread.remove(ReadingStatus.UNREAD);
        List<Long> readCourseIds = repository.findCourseIdByUserIdAndReadStatusAndStatus(userId, readAndUnread, CommonStatus.ENABLED, sort);
        List<CourseBean> readCourses = courseService.getCourseByStatusAndIds(null, readCourseIds, null, null);

        List<CourseBean> returnValue = new ArrayList<>();
        for (Long courseId : readCourseIds) {
            for (CourseBean course : readCourses) {
                if (course.getId() == courseId) {
                    returnValue.add(course);
                    break;
                }
            }
        }
        logger.info("courses count is {}", returnValue.size());
        return returnValue;
    }

    // 历史课程
    public List<CourseBean> getUserAllCoursesRead(long userId, int pageIndex, int sizePerPage) {
        logger.info("get courses read by userId={} at pageIndex={} size={}", userId, pageIndex, sizePerPage);

        // get user read courses
        List<ReadingStatus> read = new ArrayList<>();
        read.add(ReadingStatus.READ);
        List<Long> readCourseIds = repository.findCourseIdByUserIdAndReadStatusAndStatus(userId, read, CommonStatus.ENABLED, sort);
        List<Long> readCoursePageIds = new ArrayList<>();
        int startIndex = pageIndex*sizePerPage;
        int endIndex = startIndex + sizePerPage;
        for (int i=startIndex; i<readCourseIds.size(); i++) {
            if (i<endIndex) {
                readCoursePageIds.add(readCourseIds.get(i));
                continue;
            }
            else {
                break;
            }
        }
        List<CourseBean> readCourses = courseService.getCourseByStatusAndIds(null, readCoursePageIds, null, null);

        List<CourseBean> returnValue = new ArrayList<>();
        for (Long courseId : readCoursePageIds) {
            for (CourseBean course : readCourses) {
                if (course.getId() == courseId) {
                    returnValue.add(course);
                    break;
                }
            }
        }
        logger.info("courses count is {}", returnValue.size());
        return returnValue;
    }

    //===================================================
    //               get for admin user
    //===================================================

    public long countByUserAndReadStatusAndStatus(long userId, String strReadingStatuses, String strStatus) {
        logger.info("count the course with user={} readingStatus={} status={}",
                userId, strReadingStatuses, strStatus);

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<ReadingStatus> readingStatuses;
        if ("ALL".equalsIgnoreCase(strReadingStatuses)) {
            readingStatuses = ReadingStatus.getAllStatus();
        }
        else {
            readingStatuses = ReadingStatus.parseStatuses(strReadingStatuses);
        }

        long count = repository.countByUserIdAndReadStatusAndStatus(userId, readingStatuses, status);
        logger.info("count is {}", count);
        return count;
    }

    public List<Long> getRelationCourseId(long userId, String strReadingStatuses, String strStatus) {
        logger.info("get the course with user={} readingStatus={} status={}",
                userId, strReadingStatuses, strStatus);

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<ReadingStatus> readingStatuses;
        if ("ALL".equalsIgnoreCase(strReadingStatuses)) {
            readingStatuses = ReadingStatus.getAllStatus();
        }
        else {
            readingStatuses = ReadingStatus.parseStatuses(strReadingStatuses);
        }

        List<Long> resultSet = repository.findCourseIdByUserIdAndReadStatusAndStatus(userId, readingStatuses, status, sort);
        logger.info("count is {}", resultSet.size());
        return resultSet;
    }

    public List<UserCourseRelationBean> getRelation(long userId, String strReadingStatuses, String strStatus) {
        logger.info("get the course with user={} readingStatus={} status={}",
                userId, strReadingStatuses, strStatus);

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<ReadingStatus> readingStatuses;
        if ("ALL".equalsIgnoreCase(strReadingStatuses)) {
            readingStatuses = ReadingStatus.getAllStatus();
        }
        else {
            readingStatuses = ReadingStatus.parseStatuses(strReadingStatuses);
        }

        List<UserCourseRelationEntity> resultSet = repository.findByUserIdAndReadStatusAndStatus(userId, readingStatuses, status, sort);
        List<UserCourseRelationBean> relations = entitiesToBeans(resultSet);
        logger.info("count is {}", relations.size());
        return relations;
    }

    private List<UserCourseRelationBean> entitiesToBeans(Iterable<UserCourseRelationEntity> entities) {
        List<UserCourseRelationBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        UserCourseRelationBean bean;
        for (UserCourseRelationEntity entity : entities) {
            bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }


    //===================================================
    //               add
    //===================================================
    @Transactional
    public List<UserCourseRelationBean> addUserCourseRelation(long userId, List<Long> courseIds) {
        logger.info("add course to user={}, courseIds={}, point_time={}", userId, courseIds);
        if (!userRepository.exists(userId)) {
            logger.error("user not exist");
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        if (VerifyUtil.isListEmpty(courseIds)) {
            logger.error("course is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        for (Long courseId : courseIds) {
            if (!courseRepository.exists(courseId)) {
                logger.error("course id={} not exists", courseId);
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        UserCourseRelationEntity entity = null;
        List<UserCourseRelationBean> relations = new ArrayList<>();
        for (int i = 0, count = courseIds.size(); i < count; i++) {
            Long courseId = courseIds.get(i);
            entity = new UserCourseRelationEntity();
            entity.setUserId(userId);
            entity.setCourseId(courseId);
            entity.setReadingStatus(ReadingStatus.UNREAD);
            entity.setTime(new Date());
            entity.setStatus(CommonStatus.ENABLED);
            entity = repository.save(entity);
            relations.add(beanConverter.convert(entity));
        }

        logger.info("add relations is {}", relations);
        return relations;
    }

    //===================================================
    //               update
    //===================================================
    @Transactional
    public UserCourseRelationBean updateUserCourseRelation(long relationId, boolean checkUser, long userId, String strReadingStatus, String strStatus) {
        logger.info("user={} update relation={} with readingStatus={} and status={}", userId, relationId, strReadingStatus, strStatus);
        UserCourseRelationEntity entity = repository.findOne(relationId);
        if (null==entity) {
            logger.error("relation not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (checkUser && entity.getUserId()!=userId) {
            logger.info("not user's relation");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean changed = false;

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }

        ReadingStatus readingStatus = ReadingStatus.parseString(strReadingStatus);
        if (null!=readingStatus && !readingStatus.equals(entity.getReadingStatus())) {
            entity.setReadingStatus(readingStatus);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
            logger.info("after updating is {}", entity);
        }
        UserCourseRelationBean bean = beanConverter.convert(entity);
        return bean;
    }


    @Transactional
    public UserCourseRelationBean updateUserCourseRelation(long courseId, long userId, String strReadingStatus) {
        logger.info("user={} update courseId={} with readingStatus={} and status={}", userId, courseId, strReadingStatus);
        ReadingStatus readingStatus = ReadingStatus.parseString(strReadingStatus);
        if (null==readingStatus) {
            logger.error("status is not valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        List<UserCourseRelationEntity> entities = repository.findByUserIdAndCourseId(userId, courseId, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            logger.error("relation not exist");
            if (userRepository.exists(userId) && courseRepository.exists(courseId)) {
                UserCourseRelationEntity entity = new UserCourseRelationEntity();
                entity.setUserId(userId);
                entity.setCourseId(courseId);
                entity.setReadingStatus(readingStatus);
                entity.setStatus(CommonStatus.ENABLED);
                entity.setTime(new Date());
                entity = repository.save(entity);
                return beanConverter.convert(entity);
            }
        }
        else {
            for (UserCourseRelationEntity entity : entities) {
                entity.setReadingStatus(readingStatus);
            }
            entities = repository.save(entities);
            return beanConverter.convert(entities.get(0));
        }
        return null;
    }
}
