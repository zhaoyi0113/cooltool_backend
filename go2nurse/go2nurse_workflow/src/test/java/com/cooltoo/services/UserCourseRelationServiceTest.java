package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.DiagnosticEnumerationBean;
import com.cooltoo.go2nurse.beans.UserCourseRelationBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.converter.DiagnosticEnumerationBeanConverter;
import com.cooltoo.go2nurse.service.UserCourseRelationService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by hp on 2016/6/16.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_hospital_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_department_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_diagnostic_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_department_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/diagnostic_point.xml"),

        @DatabaseSetup("classpath:/com/cooltoo/services/user_course_relation_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/user_hospitalized_relation_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/course_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/user_data.xml")
})
public class UserCourseRelationServiceTest extends AbstractCooltooTest {
    @Autowired
    private UserCourseRelationService relationService;
    @Autowired
    private DiagnosticEnumerationBeanConverter diagnosticBeanConverter;

    @Test
    public void testCountByUserAndReadStatusAndStatus() {
        long userId = 1;
        String readingStatuses = "ALL";
        String status = "ALL";
        long count = relationService.countByUserAndReadStatusAndStatus(userId, readingStatuses, status);
        Assert.assertEquals(5, count);

        status = CommonStatus.ENABLED.name();
        count = relationService.countByUserAndReadStatusAndStatus(userId, readingStatuses, status);
        Assert.assertEquals(4, count);

        status = CommonStatus.DISABLED.name();
        count = relationService.countByUserAndReadStatusAndStatus(userId, readingStatuses, status);
        Assert.assertEquals(1, count);

        status = "ALL";
        readingStatuses = ReadingStatus.UNREAD.name();
        count = relationService.countByUserAndReadStatusAndStatus(userId, readingStatuses, status);
        Assert.assertEquals(3, count);

        readingStatuses = ReadingStatus.READ.name();
        count = relationService.countByUserAndReadStatusAndStatus(userId, readingStatuses, status);
        Assert.assertEquals(1, count);

        readingStatuses = ReadingStatus.DELETED.name();
        count = relationService.countByUserAndReadStatusAndStatus(userId, readingStatuses, status);
        Assert.assertEquals(1, count);

        readingStatuses = ReadingStatus.DELETED.name()+","+ReadingStatus.READ.name();
        count = relationService.countByUserAndReadStatusAndStatus(userId, readingStatuses, status);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetRelativeCoursesId() {
        long userId = 1;
        String readingStatuses = "ALL";
        String status = "ALL";
        List<Long> count = relationService.getRelationCourseId(userId, readingStatuses, status);
        Assert.assertEquals(5, count.size());
        Assert.assertEquals(5, count.get(0).longValue());
        Assert.assertEquals(4, count.get(1).longValue());
        Assert.assertEquals(3, count.get(2).longValue());
        Assert.assertEquals(2, count.get(3).longValue());
        Assert.assertEquals(1, count.get(4).longValue());

        status = CommonStatus.ENABLED.name();
        count = relationService.getRelationCourseId(userId, readingStatuses, status);
        Assert.assertEquals(4, count.size());
        Assert.assertEquals(4, count.get(0).longValue());
        Assert.assertEquals(3, count.get(1).longValue());
        Assert.assertEquals(2, count.get(2).longValue());
        Assert.assertEquals(1, count.get(3).longValue());

        status = CommonStatus.DISABLED.name();
        count = relationService.getRelationCourseId(userId, readingStatuses, status);
        Assert.assertEquals(1, count.size());
        Assert.assertEquals(5, count.get(0).longValue());

        status = "ALL";
        readingStatuses = ReadingStatus.UNREAD.name();
        count = relationService.getRelationCourseId(userId, readingStatuses, status);
        Assert.assertEquals(3, count.size());
        Assert.assertEquals(5, count.get(0).longValue());
        Assert.assertEquals(2, count.get(1).longValue());
        Assert.assertEquals(1, count.get(2).longValue());

        readingStatuses = ReadingStatus.READ.name();
        count = relationService.getRelationCourseId(userId, readingStatuses, status);
        Assert.assertEquals(1, count.size());
        Assert.assertEquals(3, count.get(0).longValue());

        readingStatuses = ReadingStatus.DELETED.name();
        count = relationService.getRelationCourseId(userId, readingStatuses, status);
        Assert.assertEquals(1, count.size());
        Assert.assertEquals(4, count.get(0).longValue());

        readingStatuses = ReadingStatus.DELETED.name()+","+ReadingStatus.READ.name();
        count = relationService.getRelationCourseId(userId, readingStatuses, status);
        Assert.assertEquals(2, count.size());
        Assert.assertEquals(4, count.get(0).longValue());
        Assert.assertEquals(3, count.get(1).longValue());
    }

    @Test
    public void testGetRelation() {
        long userId = 1;
        String readingStatuses = "ALL";
        String status = "ALL";
        List<UserCourseRelationBean> count = relationService.getRelation(userId, readingStatuses, status);
        Assert.assertEquals(5, count.size());
        Assert.assertEquals(5, count.get(0).getId());
        Assert.assertEquals(4, count.get(1).getId());
        Assert.assertEquals(3, count.get(2).getId());
        Assert.assertEquals(2, count.get(3).getId());
        Assert.assertEquals(1, count.get(4).getId());

        status = CommonStatus.ENABLED.name();
        count = relationService.getRelation(userId, readingStatuses, status);
        Assert.assertEquals(4, count.size());
        Assert.assertEquals(4, count.get(0).getId());
        Assert.assertEquals(3, count.get(1).getId());
        Assert.assertEquals(2, count.get(2).getId());
        Assert.assertEquals(1, count.get(3).getId());

        status = CommonStatus.DISABLED.name();
        count = relationService.getRelation(userId, readingStatuses, status);
        Assert.assertEquals(1, count.size());
        Assert.assertEquals(5, count.get(0).getId());

        status = "ALL";
        readingStatuses = ReadingStatus.UNREAD.name();
        count = relationService.getRelation(userId, readingStatuses, status);
        Assert.assertEquals(3, count.size());
        Assert.assertEquals(5, count.get(0).getId());
        Assert.assertEquals(2, count.get(1).getId());
        Assert.assertEquals(1, count.get(2).getId());

        readingStatuses = ReadingStatus.READ.name();
        count = relationService.getRelation(userId, readingStatuses, status);
        Assert.assertEquals(1, count.size());
        Assert.assertEquals(3, count.get(0).getId());

        readingStatuses = ReadingStatus.DELETED.name();
        count = relationService.getRelation(userId, readingStatuses, status);
        Assert.assertEquals(1, count.size());
        Assert.assertEquals(4, count.get(0).getId());

        readingStatuses = ReadingStatus.DELETED.name()+","+ReadingStatus.READ.name();
        count = relationService.getRelation(userId, readingStatuses, status);
        Assert.assertEquals(2, count.size());
        Assert.assertEquals(4, count.get(0).getId());
        Assert.assertEquals(3, count.get(1).getId());
    }

    @Test
    public void getUpdateStatus() {
        long relationId = 1;
        String readingStatus = ReadingStatus.READ.name();
        String status = CommonStatus.DISABLED.name();
        UserCourseRelationBean bean;

        long userId = 1;
        long count = relationService.countByUserAndReadStatusAndStatus(userId, readingStatus, status);
        Assert.assertEquals(0, count);

        userId = 2;
        Throwable error = null;
        try { relationService.updateUserCourseRelation(relationId, true, userId, readingStatus, status); }
        catch (Exception ex) { error= ex; }
        Assert.assertNotNull(error);

        userId = 1;
        bean = relationService.updateUserCourseRelation(relationId, true, userId, readingStatus, status);
        Assert.assertEquals(relationId, bean.getId());
        Assert.assertEquals(readingStatus, bean.getReadingStatus().name());
        Assert.assertEquals(status, bean.getStatus().name());

        count = relationService.countByUserAndReadStatusAndStatus(userId, readingStatus, status);
        Assert.assertEquals(1, count);
    }

    @Test
    public void addRelation() {
        List<UserCourseRelationBean> relations;
        long userId = 6;
        List<Long> courseIds = Arrays.asList(new Long[]{ 1L, 2L, 3L, 5L, 4L });

        long count = relationService.countByUserAndReadStatusAndStatus(userId, "ALL", "ALL");
        Assert.assertEquals(0, count);

        relations = relationService.addUserCourseRelation(userId, courseIds);
        Assert.assertEquals(5, relations.size());
        Assert.assertTrue(courseIds.contains(relations.get(0).getCourseId()));
        Assert.assertTrue(courseIds.contains(relations.get(1).getCourseId()));
        Assert.assertTrue(courseIds.contains(relations.get(2).getCourseId()));
        Assert.assertTrue(courseIds.contains(relations.get(3).getCourseId()));
        Assert.assertTrue(courseIds.contains(relations.get(4).getCourseId()));

        count = relationService.countByUserAndReadStatusAndStatus(userId, "ALL", "ALL");
        Assert.assertEquals(5, count);

        String status = CommonStatus.ENABLED.name();
        String readingStatus = ReadingStatus.UNREAD.name();
        count = relationService.countByUserAndReadStatusAndStatus(userId, readingStatus, status);
        Assert.assertEquals(5, count);
    }

    @Test
    public void testGetCourseByHospitalAndDepartment() {
        long userId = 1;
        Integer hospitalId = 33;
        Integer departmentId = 22;
        Map<DiagnosticEnumerationBean, List<CourseBean>> courses = relationService.getCourseByHospitalAndDepartment(userId, hospitalId, departmentId);
        Assert.assertEquals(0, courses.size());

        hospitalId = 22;
        departmentId = 11;
        courses = relationService.getCourseByHospitalAndDepartment(userId, hospitalId, departmentId);
        Assert.assertEquals(0, courses.size());

        userId = 4;
        hospitalId = 22;
        departmentId = 22;
        courses = relationService.getCourseByHospitalAndDepartment(userId, hospitalId, departmentId);
        Assert.assertEquals(2, courses.size());
        Set<DiagnosticEnumerationBean> keys = courses.keySet();
        for (DiagnosticEnumerationBean key : keys) {
            Assert.assertTrue(key.getId() == 2 || key.getId() == 4);
            Assert.assertEquals(4, courses.get(key).get(0).getId());
        }
    }
}
