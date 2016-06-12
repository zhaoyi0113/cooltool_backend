package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.CourseHospitalRelationBean;
import com.cooltoo.go2nurse.entities.CourseHospitalRelationEntity;
import com.cooltoo.go2nurse.repository.CourseHospitalRelationRepository;
import com.cooltoo.go2nurse.service.CourseHospitalRelationService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hp on 2016/6/12.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_hospital_relation_data.xml")
})
public class CourseHospitalRelationServiceTest extends AbstractCooltooTest {

    @Autowired
    private CourseHospitalRelationService service;
    @Autowired
    private CourseHospitalRelationRepository repository;

    @Test
    public void testGetCourseInHospital() {
        int hospitalId = 22;
        String status = "ALL";
        List<Long> courseIds = service.getCourseInHospital(hospitalId, status);
        Assert.assertEquals(5, courseIds.size());
        Assert.assertEquals(6, courseIds.get(0).longValue());
        Assert.assertEquals(5, courseIds.get(1).longValue());
        Assert.assertEquals(4, courseIds.get(2).longValue());
        Assert.assertEquals(3, courseIds.get(3).longValue());
        Assert.assertEquals(2, courseIds.get(4).longValue());

        status = CommonStatus.ENABLED.name();
        courseIds = service.getCourseInHospital(hospitalId, status);
        Assert.assertEquals(3, courseIds.size());
        Assert.assertEquals(4, courseIds.get(0).longValue());
        Assert.assertEquals(3, courseIds.get(1).longValue());
        Assert.assertEquals(2, courseIds.get(2).longValue());

        status = CommonStatus.DISABLED.name();
        courseIds = service.getCourseInHospital(hospitalId, status);
        Assert.assertEquals(1, courseIds.size());
        Assert.assertEquals(5, courseIds.get(0).longValue());

        status = CommonStatus.DELETED.name();
        courseIds = service.getCourseInHospital(hospitalId, status);
        Assert.assertEquals(1, courseIds.size());
        Assert.assertEquals(6, courseIds.get(0).longValue());
    }

    @Test
    public void getUpdateStatus() {
        int hospitalId = 11;
        long courseId = 1;
        String status = CommonStatus.DISABLED.name();

        List<CourseHospitalRelationEntity> relations = repository.findByHospitalIdAndCourseId(hospitalId, courseId, CourseHospitalRelationService.sort);
        Assert.assertEquals(2, relations.size());
        Assert.assertEquals(11, relations.get(0).getId());
        Assert.assertEquals(10, relations.get(1).getId());

        CourseHospitalRelationBean relation = service.updateStatus(courseId, hospitalId, status);
        Assert.assertEquals(11, relation.getId());
        Assert.assertEquals(courseId, relation.getCourseId());
        Assert.assertEquals(hospitalId, relation.getHospitalId());
        Assert.assertEquals(CommonStatus.DISABLED, relation.getStatus());

        relations = repository.findByHospitalIdAndCourseId(hospitalId, courseId, CourseHospitalRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(11, relations.get(0).getId());
    }

    @Test
    public void addRelation() {
        int hospitalId = 22;
        long courseId = 5;

        // already exists
        List<CourseHospitalRelationEntity> relations = repository.findByHospitalIdAndCourseId(hospitalId, courseId, CourseHospitalRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(19, relations.get(0).getId());
        Assert.assertEquals(CommonStatus.DISABLED, relations.get(0).getStatus());

        CourseHospitalRelationBean relation = service.addCourseToHospital(courseId, hospitalId);
        Assert.assertEquals(19, relation.getId());
        Assert.assertEquals(CommonStatus.ENABLED, relation.getStatus());

        relations = repository.findByHospitalIdAndCourseId(hospitalId, courseId, CourseHospitalRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(19, relations.get(0).getId());
        Assert.assertEquals(CommonStatus.ENABLED, relations.get(0).getStatus());

        // not exists
        courseId = 7;
        relations = repository.findByHospitalIdAndCourseId(hospitalId, courseId, CourseHospitalRelationService.sort);
        Assert.assertEquals(0, relations.size());

        relation = service.addCourseToHospital(courseId, hospitalId);
        Assert.assertTrue(relation.getId()>0);
        Assert.assertEquals(CommonStatus.ENABLED, relation.getStatus());
        Assert.assertEquals(hospitalId, relation.getHospitalId());
        Assert.assertEquals(courseId, relation.getCourseId());

        relations = repository.findByHospitalIdAndCourseId(hospitalId, courseId, CourseHospitalRelationService.sort);
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(relation.getId(), relations.get(0).getId());
    }
}
