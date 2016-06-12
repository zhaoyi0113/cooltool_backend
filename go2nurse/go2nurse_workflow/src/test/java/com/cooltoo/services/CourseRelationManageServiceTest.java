package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CourseHospitalRelationBean;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/12.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_hospital_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_department_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_diagnostic_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_department_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/diagnostic_point.xml")
})
public class CourseRelationManageServiceTest extends AbstractCooltooTest {

    @Autowired private CourseRelationManageService service;

    @Test
    public void testHospitalExists() {
        int hospitalId = 22;
        boolean exists = service.hospitalExist(hospitalId);
        Assert.assertEquals(true, exists);

        hospitalId = 10;
        exists = service.hospitalExist(hospitalId);
        Assert.assertEquals(false, exists);
    }

    @Test
    public void testDepartmentExists() {
        int departmentId = 22;
        boolean exists = service.departmentExist(departmentId);
        Assert.assertEquals(true, exists);

        departmentId = 10;
        exists = service.departmentExist(departmentId);
        Assert.assertEquals(false, exists);
    }

    @Test
    public void testDiagnosticExists() {
        int diagnosticId = 2;
        boolean exists = service.diagnosticExist(diagnosticId);
        Assert.assertEquals(true, exists);

        diagnosticId = 10;
        exists = service.diagnosticExist(diagnosticId);
        Assert.assertEquals(false, exists);
    }

    @Test
    public void testGetCourseIdByConditions() {
        int hospitalId = 0;
        int departmentId = 0;
        int diagnosticId = 0;
        String relationStatus = "";
        String courseStatus = "";
        Map<String, List<Long>> keyToCourseId = service.getCoursesIdByConditions(hospitalId, departmentId, diagnosticId, relationStatus, courseStatus);
        Assert.assertEquals(0, keyToCourseId.size());

        hospitalId = 11;
        relationStatus = "ALL";
        courseStatus = "ALL";
        keyToCourseId = service.getCoursesIdByConditions(hospitalId, departmentId, diagnosticId, relationStatus, courseStatus);
        Assert.assertEquals(3, keyToCourseId.size());
        Assert.assertEquals(5, keyToCourseId.get(CourseRelationManageService.key_all_courses_in_hospital).size());
        Assert.assertEquals(0, keyToCourseId.get(CourseRelationManageService.key_department).size());
        Assert.assertEquals(0, keyToCourseId.get(CourseRelationManageService.key_diagnostic).size());

        departmentId = 22;
        keyToCourseId = service.getCoursesIdByConditions(hospitalId, departmentId, diagnosticId, relationStatus, courseStatus);
        Assert.assertEquals(3, keyToCourseId.size());
        Assert.assertEquals(5, keyToCourseId.get(CourseRelationManageService.key_all_courses_in_hospital).size());
        Assert.assertEquals(3, keyToCourseId.get(CourseRelationManageService.key_department).size());
        Assert.assertEquals(0, keyToCourseId.get(CourseRelationManageService.key_diagnostic).size());

        diagnosticId = 2;
        keyToCourseId = service.getCoursesIdByConditions(hospitalId, departmentId, diagnosticId, relationStatus, courseStatus);
        Assert.assertEquals(3, keyToCourseId.size());
        Assert.assertEquals(5, keyToCourseId.get(CourseRelationManageService.key_all_courses_in_hospital).size());
        Assert.assertEquals(3, keyToCourseId.get(CourseRelationManageService.key_department).size());
        Assert.assertEquals(1, keyToCourseId.get(CourseRelationManageService.key_diagnostic).size());
    }

    @Test
    public void testGetCourseByConditions() {
        int hospitalId = 0;
        int departmentId = 0;
        int diagnosticId = 0;
        String relationStatus = "";
        String courseStatus = "";
        Map<String, List<CourseBean>> keyToCourse = service.getCoursesByConditions(hospitalId, departmentId, diagnosticId, relationStatus, courseStatus);
        Assert.assertEquals(3, keyToCourse.size());

        hospitalId = 11;
        relationStatus = "ALL";
        courseStatus = "ALL";
        keyToCourse = service.getCoursesByConditions(hospitalId, departmentId, diagnosticId, relationStatus, courseStatus);
        Assert.assertEquals(3, keyToCourse.size());
        Assert.assertEquals(5, keyToCourse.get(CourseRelationManageService.key_all_courses_in_hospital).size());
        Assert.assertEquals(0, keyToCourse.get(CourseRelationManageService.key_department).size());
        Assert.assertEquals(0, keyToCourse.get(CourseRelationManageService.key_diagnostic).size());

        departmentId = 22;
        keyToCourse = service.getCoursesByConditions(hospitalId, departmentId, diagnosticId, relationStatus, courseStatus);
        Assert.assertEquals(3, keyToCourse.size());
        Assert.assertEquals(5, keyToCourse.get(CourseRelationManageService.key_all_courses_in_hospital).size());
        Assert.assertEquals(3, keyToCourse.get(CourseRelationManageService.key_department).size());
        Assert.assertEquals(0, keyToCourse.get(CourseRelationManageService.key_diagnostic).size());

        diagnosticId = 2;
        keyToCourse = service.getCoursesByConditions(hospitalId, departmentId, diagnosticId, relationStatus, courseStatus);
        Assert.assertEquals(3, keyToCourse.size());
        Assert.assertEquals(5, keyToCourse.get(CourseRelationManageService.key_all_courses_in_hospital).size());
        Assert.assertEquals(3, keyToCourse.get(CourseRelationManageService.key_department).size());
        Assert.assertEquals(1, keyToCourse.get(CourseRelationManageService.key_diagnostic).size());
    }
}
