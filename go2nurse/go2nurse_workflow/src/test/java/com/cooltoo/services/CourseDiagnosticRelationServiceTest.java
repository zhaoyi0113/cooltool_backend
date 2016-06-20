package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.CourseDiagnosticRelationBean;
import com.cooltoo.go2nurse.entities.CourseDiagnosticRelationEntity;
import com.cooltoo.go2nurse.repository.CourseDiagnosticRelationRepository;
import com.cooltoo.go2nurse.service.CourseDiagnosticRelationService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/12.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_diagnostic_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_data.xml")
})
public class CourseDiagnosticRelationServiceTest extends AbstractCooltooTest {

    @Autowired
    private CourseDiagnosticRelationService service;
    @Autowired
    private CourseDiagnosticRelationRepository repository;

    @Test
    public void testJudgeCourseInDiagnostic() {
        List<Long> checkingCoursesId = Arrays.asList(new Long[]{4L, 5L, 6L});
        int diagnosticId = 2;
        String status = "ALL";
        List<Long> validCoursesId = service.judgeCourseInDiagnostic(diagnosticId, checkingCoursesId, status);
        Assert.assertEquals(3, validCoursesId.size());
        Assert.assertEquals(6, validCoursesId.get(0).longValue());
        Assert.assertEquals(5, validCoursesId.get(1).longValue());
        Assert.assertEquals(4, validCoursesId.get(2).longValue());

        status = CommonStatus.ENABLED.name();
        validCoursesId = service.judgeCourseInDiagnostic(diagnosticId, checkingCoursesId, status);
        Assert.assertEquals(1, validCoursesId.size());
        Assert.assertEquals(4, validCoursesId.get(0).longValue());

        status = CommonStatus.DISABLED.name();
        validCoursesId = service.judgeCourseInDiagnostic(diagnosticId, checkingCoursesId, status);
        Assert.assertEquals(1, validCoursesId.size());
        Assert.assertEquals(5, validCoursesId.get(0).longValue());

        status = CommonStatus.DELETED.name();
        validCoursesId = service.judgeCourseInDiagnostic(diagnosticId, checkingCoursesId, status);
        Assert.assertEquals(1, validCoursesId.size());
        Assert.assertEquals(6, validCoursesId.get(0).longValue());
    }

    @Test
    public void testGetCourseInDiagnostic() {
        long diagnosticId = 2;
        String status = "ALL";
        List<Long> courseIds = service.getCourseInDiagnostic(diagnosticId, status);
        Assert.assertEquals(3, courseIds.size());
        Assert.assertEquals(6, courseIds.get(0).longValue());
        Assert.assertEquals(5, courseIds.get(1).longValue());
        Assert.assertEquals(4, courseIds.get(2).longValue());

        status = CommonStatus.ENABLED.name();
        courseIds = service.getCourseInDiagnostic(diagnosticId, status);
        Assert.assertEquals(1, courseIds.size());
        Assert.assertEquals(4, courseIds.get(0).longValue());

        status = CommonStatus.DISABLED.name();
        courseIds = service.getCourseInDiagnostic(diagnosticId, status);
        Assert.assertEquals(1, courseIds.size());
        Assert.assertEquals(5, courseIds.get(0).longValue());

        status = CommonStatus.DELETED.name();
        courseIds = service.getCourseInDiagnostic(diagnosticId, status);
        Assert.assertEquals(1, courseIds.size());
        Assert.assertEquals(6, courseIds.get(0).longValue());
    }

    @Test
    public void testGetDiagnosticByCourseId() {
        List<Long> courseIds = Arrays.asList(new Long[]{2L, 5L});
        String status = "ALL";
        List<Long> diagnosticIds = service.getDiagnosticByCourseId(courseIds, status);
        Assert.assertEquals(3, diagnosticIds.size());
        Assert.assertEquals(5, diagnosticIds.get(0).longValue());
        Assert.assertEquals(4, diagnosticIds.get(1).longValue());
        Assert.assertEquals(2, diagnosticIds.get(2).longValue());

        status = CommonStatus.ENABLED.name();
        diagnosticIds = service.getDiagnosticByCourseId(courseIds, status);
        Assert.assertEquals(2, diagnosticIds.size());
        Assert.assertEquals(5, diagnosticIds.get(0).longValue());
        Assert.assertEquals(4, diagnosticIds.get(1).longValue());

        status = CommonStatus.DISABLED.name();
        diagnosticIds = service.getDiagnosticByCourseId(courseIds, status);
        Assert.assertEquals(1, diagnosticIds.size());
        Assert.assertEquals(2, diagnosticIds.get(0).longValue());
    }

