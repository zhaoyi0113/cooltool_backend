package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.backend.services.NurseHospitalRelationService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_hospital_relation_data.xml")
})
public class NurseHospitalRelationServiceTest extends AbstractCooltooTest {

    @Autowired
    private NurseHospitalRelationService service;

    @Test
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
    public void testNew2() {
        long id = service.newOne(11, 22, 33);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void testGetAll() {
        List< NurseHospitalRelationBean> all = service.getAll();
        Assert.assertEquals(5, all.size());
    }

    @Test
    public void testGetOne() {
        NurseHospitalRelationBean one = service.getOneById(33L);
        Assert.assertEquals(33, one.getId());
    }
}
