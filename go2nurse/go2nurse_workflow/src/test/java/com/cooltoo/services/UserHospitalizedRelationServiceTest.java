package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.UserHospitalizedRelationBean;
import com.cooltoo.go2nurse.service.UserHospitalizedRelationService;
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
        @DatabaseSetup("classpath:/com/cooltoo/services/user_hospitalized_relation_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/hospital_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/hospital_department_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/file_storage_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/user_data.xml")
})
public class UserHospitalizedRelationServiceTest extends AbstractCooltooTest {

    @Autowired private UserHospitalizedRelationService relationService;

    @Test
    public void testGetUserHospitalizedRelationByGroupId() {
        long userId = 1;
        long groupId = 1;
        List<UserHospitalizedRelationBean> relations = relationService.getUserHospitalizedRelationByGroupId(userId, groupId);
        Assert.assertEquals(2, relations.size());
        Assert.assertEquals(2, relations.get(0).getId());
        Assert.assertEquals(1, relations.get(1).getId());
    }

    @Test
    public void testGetUserAllHospitalizedRelation() {
        long userId = 1;
        List<UserHospitalizedRelationBean> relations = relationService.getUserAllHospitalizedRelation(userId);
        Assert.assertEquals(3, relations.size());
        Assert.assertEquals(3, relations.get(0).getId());
        Assert.assertEquals(2, relations.get(1).getId());
        Assert.assertEquals(1, relations.get(2).getId());
    }

    @Test
    public void testCountByUserAndStatus() {
        long userId = 2;
        String status = "ALL";
        long count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(3, count);

        status = CommonStatus.ENABLED.name();
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);

        status = CommonStatus.DISABLED.name();
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);

        status = CommonStatus.DELETED.name();
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testGetUserHospitalized() {
        int userId = 2;
        String status = "ALL";
        List<UserHospitalizedRelationBean> hospitalized = relationService.getRelation(userId, status);
        Assert.assertEquals(3, hospitalized.size());
        Assert.assertEquals(6, hospitalized.get(0).getId());
        Assert.assertEquals(5, hospitalized.get(1).getId());
        Assert.assertEquals(4, hospitalized.get(2).getId());
        System.out.print(hospitalized);

        status = CommonStatus.ENABLED.name();
        hospitalized = relationService.getRelation(userId, status);
        Assert.assertEquals(1, hospitalized.size());
        Assert.assertEquals(6, hospitalized.get(0).getId());

        status = CommonStatus.DISABLED.name();
        hospitalized = relationService.getRelation(userId, status);
        Assert.assertEquals(1, hospitalized.size());
        Assert.assertEquals(4, hospitalized.get(0).getId());

        status = CommonStatus.DELETED.name();
        hospitalized = relationService.getRelation(userId, status);
        Assert.assertEquals(1, hospitalized.size());
        Assert.assertEquals(5, hospitalized.get(0).getId());
    }

    @Test
    public void getUpdateStatus() {
        long relationId = 1;
        String hasLeave = "YES";
        String status = CommonStatus.DISABLED.name();
        UserHospitalizedRelationBean bean;

        long userId = 1;
        long count = 0;

        userId = 2;
        Throwable error = null;
        try { relationService.updateRelation(relationId, true, userId, hasLeave, status); }
        catch (Exception ex) { error= ex; }
        Assert.assertNotNull(error);

        userId = 1;
        bean = relationService.updateRelation(relationId, true, userId, hasLeave, status);
        Assert.assertEquals(relationId, bean.getId());
        Assert.assertEquals(status, bean.getStatus().name());
        Assert.assertEquals(hasLeave, bean.getHasLeave().name());

        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);
    }

    @Test
    public void getUpdateStatusByUserIdHospitalIdDepartmentId() {
        long relationId = 1;
        long groupId = 1;
        int hospitalId = 11;
        int departmentId = 11;
        String hasLeave = "YES";
        String status = CommonStatus.DISABLED.name();
        UserHospitalizedRelationBean bean;

        long userId = 1;
        long count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(0, count);

        bean = relationService.updateRelation(groupId, hospitalId, departmentId, userId, hasLeave, status);
        Assert.assertEquals(relationId, bean.getId());
        Assert.assertEquals(status, bean.getStatus().name());
        Assert.assertEquals(hasLeave, bean.getHasLeave().name());
        Assert.assertEquals(groupId, bean.getGroupId());

        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);
    }

    @Test
    public void addRelation() {
        UserHospitalizedRelationBean relations;
        long userId = 7;
        long groupId = 1;
        int hospitalId = 11;
        int departmentId =11;
        String status = "ALL";

        long count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(0, count);

        relations = relationService.addRelation(userId, groupId, hospitalId, departmentId);
        Assert.assertNotNull(relations);
        Assert.assertEquals(userId, relations.getUserId());
        Assert.assertEquals(groupId, relations.getGroupId());
        Assert.assertEquals(hospitalId, relations.getHospitalId());
        Assert.assertEquals(departmentId, relations.getDepartmentId());

        status = "ALL";
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);

        status = CommonStatus.ENABLED.name();
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);

        relations = relationService.addRelation(userId, groupId, hospitalId, departmentId);
        Assert.assertNotNull(relations);
        Assert.assertEquals(userId, relations.getUserId());
        Assert.assertEquals(groupId, relations.getGroupId());
        Assert.assertEquals(hospitalId, relations.getHospitalId());
        Assert.assertEquals(departmentId, relations.getDepartmentId());

        status = "ALL";
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(2, count);

        status = CommonStatus.ENABLED.name();
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);
    }

    @Test
    public void addRelationByUniqueId() {
        UserHospitalizedRelationBean relations;
        long userId = 7;
        long groupId = 7;
        int hospitalId = 11;
        String hospitalUniqueId = "111111";
        int departmentId = 11;
        String departmentUniqueId ="111111";
        String status = "ALL";

        long count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(0, count);

        relations = relationService.addRelation(userId, groupId, hospitalUniqueId, departmentUniqueId);
        Assert.assertNotNull(relations);
        Assert.assertEquals(userId, relations.getUserId());
        Assert.assertEquals(groupId, relations.getGroupId());
        Assert.assertEquals(hospitalId, relations.getHospitalId());
        Assert.assertEquals(departmentId, relations.getDepartmentId());

        status = "ALL";
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);

        status = CommonStatus.ENABLED.name();
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);

        relations = relationService.addRelation(userId, groupId, hospitalUniqueId, departmentUniqueId);
        Assert.assertNotNull(relations);
        Assert.assertEquals(userId, relations.getUserId());
        Assert.assertEquals(groupId, relations.getGroupId());
        Assert.assertEquals(hospitalId, relations.getHospitalId());
        Assert.assertEquals(departmentId, relations.getDepartmentId());

        status = "ALL";
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(2, count);

        status = CommonStatus.ENABLED.name();
        count = relationService.countByUserAndStatus(userId, status);
        Assert.assertEquals(1, count);
    }
}
