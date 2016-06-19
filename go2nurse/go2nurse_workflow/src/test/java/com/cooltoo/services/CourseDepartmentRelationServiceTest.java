package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.CourseDepartmentRelationBean;
import com.cooltoo.go2nurse.entities.CourseDepartmentRelationEntity;
import com.cooltoo.go2nurse.repository.CourseDepartmentRelationRepository;
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
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_department_relation_data.xml")
})
public class CourseDepartmentRelationServiceTest extends AbstractCooltooTest {

    @Autowired
    private CourseDepartmentRelationService service;
    @Autowired
    private CourseDepartmentRelationRepository repository;

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
        List<Long> courseIds = service.getCourseInDepartment(departmentId, status);
        Assert.assertEquals(5, courseIds.size());
        Assert.assertEquals(6, courseIds.get(0).longValue());
        Assert.assertEquals(5, courseIds.get(1).longValue());
        Assert.assertEquals(4, courseIds.get(2).longValue());
        Assert.assertEquals(3, courseIds.get(3).longValue());
        Assert.assertEquals(2, courseIds.get(4).longValue());

        status = CommonStatus.ENABLED.name();
        courseIds = service.getCourseInDepartment(departmentId, status);
        Assert.assertEquals(3, courseIds.size());
        Assert.assertEquals(4, courseIds.get(0).longValue());
        Assert.assertEquals(3, courseIds.get(1).longValue());
        Assert.assertEquals(2, courseIds.get(2).longValue());

        status = CommonStatus.DISABLED.name();
        courseIds = service.getCourseInDepartment(departmentId, status);
        Assert.assertEquals(1, courseIds.size());
        Assert.assertEquals(5, courseIds.get(0).longValue());

        status = CommonStatus.DELETED.name();
        courseIds = service.getCourseInDepartment(departmentId, status);
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
    public void getUpdateStatus() {
        int departmentId = 11;
        long courseId = 1;
        String status = CommonStatus.DISABLED.name();

        List<CourseDepartmentRelationEntity> relations = repository.findByDepartmentIdAndCourseId(departmentId, courseId, CourseDepartmentRelationService.sort);
        Assert.assertEquals(2, relations.size());
        Assert.assertEquals(11, relations.get(0).getId());
        Assert.assertEquals(10, relations.get(1).getId());

        CourseDepartmentRelationBean relation = service.updateStatus(courseId, departmentId, status);
        Assert.assertEquals(11, relation.getId());
        Assert.assertEquals(courseId, relation.getCourseId());
        Assert.assertEquals(departmentId, relation.getDepartmentId());
        Assert.assertEquals(CommonStatus.DISABLED, relation.getStatus());

        relations = repository.findByDepartmentIdAndCourseId(departmentId, courseId, CourseDepartmentRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(11, relations.get(0).getId());
    }

    @Test
    public void addRelation() {
        int departmentId = 22;
        long courseId = 5;

        // already exists
        List<CourseDepartmentRelationEntity> relations = repository.findByDepartmentIdAndCourseId(departmentId, courseId, CourseDepartmentRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(19, relations.get(0).getId());
        Assert.assertEquals(CommonStatus.DISABLED, relations.get(0).getStatus());

        CourseDepartmentRelationBean relation = service.addCourseToDepartment(courseId, departmentId);
        Assert.assertEquals(19, relation.getId());
        Assert.assertEquals(CommonStatus.ENABLED, relation.getStatus());

        relations = repository.findByDepartmentIdAndCourseId(departmentId, courseId, CourseDepartmentRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(19, relations.get(0).getId());
        Assert.assertEquals(CommonStatus.ENABLED, relations.get(0).getStatus());

        // not exists
        courseId = 7;
        relations = repository.findByDepartmentIdAndCourseId(departmentId, courseId, CourseDepartmentRelationService.sort);
        Assert.assertEquals(0, relations.size());

        relation = service.addCourseToDepartment(courseId, departmentId);
        Assert.assertTrue(relation.getId()>0);
        Assert.assertEquals(CommonStatus.ENABLED, relation.getStatus());
        Assert.assertEquals(departmentId, relation.getDepartmentId());
        Assert.assertEquals(courseId, relation.getCourseId());

        relations = repository.findByDepartmentIdAndCourseId(departmentId, courseId, CourseDepartmentRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(relation.getId(), relations.get(0).getId());
    }

    @Test
    public void testSetDepartmentRelation() {
        long courseId = 6;
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
