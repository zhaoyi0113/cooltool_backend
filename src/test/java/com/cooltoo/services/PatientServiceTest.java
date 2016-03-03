package com.cooltoo.services;

import com.cooltoo.beans.PatientBean;
import com.cooltoo.serivces.PatientService;
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
public class PatientServiceTest extends AbstractCooltooTest {

    @Autowired
    private PatientService service;

    @Test
    public void testCreate() {
        PatientBean bean = new PatientBean();
        bean.setName("name_1");
        bean.setNickname("nickname_1");
        bean.setMobile("18511663355");
        bean.setOfficeId(12);
        bean.setCertificateId(12);
        long id = service.create(bean);
        Assert.assertTrue(id>0);
        List<PatientBean> all = service.getAll();
        Assert.assertTrue(all.size()>0);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/patient_data.xml")
    public void testGetAll() {
        List<PatientBean> all = service.getAll();
        for (PatientBean bean : all) {
            System.out.println(bean);
        }
        Assert.assertEquals(9, all.size());
    }

    @Test
    @DatabaseSetup(value="classpath:/com/cooltoo/services/patient_data.xml")
    public void testDelete() {
        PatientBean bean = service.delete(5);
        Assert.assertNotNull(bean);
        Assert.assertEquals(5, bean.getId());
        bean = service.getOneById(5);
        Assert.assertNull(bean);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/patient_data.xml")
    public void testUpdate(){
        PatientBean bean = service.update(5, null,"nickname55555", 33, 654, "14212333221", 2, null, "e21ewq");
        Assert.assertNotNull(bean);
        Assert.assertEquals(5, bean.getId());
        Assert.assertEquals("nickname55555", bean.getNickname());
        Assert.assertEquals(33,  bean.getCertificateId());
        Assert.assertEquals(654, bean.getOfficeId());
        Assert.assertEquals(2, bean.getAge());
        Assert.assertEquals("e21ewq", bean.getUsercol());
        Assert.assertNotEquals("14212333221", bean.getMobile());
    }
}
