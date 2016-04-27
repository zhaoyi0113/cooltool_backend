package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.services.HospitalDepartmentRelationService;
import com.cooltoo.beans.HospitalDepartmentRelationBean;
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
@DatabaseSetup("classpath:/com/cooltoo/services/hospital_department_relation_data.xml")
public class HospitalDepartmentRelationServiceTest extends AbstractCooltooTest {

    @Autowired
    private HospitalDepartmentRelationService service;

    @Test
    public void testNew1() {
        HospitalDepartmentRelationBean bean = new  HospitalDepartmentRelationBean();
        bean.setHospitalId(22);
        bean.setDepartmentId(55);
        int id = service.newOne(bean);
        Assert.assertTrue(id>0);
        List<HospitalDepartmentRelationBean> all = service.getAll();
        Assert.assertTrue(all.size()>0);
    }

    @Test
    public void testNew2() {
        int id = service.newOne(22, 33);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void testGetAll() {
        List< HospitalDepartmentRelationBean> all = service.getAll();
        Assert.assertEquals(5, all.size());
    }

    @Test
    public void testGetOne() {
        HospitalDepartmentRelationBean one = service.getOneById(33);
        Assert.assertEquals(33, one.getId());
    }

    @Test
    public void testDeleteById() {
        HospitalDepartmentRelationBean bean = service.deleteById(22);
        Assert.assertEquals(22, bean.getId());
    }

    @Test
    public void testDeleteByUpdate() {
        HospitalDepartmentRelationBean bean = service.update(22, 44, 55);
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals(44, bean.getHospitalId());
        Assert.assertEquals(55, bean.getDepartmentId());

        bean.setDepartmentId(22);
        bean =  service.update(bean);
        Assert.assertEquals(22, bean.getId());
        Assert.assertEquals(22, bean.getDepartmentId());
    }
}
