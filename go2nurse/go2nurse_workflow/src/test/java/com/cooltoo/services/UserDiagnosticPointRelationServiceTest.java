package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.UserDiagnosticPointRelationBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.service.UserDiagnosticPointRelationService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/6/15.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup("classpath:/com/cooltoo/services/user_diagnostic_point_relation_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/user_data.xml")
})
public class UserDiagnosticPointRelationServiceTest extends AbstractCooltooTest {

    @Autowired private UserDiagnosticPointRelationService relationService;

    @Test
    public void testCountByUserAndStatus() {
        long userId = 2;
        String status = "ALL";
        long count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(4, count);

        status = CommonStatus.ENABLED.name();
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(3, count);

        status = CommonStatus.DISABLED.name();
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testGetCourseInHospital() {
        int userId = 2;
        String status = "ALL";
        List<UserDiagnosticPointRelationBean> diagnosticPoints = relationService.getRelation(userId, status);
        Assert.assertEquals(4, diagnosticPoints.size());
        Assert.assertEquals(1, diagnosticPoints.get(0).getDiagnosticId());
        Assert.assertEquals(2, diagnosticPoints.get(1).getDiagnosticId());
        Assert.assertEquals(3, diagnosticPoints.get(2).getDiagnosticId());
        Assert.assertEquals(5, diagnosticPoints.get(3).getDiagnosticId());

        status = CommonStatus.ENABLED.name();
        diagnosticPoints = relationService.getRelation(userId, status);
        Assert.assertEquals(3, diagnosticPoints.size());
        Assert.assertEquals(1, diagnosticPoints.get(0).getDiagnosticId());
        Assert.assertEquals(2, diagnosticPoints.get(1).getDiagnosticId());
        Assert.assertEquals(5, diagnosticPoints.get(2).getDiagnosticId());

        status = CommonStatus.DISABLED.name();
        diagnosticPoints = relationService.getRelation(userId, status);
        Assert.assertEquals(1, diagnosticPoints.size());
        Assert.assertEquals(3, diagnosticPoints.get(0).getDiagnosticId());
    }

    @Test
    public void getUpdateStatus() {
        long relationId = 1;
        Date pointTime = new Date();
        String status = CommonStatus.DISABLED.name();
        UserDiagnosticPointRelationBean bean;

        long userId = 1;
        long count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(0, count);

        userId = 2;
        Throwable error = null;
        try { relationService.updateUserDiagnosticRelation(relationId, true, userId, pointTime, status); }
        catch (Exception ex) { error= ex; }
        Assert.assertNotNull(error);

        userId = 1;
        bean = relationService.updateUserDiagnosticRelation(relationId, true, userId, pointTime, status);
        Assert.assertEquals(relationId, bean.getId());
        Assert.assertEquals(pointTime, bean.getDiagnosticTime());
        Assert.assertEquals(status, bean.getStatus().name());

        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);
    }

    @Test
    public void addRelation() {
        List<UserDiagnosticPointRelationBean> relations;
        long userId = 6;
        List<DiagnosticEnumeration> diagnosticPoints = Arrays.asList(new DiagnosticEnumeration[]{
                DiagnosticEnumeration.HOSPITALIZED_DATE,
                DiagnosticEnumeration.PHYSICAL_EXAMINATION,
                DiagnosticEnumeration.OPERATION,
                DiagnosticEnumeration.REHABILITATION,
                DiagnosticEnumeration.DISCHARGED_FROM_THE_HOSPITAL
        });
        long oneMonth = 1000*3600*24*30;
        List<Date> pointTimes = Arrays.asList(new Date[]{
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 2*oneMonth),
                new Date(System.currentTimeMillis() + 3*oneMonth),
                new Date(System.currentTimeMillis() + 4*oneMonth),
                new Date(System.currentTimeMillis() + 5*oneMonth),

        });
        String status = "ALL";

        long count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(0, count);

        relations = relationService.addUserDiagnosticRelation(userId, diagnosticPoints, pointTimes);
        Assert.assertEquals(5, relations.size());
        Assert.assertEquals(1, relations.get(0).getDiagnosticId());
        Assert.assertEquals(2, relations.get(1).getDiagnosticId());
        Assert.assertEquals(3, relations.get(2).getDiagnosticId());
        Assert.assertEquals(4, relations.get(3).getDiagnosticId());
        Assert.assertEquals(5, relations.get(4).getDiagnosticId());

        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(5, count);

        status = CommonStatus.ENABLED.name();
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(5, count);
    }

    @Test
    public void getUpdateStatusByDiagnosticIdAndUserId() {
        long diagnosticId = 1;
        Date pointTime = new Date();
        String status = CommonStatus.DISABLED.name();
        UserDiagnosticPointRelationBean bean;

        long userId = 1;
        long count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(0, count);

        userId = 1;
        bean = relationService.updateUserDiagnosticRelation(diagnosticId, userId, pointTime, status);
        Assert.assertEquals(userId, bean.getUserId());
        Assert.assertEquals(diagnosticId, bean.getDiagnosticId());
        Assert.assertEquals(pointTime, bean.getDiagnosticTime());
        Assert.assertEquals(status, bean.getStatus().name());

        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);
    }
}
