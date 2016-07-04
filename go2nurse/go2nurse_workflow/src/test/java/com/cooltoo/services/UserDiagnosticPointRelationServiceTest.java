package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.UserDiagnosticPointRelationBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.service.UserDiagnosticPointRelationService;
import com.cooltoo.util.NumberUtil;
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

    //==========================================================
    //             getting for user
    //==========================================================
    @Test
    public void getUserCurrentGroupId() {
        long userId = 1;
        long currentTime = System.currentTimeMillis();
        long groupId = relationService.getUserCurrentGroupId(userId, currentTime);
        Assert.assertEquals(Long.MIN_VALUE, groupId);

        currentTime = NumberUtil.getTime("2016-01-15 14:44:44", NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        groupId = relationService.getUserCurrentGroupId(userId, currentTime);
        Assert.assertEquals(2, groupId);
    }

    @Test
    public void testGetUserAllGroupIds() {
        long userId = 1;
        List<Long> groupIds = relationService.getUserAllGroupIds(userId);
        Assert.assertEquals(2, groupIds.size());
        Assert.assertEquals(2, groupIds.get(0).intValue());
        Assert.assertEquals(1, groupIds.get(1).intValue());
    }

    @Test
    public void testGetUserDiagnosticRelationByGroupId() {
        long userId = 1;
        long groupId = 1;
        List<UserDiagnosticPointRelationBean> relations = relationService.getUserDiagnosticRelationByGroupId(userId, groupId);
        Assert.assertEquals(3, relations.size());
        Assert.assertEquals(5, relations.get(0).getDiagnosticId());
        Assert.assertEquals(3, relations.get(1).getDiagnosticId());
        Assert.assertEquals(1, relations.get(2).getDiagnosticId());
        Assert.assertEquals(groupId, relations.get(0).getGroupId());
        Assert.assertEquals(groupId, relations.get(1).getGroupId());
        Assert.assertEquals(groupId, relations.get(2).getGroupId());
    }

    //==========================================================
    //
    //==========================================================

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
        Assert.assertEquals(5, diagnosticPoints.get(0).getDiagnosticId());
        Assert.assertEquals(3, diagnosticPoints.get(1).getDiagnosticId());
        Assert.assertEquals(2, diagnosticPoints.get(2).getDiagnosticId());
        Assert.assertEquals(1, diagnosticPoints.get(3).getDiagnosticId());

        status = CommonStatus.ENABLED.name();
        diagnosticPoints = relationService.getRelation(userId, status);
        Assert.assertEquals(3, diagnosticPoints.size());
        Assert.assertEquals(5, diagnosticPoints.get(0).getDiagnosticId());
        Assert.assertEquals(2, diagnosticPoints.get(1).getDiagnosticId());
        Assert.assertEquals(1, diagnosticPoints.get(2).getDiagnosticId());

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
        long groupId = 14;
        List<DiagnosticEnumeration> diagnosticPoints = Arrays.asList(new DiagnosticEnumeration[]{
                DiagnosticEnumeration.HOSPITALIZED_DATE,
                DiagnosticEnumeration.PHYSICAL_EXAMINATION,
                DiagnosticEnumeration.OPERATION,
                DiagnosticEnumeration.DISCHARGED_FROM_THE_HOSPITAL
        });
        long oneMonth = 1000*3600*24*30;
        List<Date> pointTimes = Arrays.asList(new Date[]{
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 2*oneMonth),
                new Date(System.currentTimeMillis() + 3*oneMonth),
                new Date(System.currentTimeMillis() + 4*oneMonth)

        });
        String status = "ALL";

        long count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(0, count);

        relations = relationService.addUserDiagnosticRelation(userId, groupId, diagnosticPoints, pointTimes);
        Assert.assertEquals(4, relations.size());
        Assert.assertEquals(1, relations.get(0).getDiagnosticId());
        Assert.assertEquals(groupId, relations.get(0).getGroupId());
        Assert.assertEquals(2, relations.get(1).getDiagnosticId());
        Assert.assertEquals(groupId, relations.get(1).getGroupId());
        Assert.assertEquals(3, relations.get(2).getDiagnosticId());
        Assert.assertEquals(groupId, relations.get(2).getGroupId());
        Assert.assertEquals(4, relations.get(3).getDiagnosticId());
        Assert.assertEquals(groupId, relations.get(3).getGroupId());

        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(4, count);

        status = CommonStatus.ENABLED.name();
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(4, count);
    }

    @Test
    public void getUpdateStatusByDiagnosticIdAndUserId() {
        long diagnosticId = 1;
        long groupId = 1;
        Date pointTime = new Date();
        String status = CommonStatus.DISABLED.name();
        UserDiagnosticPointRelationBean bean;

        long userId = 1;
        long count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(0, count);

        userId = 1;
        bean = relationService.updateUserDiagnosticRelation(groupId, diagnosticId, userId, pointTime, status);
        Assert.assertEquals(userId, bean.getUserId());
        Assert.assertEquals(diagnosticId, bean.getDiagnosticId());
        Assert.assertEquals(groupId, bean.getGroupId());
        Assert.assertEquals(pointTime, bean.getDiagnosticTime());
        Assert.assertEquals(status, bean.getStatus().name());

        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testCancelUserDiagnosticRelation() {
        long userId = 1;
        long groupId = 1;
        List<UserDiagnosticPointRelationBean> beans = relationService.getUserDiagnosticRelationByGroupId(userId, groupId);
        for (UserDiagnosticPointRelationBean bean : beans) {
            Assert.assertEquals(YesNoEnum.NO, bean.getCancelled());
        }

        relationService.cancelUserDiagnosticRelation(userId, groupId);

        beans = relationService.getUserDiagnosticRelationByGroupId(userId, groupId);
        for (UserDiagnosticPointRelationBean bean : beans) {
            Assert.assertEquals(YesNoEnum.YES, bean.getCancelled());
        }
    }
}
