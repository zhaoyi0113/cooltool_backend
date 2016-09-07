package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
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

import java.util.*;

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
    @Autowired private CourseRelationManageService courseRelationManageService;
    @Autowired private CourseCategoryService courseCategoryService;
    @Autowired private CourseService courseService;

    @Autowired private UserCourseRelationRepository repository;
    @Autowired private UserCourseRelationBeanConverter beanConverter;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;

    //===================================================
    //               getting for user
    //===================================================
    public Map<CourseCategoryBean, List<CourseBean>> getAllPublicExtensionNursingCourses(long userId) {
        logger.info("user={} get all public extension nursing courses", userId);
        List<Long> coursesId = courseRelationManageService.getEnabledCoursesIdInExtensionNursing();
        Map<CourseCategoryBean, List<CourseBean>> returnValue =
                courseCategoryService.getCategoryRelationByCourseId(coursesId);

        setCourseReadStatusForCategoryToCourseBeans(userId, returnValue);
        return returnValue;
    }

    public Map<CourseCategoryBean, List<CourseBean>> getAllCategoryToCoursesByCourses(List<CourseBean> courses) {
        logger.info("get all course_category to courses map");
        Map<CourseCategoryBean, List<CourseBean>> returnValue = courseCategoryService.getCategoryRelationByCourse(courses);
        return returnValue;
    }

    private void setCourseReadStatusForCategoryToCourseBeans(long userId, Map<CourseCategoryBean, List<CourseBean>> categoryToCourses) {
        logger.info("set user={} course read status", userId);
        if (VerifyUtil.isMapEmpty(categoryToCourses)) {
            return;
        }

        List<Long> readCourseIds = getRelationCourseId(userId, "read", CommonStatus.ENABLED.name());
        if (VerifyUtil.isListEmpty(readCourseIds) && null==readCourseIds) {
            readCourseIds = new ArrayList<>();
        }

        Set<CourseCategoryBean> keys = categoryToCourses.keySet();
        for (CourseCategoryBean key : keys) {
            List<CourseBean> courses = categoryToCourses.get(key);
            for (CourseBean course : courses) {
                course.setReading(readCourseIds.contains(course.getId()) ? ReadingStatus.READ : ReadingStatus.UNREAD);
            }
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

    public Map<DiagnosticEnumeration, List<CourseBean>> getUserCurrentCoursesWithExtensionNursingOfHospital(long userId) {
        logger.info("get current hospitalized relation courses by userId={}", userId);

        // get current diagnostic group ID
        long currentDiagnosticGroupId = userDiagnosticRelationService.getUserCurrentGroupId(userId);
        // get user read courses
        List<Long> readCourseIds = getRelationCourseId(userId, "read", CommonStatus.ENABLED.name());
        // the hospital that user hospitalized
        List<UserHospitalizedRelationBean> userHospitalized =
                userHospitalizedRelationService.getUserHospitalizedRelationByGroupId(userId, currentDiagnosticGroupId);

        // construct hospitalized courses in hospital---department
        Map<DiagnosticEnumeration, List<CourseBean>> returnVal = new HashMap<>();
        for (UserHospitalizedRelationBean hospitalized : userHospitalized) {
            // has leave this department
            if (YesNoEnum.YES.equals(hospitalized.getHasLeave())) {
                continue;
            }
            // get all diagnostic point --> courses map in hospital and department
            Map<DiagnosticEnumeration, List<CourseBean>> coursesInDepartment =
                    courseRelationManageService.getDiagnosticToCoursesMapInDepartment(
                            hospitalized.getHospitalId(), hospitalized.getDepartmentId()
                    );

            Set<DiagnosticEnumeration> keys = coursesInDepartment.keySet();
            for (DiagnosticEnumeration key : keys) {
                List<CourseBean> courses = coursesInDepartment.get(key);
                for (CourseBean course : courses) {// set reading status
                    course.setReading(readCourseIds.contains(course.getId()) ? ReadingStatus.READ : ReadingStatus.UNREAD);
                }
                List<CourseBean> finalCourses = returnVal.get(key);
                if (null == finalCourses) {
                    returnVal.put(key, courses);
                    continue;
                }
                for (CourseBean course : courses) {
                    if (!finalCourses.contains(course)) {
                        finalCourses.add(course);
                    }
                }
            }

            // get course of extension nursing of hospital
            List<CourseBean> coursesOfExtensionNursing =
                    courseRelationManageService.getEnabledCoursesByHospitalIdAndDiagnosticId(hospitalized.getHospitalId(), DiagnosticEnumeration.EXTENSION_NURSING.ordinal());
            for (CourseBean course : coursesOfExtensionNursing) {// set reading status
                course.setReading(readCourseIds.contains(course.getId()) ? ReadingStatus.READ : ReadingStatus.UNREAD);
            }

            List<CourseBean> finalCourses = returnVal.get(DiagnosticEnumeration.EXTENSION_NURSING);
            if (null==finalCourses) {
                returnVal.put(DiagnosticEnumeration.EXTENSION_NURSING, coursesOfExtensionNursing);
                continue;
            }
            else {
                for (CourseBean course : coursesOfExtensionNursing) {
                    if (!finalCourses.contains(course)) {
                        finalCourses.add(course);
                    }
                }
            }
        }
        return returnVal;
    }

//// 获取用户当前住院的所有诊疗点的课程(不含医院健康宣教的课程，看含有科室健康宣教的课程)
//    public Map<UserHospitalizedRelationBean, Map<UserDiagnosticPointRelationBean, List<CourseBean>>> getUserCurrentCourses(long userId) {
//        logger.info("get current hospitalized relation courses by userId={}", userId);
//
//        // get current diagnostic group ID
//        long currentDiagnosticGroupId = userDiagnosticRelationService.getUserCurrentGroupId(userId);
//        // get user read courses
//        List<Long> readCourseIds = getRelationCourseId(userId, "read", CommonStatus.ENABLED.name());
//        // the hospital that user hospitalized
//        List<UserHospitalizedRelationBean> userHospitalized =
//                userHospitalizedRelationService.getUserHospitalizedRelationByGroupId(userId, currentDiagnosticGroupId);
//        // the diagnostic point that user hospitalized
//        List<UserDiagnosticPointRelationBean> userDiagnostic =
//                userDiagnosticRelationService.getUserDiagnosticRelationByGroupId(userId, currentDiagnosticGroupId);
//
//        long courseCount = 0;
//        Map<UserHospitalizedRelationBean, Map<UserDiagnosticPointRelationBean, List<CourseBean>>>
//                hospitalToDiagnosticToCourses = new HashMap<>();
//
//        // construct return value
//        for (UserHospitalizedRelationBean hospitalized : userHospitalized) {
//            // has leave this department
//            if (YesNoEnum.YES.equals(hospitalized.getHasLeave())) {
//                continue;
//            }
//            // get all user diagnostic point --> courses map in hospital and department
//            Map<UserDiagnosticPointRelationBean, List<CourseBean>> finalDiagnosticToCourses = new HashMap<>();
//            // get all diagnostic point --> courses map in hospital and department
//            Map<DiagnosticEnumerationBean, List<CourseBean>> diagnosticToCourses =
//                    courseRelationManageService.getDiagnosticToCoursesMapInDepartment(
//                            hospitalized.getHospitalId(), hospitalized.getDepartmentId()
//                    );
//
//            Set<DiagnosticEnumerationBean> keys = diagnosticToCourses.keySet();
//            for (DiagnosticEnumerationBean key : keys) {
//                UserDiagnosticPointRelationBean finalDiagnostic = selectUserDiagnosticRelationBean(userDiagnostic, key);
//                if (null==finalDiagnostic) {
//                    finalDiagnostic = createDiagnosticRelationBean(userId, currentDiagnosticGroupId, key.getId());
//                }
//                List<CourseBean> courses = diagnosticToCourses.get(key);
//                courseCount += setCourseReadStatus(courses, readCourseIds);
//                finalDiagnosticToCourses.put(finalDiagnostic, courses);
//            }
//            hospitalToDiagnosticToCourses.put(hospitalized, finalDiagnosticToCourses);
//        }
//        logger.info("get course, count={}", courseCount);
//        return hospitalToDiagnosticToCourses;
//    }

    // 历史课程
    public List<CourseBean> getUserAllCoursesRead(long userId) {
        logger.info("get all courses read by userId={}", userId);

        // get user read courses
        List<ReadingStatus> readAndUnread = ReadingStatus.getAllStatus();
        readAndUnread.remove(ReadingStatus.DELETED);
        readAndUnread.remove(ReadingStatus.UNREAD);
        List<Long> readCourseIds = repository.findCourseIdByUserIdAndReadStatusAndStatus(userId, readAndUnread, CommonStatus.ENABLED, sort);
        List<CourseBean> readCourses = courseService.getCourseByIds(readCourseIds);

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
        List<CourseBean> readCourses = courseService.getCourseByIds(readCoursePageIds);

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
    public List<CourseCategoryBean> getCourseCategory(long userId, String strReadingStatuses, String strStatus) {
        List<UserCourseRelationBean> relations = getRelation(userId, strReadingStatuses, strStatus);
        List<Long> courseIds = new ArrayList<>();
        for (UserCourseRelationBean tmp : relations) {
            courseIds.add(tmp.getCourseId());
        }
        return courseCategoryService.getCategoryByCourseId(strStatus, courseIds);
    }

    public List<CourseBean> getUserCoursesInExtensionNursing(long userId) {
        logger.info("get user courses in extension nursing, userId={}", userId);
        List<CourseBean> courses = courseRelationManageService.getEnabledCoursesInExtensionNursing();

        // get courses read by user
        String read = ReadingStatus.READ.name();
        List<Long> readCourseIds = getRelationCourseId(userId, read, CommonStatus.ENABLED.name());

        // set reading status
        for (CourseBean course : courses) {
            if (readCourseIds.contains(course.getId())) {
                course.setReading(ReadingStatus.READ);
            }
            else {
                course.setReading(ReadingStatus.UNREAD);
            }
        }

        logger.info("count is {}", courses.size());
        return courses;
    }

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

    public List<UserCourseRelationBean> getRelation(long userId, String strReadingStatuses, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get the course with user={} readingStatus={} status={} at page={} sizePerPage={}",
                userId, strReadingStatuses, strStatus, pageIndex, sizePerPage);

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<ReadingStatus> readingStatuses;
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

//    private List<Long> getCourseIds(List<UserCourseRelationBean> beans, ReadingStatus readingStatus) {
//        List<Long> courseIds = new ArrayList<>();
//        if (VerifyUtil.isListEmpty(beans)) {
//            return courseIds;
//        }
//        for (UserCourseRelationBean bean : beans) {
//            if (bean.getReadingStatus().equals(readingStatus) && !courseIds.contains(bean.getCourseId())) {
//                courseIds.add(bean.getCourseId());
//            }
//        }
//        return courseIds;
//    }
//
//    private int setCourseReadStatus(List<CourseBean> courses, List<Long> coursesReadIds) {
//        if (VerifyUtil.isListEmpty(courses) || VerifyUtil.isListEmpty(coursesReadIds)) {
//            return VerifyUtil.isListEmpty(courses) ? 0 : courses.size();
//        }
//        for (int i=0, count=courses.size(); i<count; i++) {
//            CourseBean course = courses.get(i);
//            // set the course in user's read courses to READ
//            if (coursesReadIds.contains(course.getId())) {
//                course.setReading(ReadingStatus.READ);
//            }
//            else {
//                course.setReading(ReadingStatus.UNREAD);
//            }
//        }
//        return courses.size();
//    }
//
//    private UserDiagnosticPointRelationBean selectUserDiagnosticRelationBean(
//            List<UserDiagnosticPointRelationBean> userDiagnosticRelations,
//            DiagnosticEnumerationBean diagnostic) {
//        if (VerifyUtil.isListEmpty(userDiagnosticRelations) || null==diagnostic) {
//            return null;
//        }
//        UserDiagnosticPointRelationBean retVal = null;
//        for (UserDiagnosticPointRelationBean relation : userDiagnosticRelations) {
//            if (relation.getDiagnosticId() == diagnostic.getId()) {
//                retVal = relation;
//            }
//        }
//        return retVal;
//    }
//
//    private UserDiagnosticPointRelationBean createDiagnosticRelationBean(long userId, long groupId, long diagnosticId) {
//        UserDiagnosticPointRelationBean returnValue = new UserDiagnosticPointRelationBean();
//        returnValue.setAnswerTimes(userId);
//        returnValue.setGroupId(groupId);
//        returnValue.setDiagnosticId(diagnosticId);
//        return returnValue;
//    }

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
