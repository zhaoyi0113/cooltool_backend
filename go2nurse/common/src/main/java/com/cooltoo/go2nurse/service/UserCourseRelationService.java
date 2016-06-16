package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CourseCategoryBean;
import com.cooltoo.go2nurse.beans.UserCourseRelationBean;
import com.cooltoo.go2nurse.converter.UserCourseRelationBeanConverter;
import com.cooltoo.go2nurse.entities.UserCourseRelationEntity;
import com.cooltoo.go2nurse.repository.CourseRepository;
import com.cooltoo.go2nurse.repository.UserCourseRelationRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Autowired private CourseRelationManageService courseRelationManageService;
    @Autowired private CourseCategoryService courseCategoryService;
    @Autowired private UserCourseRelationRepository repository;
    @Autowired private UserCourseRelationBeanConverter beanConverter;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;

    //===================================================
    //               get
    //===================================================
    public List<CourseCategoryBean> getCourseCategory(long userId, String strReadingStatuses, String strStatus) {
        List<UserCourseRelationBean> relations = getRelation(userId, strReadingStatuses, strStatus);
        List<Long> courseIds = new ArrayList<>();
        for (UserCourseRelationBean tmp : relations) {
            courseIds.add(tmp.getCourseId());
        }
        return courseCategoryService.getCategoryByCourseId(strStatus, courseIds);
    }

    public long countByUserAndReadStatusAndStatus(long userId, String strReadingStatuses, String strStatus) {
        logger.info("count the course with user={} readingStatus={} status={}",
                userId, strReadingStatuses, strStatus);

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<ReadingStatus> readingStatuses = null;
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
        List<ReadingStatus> readingStatuses = null;
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
        List<ReadingStatus> readingStatuses = null;
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

    public List<UserCourseRelationBean> getRelation(long userId, String strReadingStatuses, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get the course with user={} readingStatus={} status={} at page={} sizePerPage={}",
                userId, strReadingStatuses, strStatus, pageIndex, sizePerPage);

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<ReadingStatus> readingStatuses = null;
        if ("ALL".equalsIgnoreCase(strReadingStatuses)) {
            readingStatuses = ReadingStatus.getAllStatus();
        }
        else {
            readingStatuses = ReadingStatus.parseStatuses(strReadingStatuses);
        }

        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<UserCourseRelationEntity> resultSet = repository.findByUserIdAndReadStatusAndStatus(userId, readingStatuses, status, page);
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
}
