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
    public void testGetDiagnosticByCourseId() {
        List<Long> courseIds = Arrays.asList(new Long[]{2L, 5L});
        String status = "ALL";
        List<Long> diagnosticIds = service.getDiagnosticByCourseId(courseIds);
        Assert.assertEquals(2, diagnosticIds.size());
        Assert.assertEquals(4, diagnosticIds.get(0).longValue());
        Assert.assertEquals(2, diagnosticIds.get(1).longValue());
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
        Assert.assertEquals(0, relations.size());;
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
}
