package com.cooltoo.services;

import com.cooltoo.beans.PatientBadgeBean;
import com.cooltoo.serivces.PatientBadgeService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/3.
 */
@Transactional
public class PatientBadgeServiceTest extends AbstractCooltooTest {

    @Autowired
    private PatientBadgeService service;

    @Test
    public void testNew1() {
        PatientBadgeBean bean = new PatientBadgeBean();
        bean.setBadgeId(1005);
        bean.setPatientId(13);
        int id = service.newOne(bean);
        Assert.assertTrue(id>0);
        List<PatientBadgeBean> all = service.getAll();
        Assert.assertTrue(all.size()>0);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/patient_badge_data.xml")
    public void testNew2() {
        int id = service.newOne(1, 1001);
        Assert.assertTrue(id > 0);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/patient_badge_data.xml")
    public void testGetAll() {
        List<PatientBadgeBean> all = service.getAll();
        Assert.assertEquals(7, all.size());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/patient_badge_data.xml")
    public void testGetOne() {
        PatientBadgeBean one = service.getOneById(33);
        Assert.assertEquals(33, one.getId());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/patient_badge_data.xml")
    public void testDeleteById() {
        PatientBadgeBean bean = service.deleteById(22);
        Assert.assertEquals(22, bean.getId());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/patient_badge_data.xml")
    public void testDeleteByUpdate() {
        PatientBadgeBean bean = service.update(22, 5, 1004);
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals(5, bean.getPatientId());
        Assert.assertEquals(1004, bean.getBadgeId());

        bean.setPatientId(7);
        bean.setBadgeId(1001);
        bean =  service.update(bean);
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals(7, bean.getPatientId());
        Assert.assertEquals(1001, bean.getBadgeId());
    }
}
