package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.serivces.NurseHospitalRelationService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Transactional
public class NurseHospitalRelationServiceTest extends AbstractCooltooTest {

    @Autowired
    private NurseHospitalRelationService service;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_hospital_relation_data.xml")
    public void testNew1() {
        NurseHospitalRelationBean bean = new NurseHospitalRelationBean();
        bean.setNurseId(11);
        bean.setDepartmentId(55);
        bean.setHospitalId(22);
        long id = service.newOne(bean);
        Assert.assertTrue(id>0);
        List<NurseHospitalRelationBean> all = service.getAll();
        Assert.assertTrue(all.size()>0);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_hospital_relation_data.xml")
    public void testNew2() {
        long id = service.newOne(11, 22, 33);
        Assert.assertTrue(id > 0);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_hospital_relation_data.xml")
    public void testGetAll() {
        List< NurseHospitalRelationBean> all = service.getAll();
        Assert.assertEquals(5, all.size());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_hospital_relation_data.xml")
    public void testGetOne() {
        NurseHospitalRelationBean one = service.getOneById(33L);
        Assert.assertEquals(33, one.getId());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_hospital_relation_data.xml")
    public void testDeleteById() {
        NurseHospitalRelationBean bean = service.deleteById(22L);
        Assert.assertEquals(22, bean.getId());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_hospital_relation_data.xml")
    public void testDeleteByUpdate() {
        NurseHospitalRelationBean bean = service.update(11, 22, 33, 44);
        Assert.assertEquals(11, bean.getId());
        Assert.assertEquals(22, bean.getNurseId());
        Assert.assertEquals(33, bean.getHospitalId());
        Assert.assertEquals(44, bean.getDepartmentId());

        bean.setDepartmentId(22);
        bean =  service.update(bean);
        Assert.assertEquals(11, bean.getId());
        Assert.assertEquals(22, bean.getDepartmentId());
    }
}
