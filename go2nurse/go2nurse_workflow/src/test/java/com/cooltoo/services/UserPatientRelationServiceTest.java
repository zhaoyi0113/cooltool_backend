package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.UserPatientRelationBean;
import com.cooltoo.go2nurse.converter.UserPatientRelationBeanConverter;
import com.cooltoo.go2nurse.entities.UserPatientRelationEntity;
import com.cooltoo.go2nurse.repository.UserPatientRelationRepository;
import com.cooltoo.go2nurse.service.UserPatientRelationService;
import com.cooltoo.util.VerifyUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hp on 2016/6/14.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/user_patient_relation_data.xml")
})
public class UserPatientRelationServiceTest extends AbstractCooltooTest {

    @Autowired private UserPatientRelationService relationService;
    @Autowired private UserPatientRelationRepository relationRepository;
    @Autowired private UserPatientRelationBeanConverter beanConverter;

    @Test
    public void testGetPatientByUserId() {
        long userId = 3;
        String status = "";
        List<Long> patientsId = relationService.getPatientByUser(userId, status);
        Assert.assertEquals(0, patientsId.size());

        status = "ALL";
        patientsId = relationService.getPatientByUser(userId, status);
        Assert.assertEquals(4, patientsId.size());
        Assert.assertEquals(7, patientsId.get(0).longValue());
        Assert.assertEquals(6, patientsId.get(1).longValue());
        Assert.assertEquals(5, patientsId.get(2).longValue());
        Assert.assertEquals(4, patientsId.get(3).longValue());

        status = CommonStatus.ENABLED.name();
        patientsId = relationService.getPatientByUser(userId, status);
        Assert.assertEquals(2, patientsId.size());
        Assert.assertEquals(7, patientsId.get(0).longValue());
        Assert.assertEquals(4, patientsId.get(1).longValue());

        status = CommonStatus.DELETED.name();
        patientsId = relationService.getPatientByUser(userId, status);
        Assert.assertEquals(1, patientsId.size());
        Assert.assertEquals(6, patientsId.get(0).longValue());

        status = CommonStatus.DISABLED.name();
        patientsId = relationService.getPatientByUser(userId, status);
        Assert.assertEquals(1, patientsId.size());
        Assert.assertEquals(5, patientsId.get(0).longValue());
    }

    @Test
    public void testGetUserIdByPatient() {
        List<Long> patientIds = Arrays.asList(new Long[]{3L, 7L, 9L});
        String status = "";
        List<Long> userIds = relationService.getUserIdByPatient(patientIds, status);
        Assert.assertEquals(0, userIds.size());

        status = "ALL";
        userIds = relationService.getUserIdByPatient(patientIds, status);
        Assert.assertEquals(3, userIds.size());
        Assert.assertEquals(5, userIds.get(0).longValue());
        Assert.assertEquals(3, userIds.get(1).longValue());
        Assert.assertEquals(2, userIds.get(2).longValue());

        status = CommonStatus.DISABLED.name();
        userIds = relationService.getUserIdByPatient(patientIds, status);
        Assert.assertEquals(1, userIds.size());
        Assert.assertEquals(5, userIds.get(0).longValue());
    }

    @Test
    public void testUpdateStatus() {
        long userId = 1;
        long patientId = 1;
        UserPatientRelationEntity relation = null;
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));

        relationService.updateStatus(userId, patientId, CommonStatus.DISABLED.name());
        relation = relationRepository.findByPatientIdAndUserId(patientId, userId, sort).get(0);
        UserPatientRelationBean relationBean2 = beanConverter.convert(relation);
        Assert.assertEquals(patientId, relationBean2.getPatientId());
        Assert.assertEquals(userId, relationBean2.getUserId());
        Assert.assertEquals(CommonStatus.DISABLED, relationBean2.getStatus());
    }

    @Test
    public void testAddPatientToUser() {
        long userId = 5;
        long patientId = 9;
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
        List<UserPatientRelationEntity> entities = relationRepository.findByPatientIdAndUserId(patientId, userId, sort);
        UserPatientRelationBean bean1 = beanConverter.convert(entities.get(0));
        Assert.assertEquals(CommonStatus.DISABLED, bean1.getStatus());

        UserPatientRelationBean bean2 = relationService.addPatientToUser(patientId, userId);
        Assert.assertEquals(bean1.getId(), bean2.getId());
        Assert.assertNotEquals(bean1.getStatus(), bean2.getStatus());
        Assert.assertEquals(CommonStatus.ENABLED, bean2.getStatus());


        userId = 9;
        entities = relationRepository.findByPatientIdAndUserId(patientId, userId, sort);
        bean1 = VerifyUtil.isListEmpty(entities) ? null : beanConverter.convert(entities.get(0));
        Assert.assertNull(bean1);
        relationService.addPatientToUser(patientId, userId);
        entities = relationRepository.findByPatientIdAndUserId(patientId, userId, sort);
        bean1 = VerifyUtil.isListEmpty(entities) ? null : beanConverter.convert(entities.get(0));
        Assert.assertNotNull(bean1);

    }
}
