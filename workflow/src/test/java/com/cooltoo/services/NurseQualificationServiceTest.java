package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.services.NurseQualificationService;
import com.cooltoo.constants.VetStatus;
import com.cooltoo.constants.WorkFileType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by zhaolisong on 16/3/23.
 */
@Transactional
public class NurseQualificationServiceTest extends AbstractCooltooTest {

    private static final Logger logger = Logger.getLogger(NurseQualificationServiceTest.class.getName());

    @Autowired
    private NurseQualificationService qualService;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_qualification_data.xml")
    public void testNurseQualificationOK() {
        boolean isOk = false;
        isOk = qualService.isNurseQualificationOk(3);
        Assert.assertTrue(isOk);
        isOk = qualService.isNurseQualificationOk(2);
        Assert.assertFalse(isOk);
        isOk = qualService.isNurseQualificationOk(1);
        Assert.assertFalse(isOk);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_qualification_data.xml")
    public void testAddNurseQualification() {
        String name = "Identi";
        String content = "dsafdasfdasfdasflksajfdkla;jfdksaljfdksla";
        ByteArrayInputStream byteInput = new ByteArrayInputStream(content.getBytes());
        Date time = new Date();

        NurseQualificationBean bean = null;
        bean = qualService.addNurseWorkFile(4, name, "aaa.png", byteInput);
        System.out.println("add work file : " + bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(4, bean.getUserId());
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(WorkFileType.WORK_FILE, bean.getWorkFileType());
        Assert.assertTrue(bean.getWorkFileId() > 0);
        Assert.assertTrue(bean.getTimeCreated().getTime()>=time.getTime());

        name = "Identi2";
        bean = qualService.addNurseIdentificationFile(4, name, "aaa.png", byteInput);
        System.out.println("add identification file : " + bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(4, bean.getUserId());
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(WorkFileType.IDENTIFICATION, bean.getWorkFileType());
        Assert.assertTrue(bean.getWorkFileId() > 0);
        Assert.assertTrue(bean.getTimeCreated().getTime()>=time.getTime());
    }


    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_qualification_data.xml")
    public void testUpdateNurseQualification() {
        List<NurseQualificationBean> qualifications = qualService.getAllNurseQualifications(1);
        boolean isOk = false;

        isOk = qualService.isNurseQualificationOk(1);
        Assert.assertFalse(isOk);
        for (NurseQualificationBean bean : qualifications) {
            System.out.println(bean);
            if (!VetStatus.COMPLETED.equals(bean.getStatus())) {
                if (WorkFileType.WORK_FILE.equals(bean.getWorkFileType())) {
                    qualService.updateNurseWorkFile(bean.getId(), bean.getName(), null, null, VetStatus.COMPLETED);
                }
                else {
                    qualService.updateNurseIdentificationFile(bean.getId(), bean.getName(), null, null, VetStatus.COMPLETED);
                }
            }
        }
        qualifications = qualService.getAllNurseQualifications(1);
        for (NurseQualificationBean bean : qualifications) {
            System.out.println(bean);
        }

        isOk = qualService.isNurseQualificationOk(1);
        Assert.assertTrue(isOk);
    }


    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_qualification_data.xml")
    public void testDeleteNurseQualification() {
        List<NurseQualificationBean> qualifications = qualService.getAllNurseQualifications(1);
        for (NurseQualificationBean bean : qualifications) {
            System.out.println(bean);
            qualService.deleteNurseQualification(bean.getId());
        }
        qualifications = qualService.getAllNurseQualifications(1);
        Assert.assertTrue(qualifications.isEmpty());
    }
}