    @Test
    public void getUpdateStatus() {
        long diagnosticId = 1;
        long courseId = 1;
        String status = CommonStatus.DISABLED.name();

        List<CourseDiagnosticRelationEntity> relations = repository.findByDiagnosticIdAndCourseId(diagnosticId, courseId, CourseDiagnosticRelationService.sort);
        Assert.assertEquals(2, relations.size());
        Assert.assertEquals(11, relations.get(0).getId());
        Assert.assertEquals(10, relations.get(1).getId());

        CourseDiagnosticRelationBean relation = service.updateStatus(courseId, diagnosticId, status);
        Assert.assertEquals(11, relation.getId());
        Assert.assertEquals(courseId, relation.getCourseId());
        Assert.assertEquals(diagnosticId, relation.getDiagnosticId());
        Assert.assertEquals(CommonStatus.DISABLED, relation.getStatus());

        relations = repository.findByDiagnosticIdAndCourseId(diagnosticId, courseId, CourseDiagnosticRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(11, relations.get(0).getId());
    }

    @Test
    public void addRelation() {
        long diagnosticId = 2;
        long courseId = 5;

        // already exists
        List<CourseDiagnosticRelationEntity> relations = repository.findByDiagnosticIdAndCourseId(diagnosticId, courseId, CourseDiagnosticRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(19, relations.get(0).getId());
        Assert.assertEquals(CommonStatus.DISABLED, relations.get(0).getStatus());

        CourseDiagnosticRelationBean relation = service.addCourseToDiagnostic(courseId, diagnosticId);
        Assert.assertEquals(19, relation.getId());
        Assert.assertEquals(CommonStatus.ENABLED, relation.getStatus());

        relations = repository.findByDiagnosticIdAndCourseId(diagnosticId, courseId, CourseDiagnosticRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(19, relations.get(0).getId());
        Assert.assertEquals(CommonStatus.ENABLED, relations.get(0).getStatus());

        // not exists
        courseId = 7;
        relations = repository.findByDiagnosticIdAndCourseId(diagnosticId, courseId, CourseDiagnosticRelationService.sort);
        Assert.assertEquals(0, relations.size());

        relation = service.addCourseToDiagnostic(courseId, diagnosticId);
        Assert.assertTrue(relation.getId()>0);
        Assert.assertEquals(CommonStatus.ENABLED, relation.getStatus());
        Assert.assertEquals(diagnosticId, relation.getDiagnosticId());
        Assert.assertEquals(courseId, relation.getCourseId());

        relations = repository.findByDiagnosticIdAndCourseId(diagnosticId, courseId, CourseDiagnosticRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(relation.getId(), relations.get(0).getId());
    }

    @Test
    public void testSetDiagnosticRelation() {
        long courseId = 4;
        List<Long> settingDiagnosticIds = Arrays.asList(new Long[]{0L, 1L, 3L});
        List<Long> existed = service.getDiagnosticByCourseId(Arrays.asList(new Long(courseId)), "ENABLED");
        Assert.assertEquals(2, existed.size());
        Assert.assertTrue(existed.contains(Long.valueOf(2L)));
        Assert.assertTrue(existed.contains(Long.valueOf(4L)));


        service.setCourseToDiagnosticRelation(courseId, settingDiagnosticIds);

        List<Long> newExisted = service.getDiagnosticByCourseId(Arrays.asList(new Long[]{courseId}), "ENABLED");
        Assert.assertEquals(3, newExisted.size());
        Assert.assertTrue(newExisted.contains(Long.valueOf(0L)));
        Assert.assertTrue(newExisted.contains(Long.valueOf(1L)));
        Assert.assertTrue(newExisted.contains(Long.valueOf(3L)));
        Assert.assertFalse(newExisted.contains(Long.valueOf(4L)));
        Assert.assertFalse(newExisted.contains(Long.valueOf(2L)));
    }

    @Test
    public void testGetDiagnosticToCourseIds() {
        List<Long> coursesId = Arrays.asList(new Long[]{5L, 6L});
        CommonStatus status = null;
        Map<Long, List<Long>> diagnosticToCourses = service.getDiagnosticToCourseIds(coursesId, status);
        Assert.assertEquals(0, diagnosticToCourses.size());

        status = CommonStatus.ENABLED;
        diagnosticToCourses = service.getDiagnosticToCourseIds(coursesId, status);
        Assert.assertEquals(3, diagnosticToCourses.size());
        Assert.assertTrue(diagnosticToCourses.keySet().contains(Long.valueOf(3L)));
        Assert.assertTrue(diagnosticToCourses.keySet().contains(Long.valueOf(4L)));
        Assert.assertTrue(diagnosticToCourses.keySet().contains(Long.valueOf(5L)));
        Assert.assertEquals(1, diagnosticToCourses.get(Long.valueOf(3L)).size());
        Assert.assertEquals(Long.valueOf(6), diagnosticToCourses.get(Long.valueOf(3L)).get(0));
        Assert.assertEquals(1, diagnosticToCourses.get(Long.valueOf(4L)).size());
        Assert.assertEquals(Long.valueOf(5), diagnosticToCourses.get(Long.valueOf(4L)).get(0));
        Assert.assertEquals(1, diagnosticToCourses.get(Long.valueOf(5L)).size());
        Assert.assertEquals(Long.valueOf(5), diagnosticToCourses.get(Long.valueOf(5L)).get(0));

        status = CommonStatus.DISABLED;
        diagnosticToCourses = service.getDiagnosticToCourseIds(coursesId, status);
        Assert.assertEquals(1, diagnosticToCourses.size());
        Assert.assertTrue(diagnosticToCourses.keySet().contains(Long.valueOf(2L)));
        Assert.assertEquals(1, diagnosticToCourses.get(Long.valueOf(2L)).size());
        Assert.assertEquals(Long.valueOf(5), diagnosticToCourses.get(Long.valueOf(2L)).get(0));

        status = CommonStatus.DELETED;
        diagnosticToCourses = service.getDiagnosticToCourseIds(coursesId, status);
        Assert.assertEquals(1, diagnosticToCourses.size());
        Assert.assertTrue(diagnosticToCourses.keySet().contains(Long.valueOf(2L)));
        Assert.assertEquals(1, diagnosticToCourses.get(Long.valueOf(2L)).size());
        Assert.assertEquals(Long.valueOf(6), diagnosticToCourses.get(Long.valueOf(2L)).get(0));
    }
}
