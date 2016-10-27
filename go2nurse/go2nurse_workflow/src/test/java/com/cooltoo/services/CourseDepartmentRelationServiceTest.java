package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.service.CourseDepartmentRelationService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hp on 2016/6/12.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_department_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_department_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_department_data.xml")
})
public class CourseDepartmentRelationServiceTest extends AbstractCooltooTest {

    @Autowired private CourseDepartmentRelationService service;

    @Test
    public void testJudgeCourseInDepartment() {
        List<Long> checkingCoursesId = Arrays.asList(new Long[]{2L, 5L, 6L});
        int departmentId = 22;
        String status = "ALL";
        List<Long> validCoursesId = service.judgeCourseInDepartment(departmentId, checkingCoursesId, status);
        Assert.assertEquals(3, validCoursesId.size());
        Assert.assertEquals(6, validCoursesId.get(0).longValue());
        Assert.assertEquals(5, validCoursesId.get(1).longValue());
        Assert.assertEquals(2, validCoursesId.get(2).longValue());

        status = CommonStatus.ENABLED.name();
        validCoursesId = service.judgeCourseInDepartment(departmentId, checkingCoursesId, status);
        Assert.assertEquals(1, validCoursesId.size());
        Assert.assertEquals(2, validCoursesId.get(0).longValue());

        status = CommonStatus.DISABLED.name();
        validCoursesId = service.judgeCourseInDepartment(departmentId, checkingCoursesId, status);
        Assert.assertEquals(1, validCoursesId.size());
        Assert.assertEquals(5, validCoursesId.get(0).longValue());

        status = CommonStatus.DELETED.name();
        validCoursesId = service.judgeCourseInDepartment(departmentId, checkingCoursesId, status);
        Assert.assertEquals(1, validCoursesId.size());
        Assert.assertEquals(6, validCoursesId.get(0).longValue());
    }

    @Test
    public void testGetCourseInDepartment() {
        int departmentId = 22;
        String status = "ALL";
        List<Long> courseIds = service.getCourseIdInDepartment(departmentId, status);
        Assert.assertEquals(5, courseIds.size());
        Assert.assertEquals(6, courseIds.get(0).longValue());
        Assert.assertEquals(5, courseIds.get(1).longValue());
        Assert.assertEquals(4, courseIds.get(2).longValue());
        Assert.assertEquals(3, courseIds.get(3).longValue());
        Assert.assertEquals(2, courseIds.get(4).longValue());

        status = CommonStatus.ENABLED.name();
        courseIds = service.getCourseIdInDepartment(departmentId, status);
        Assert.assertEquals(3, courseIds.size());
        Assert.assertEquals(4, courseIds.get(0).longValue());
        Assert.assertEquals(3, courseIds.get(1).longValue());
        Assert.assertEquals(2, courseIds.get(2).longValue());

        status = CommonStatus.DISABLED.name();
        courseIds = service.getCourseIdInDepartment(departmentId, status);
        Assert.assertEquals(1, courseIds.size());
        Assert.assertEquals(5, courseIds.get(0).longValue());

        status = CommonStatus.DELETED.name();
        courseIds = service.getCourseIdInDepartment(departmentId, status);
        Assert.assertEquals(1, courseIds.size());
        Assert.assertEquals(6, courseIds.get(0).longValue());
    }

    @Test
    public void testGetDiagnosticByCourseId() {
        List<Long> courseIds = Arrays.asList(new Long[]{2L, 5L});
        String status = "ALL";
        List<Integer> departmentIds = service.getDepartmentByCourseId(courseIds, status);
        Assert.assertEquals(5, departmentIds.size());
        Assert.assertEquals(55, departmentIds.get(0).intValue());
        Assert.assertEquals(44, departmentIds.get(1).intValue());
        Assert.assertEquals(33, departmentIds.get(2).intValue());
        Assert.assertEquals(22, departmentIds.get(3).intValue());
        Assert.assertEquals(11, departmentIds.get(4).intValue());

        status = CommonStatus.ENABLED.name();
        departmentIds = service.getDepartmentByCourseId(courseIds, status);
        Assert.assertEquals(5, departmentIds.size());
        Assert.assertEquals(55, departmentIds.get(0).intValue());
        Assert.assertEquals(44, departmentIds.get(1).intValue());
        Assert.assertEquals(33, departmentIds.get(2).intValue());
        Assert.assertEquals(22, departmentIds.get(3).intValue());
        Assert.assertEquals(11, departmentIds.get(4).intValue());

        status = CommonStatus.DISABLED.name();
        departmentIds = service.getDepartmentByCourseId(courseIds, status);
        Assert.assertEquals(1, departmentIds.size());
        Assert.assertEquals(22, departmentIds.get(0).intValue());
    }

    @Test
    public void testSetDepartmentRelation() {
        long courseId = 6;
        int hospitalId = 11;
        List<Integer> settingDepartmentIds = Arrays.asList(new Integer[]{11, 44, 55});
        List<Integer> existed = service.getDepartmentByCourseId(Arrays.asList(new Long(courseId)), "ENABLED");
        Assert.assertEquals(1, existed.size());
        Assert.assertTrue(existed.contains(Integer.valueOf(33)));


        service.setCourseToDepartmentRelation(courseId, settingDepartmentIds);

        List<Integer> newExisted = service.getDepartmentByCourseId(Arrays.asList(new Long[]{courseId}), "ENABLED");
        Assert.assertEquals(3, newExisted.size());
        Assert.assertTrue(newExisted.contains(Integer.valueOf(11)));
        Assert.assertTrue(newExisted.contains(Integer.valueOf(44)));
        Assert.assertTrue(newExisted.contains(Integer.valueOf(55)));
        Assert.assertFalse(newExisted.contains(Integer.valueOf(33)));
    }
}
