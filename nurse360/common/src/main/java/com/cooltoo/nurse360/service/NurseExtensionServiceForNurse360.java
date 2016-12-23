package com.cooltoo.nurse360.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.entities.NurseHospitalRelationEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.nurse360.beans.Nurse360CourseBean;
import com.cooltoo.nurse360.beans.Nurse360NotificationBean;
import com.cooltoo.nurse360.entities.NurseCourseRelationEntity;
import com.cooltoo.nurse360.entities.NurseNotificationRelationEntity;
import com.cooltoo.nurse360.repository.NurseCourseRelationRepository;
import com.cooltoo.nurse360.repository.NurseNotificationRelationRepository;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.repository.NurseHospitalRelationRepository;
import com.cooltoo.util.SetUtil;
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
 * Created by zhaolisong on 2016/10/11.
 */
@Service("NurseExtensionServiceForNurse360")
public class NurseExtensionServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(NurseExtensionServiceForNurse360.class);

    private static final Sort nurseHospitalRelationSort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
    private static final Sort nurseCourseRelationSort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));

    @Autowired private Nurse360Utility utility;
    // nurse hospital, course, notification relation
    @Autowired private NurseHospitalRelationRepository nurseHospitalRelationRepository;
    @Autowired private NurseCourseRelationRepository nurseCourseRelationRepository;
    @Autowired private NurseNotificationRelationRepository nurseNotificationRelationRepository;
    // course
    @Autowired private CourseServiceForNurse360 courseService;
    @Autowired private CourseHospitalRelationServiceForNurse360 courseHospitalRelationService;
    // notification
    @Autowired private NotificationServiceForNurse360 notificationService;

    //========================================================================
    //                getting
    //========================================================================
    //***************
    //  get courses
    //***************
    public Nurse360CourseBean getCourseById(long nurseId, long courseId) {
        logger.info("nurse={} get course by courseId={}", nurseId, courseId);
        Nurse360CourseBean course = courseService.getCourseById(courseId, utility.getHttpPrefix());
        return course;
    }

    public List<Nurse360CourseBean> getCourseByNurseId(long nurseId, int pageIndex, int number) {
        logger.info("get course by nurseId={}", nurseId);
        List<Nurse360CourseBean> courses = new ArrayList<>();

        // get nurse hospital department relation
        List<NurseHospitalRelationEntity> nurseHospitalRelations = nurseHospitalRelationRepository.findByNurseId(nurseId, nurseHospitalRelationSort);
        if (null==nurseHospitalRelations || nurseHospitalRelations.isEmpty() || nurseHospitalRelations.size()!=1) {
            return courses;
        }
        NurseHospitalRelationEntity nurseHospitalRelation = nurseHospitalRelations.get(0);

        // get courseId
        List<Long> courseIdInDepartment = courseHospitalRelationService.getCourseInHospitalAndDepartment(
                nurseHospitalRelation.getHospitalId(), nurseHospitalRelation.getDepartmentId(), CommonStatus.ENABLED.name()
        );
        List<Long> courseIdInHospital = courseHospitalRelationService.getCourseInHospitalAndDepartment(
                nurseHospitalRelation.getHospitalId(), 0, CommonStatus.ENABLED.name()
        );
        courseIdInDepartment = SetUtil.newInstance().mergeListValue(courseIdInHospital, courseIdInDepartment);

        // get course
        courses = courseService.getCourseByIds(courseIdInDepartment, pageIndex, number);

        // course that nurse has read
        List<Long> courseReadId = nurseCourseRelationRepository.findCourseIdByNurseIdAndReadingStatus(nurseId, ReadingStatus.READ);
        for (Nurse360CourseBean tmp : courses) {
            if (courseReadId.contains(tmp.getId())) {
                tmp.setHasRead(YesNoEnum.YES);
            }
            else {
                tmp.setHasRead(YesNoEnum.NO);
            }
        }

        return courses;
    }

    public List<Nurse360CourseBean> getCourseReadByNurseId(long nurseId, int pageIndex, int number) {
        logger.info("get course by nurseId={}", nurseId);
        List<Nurse360CourseBean> coursesSorted = new ArrayList<>();

        // sorted course that nurse has read
        List<Long> courseReadId = nurseCourseRelationRepository.findCourseIdByNurseIdAndReadingStatus(nurseId, ReadingStatus.READ, nurseCourseRelationSort);
        if (VerifyUtil.isListEmpty(courseReadId)) {
            return coursesSorted;
        }

        // get course existed
        List<Long> courseIdExisted = courseService.getCourseIdByStatusAndIds(CourseStatus.ENABLE.name(), courseReadId);
        if (VerifyUtil.isListEmpty(courseIdExisted)) {
            return coursesSorted;
        }

        // clear course not existed
        List<Long> courseReadIdSorted = new ArrayList<>();
        for (Long tmpId : courseReadId) {
            if (!courseIdExisted.contains(tmpId)) {
                continue;
            }
            if (courseReadIdSorted.contains(tmpId)) {
                continue;
            }
            courseReadIdSorted.add(tmpId);
        }

        // get return sorted courseId
        List<Long> returnSortedCourseId = new ArrayList<>();
        int startIndex = (pageIndex*number)<0 ? 0 : (pageIndex*number);
        for (int i=startIndex; i<courseReadIdSorted.size(); i++) {
            if (i>=(pageIndex*number + number)) {
                break;
            }
            returnSortedCourseId.add(courseReadIdSorted.get(i));
        }
        if (VerifyUtil.isListEmpty(returnSortedCourseId)) {
            return coursesSorted;
        }

        // get course bean
        List<Nurse360CourseBean> courses = courseService.getCourseByStatusAndIds(CourseStatus.ENABLE.name(), returnSortedCourseId);
        for (Long tmpId : returnSortedCourseId) {
            for (Nurse360CourseBean tmp : courses) {
                if (tmpId == tmp.getId()) {
                    tmp.setHasRead(YesNoEnum.YES);
                    coursesSorted.add(tmp);
                }
            }
        }

        return coursesSorted;
    }

    //********************
    //  get notification
    //********************
    public Nurse360NotificationBean getNotificationById(long nurseId, long notificationId) {
        logger.info("nurse={} get notification by notificationId={}", nurseId, notificationId);
        Nurse360NotificationBean notification = notificationService.getNotificationById(notificationId);
        return notification;
    }

    public List<Nurse360NotificationBean> getNotificationByNurseId(long nurseId, int pageIndex, int sizePerPage) {
        logger.info("get course by nurseId={}", nurseId);
        List<Long> notificationIds = new ArrayList<>();

        // get nurse hospital department relation
        long hospitalId = 0;
        long departId = 0;
        List<NurseHospitalRelationEntity> nurseHospitalRelations = nurseHospitalRelationRepository.findByNurseId(nurseId, nurseHospitalRelationSort);
        if (null!=nurseHospitalRelations && nurseHospitalRelations.size()==1) {
            NurseHospitalRelationEntity nurseHospitalRelation = nurseHospitalRelations.get(0);
            hospitalId = nurseHospitalRelation.getHospitalId();
            departId = nurseHospitalRelation.getDepartmentId();
        }

        List<CommonStatus> statuses = Arrays.asList(new CommonStatus[]{CommonStatus.ENABLED});
        // get cooltoo's notificationIds
        List<Long> tmpNotificationIds = notificationService.getNotificationIdByVendor(statuses, ServiceVendorType.HOSPITAL, -1L, 0L);
        SetUtil.newInstance().mergeListValue(tmpNotificationIds, notificationIds);

        // get hospital's notificationIds that nurse in
        tmpNotificationIds = notificationService.getNotificationIdByVendor(statuses, ServiceVendorType.HOSPITAL, hospitalId, 0L);
        SetUtil.newInstance().mergeListValue(tmpNotificationIds, notificationIds);

        // get department's notificationIds that nurse in
        tmpNotificationIds = notificationService.getNotificationIdByVendor(statuses, ServiceVendorType.HOSPITAL, hospitalId, departId);
        SetUtil.newInstance().mergeListValue(tmpNotificationIds, notificationIds);

        // get notification that nurse has read
        List<Long> notificationIdsNurseRead = nurseNotificationRelationRepository.findNotificationIdByNurseIdAndReadingStatus(nurseId, ReadingStatus.READ);

        // get notification
        List<Nurse360NotificationBean> notifications = notificationService.getNotificationByIds(notificationIds, pageIndex, sizePerPage);

        // notification that nurse has read
        for (Nurse360NotificationBean tmp : notifications) {
            if (notificationIdsNurseRead.contains(tmp.getId())) {
                tmp.setHasRead(YesNoEnum.YES);
            }
            else {
                tmp.setHasRead(YesNoEnum.NO);
            }
        }

        return notifications;
    }

    //========================================================================
    //                adding
    //========================================================================
    @Transactional
    public long readCourse(long nurseId, long courseId) {
        logger.info("nurse={} read course={}");
        if (!courseService.existCourse(courseId)) {
            logger.error("course not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }

        NurseCourseRelationEntity nurseReadCourse = null;
        List<NurseCourseRelationEntity> relations = nurseCourseRelationRepository.findByNurseIdAndCourseId(nurseId, courseId);
        if (VerifyUtil.isListEmpty(relations)) {
            nurseReadCourse = new NurseCourseRelationEntity();
            nurseReadCourse.setNurseId(nurseId);
            nurseReadCourse.setCourseId(courseId);
            nurseReadCourse.setTime(new Date());
        }
        else {
            nurseReadCourse = relations.get(0);
            relations.remove(0);
        }
        nurseReadCourse.setReadingStatus(ReadingStatus.READ);
        nurseReadCourse.setStatus(CommonStatus.ENABLED);
        nurseReadCourse = nurseCourseRelationRepository.save(nurseReadCourse);

        if (!VerifyUtil.isListEmpty(relations)) {
            nurseCourseRelationRepository.delete(relations);
        }
        return nurseReadCourse.getId();
    }

    @Transactional
    public long readNotification(long nurseId, long notificationId) {
        logger.info("nurse={} read notification={}", nurseId, notificationId);
        if (!notificationService.existsNotification(notificationId)) {
            logger.error("notification not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        NurseNotificationRelationEntity nurseReadNotification;
        List<NurseNotificationRelationEntity> relations = nurseNotificationRelationRepository.findByNurseIdAndNotificationId(nurseId, notificationId);
        if (VerifyUtil.isListEmpty(relations)) {
            nurseReadNotification = new NurseNotificationRelationEntity();
            nurseReadNotification.setNurseId(nurseId);
            nurseReadNotification.setNotificationId(notificationId);
            nurseReadNotification.setTime(new Date());
        }
        else {
            nurseReadNotification = relations.get(0);
            relations.remove(0);
        }
        nurseReadNotification.setReadingStatus(ReadingStatus.READ);
        nurseReadNotification.setStatus(CommonStatus.ENABLED);
        nurseReadNotification = nurseNotificationRelationRepository.save(nurseReadNotification);

        if (!VerifyUtil.isListEmpty(relations)) {
            nurseNotificationRelationRepository.delete(relations);
        }
        return nurseReadNotification.getId();
    }

}
