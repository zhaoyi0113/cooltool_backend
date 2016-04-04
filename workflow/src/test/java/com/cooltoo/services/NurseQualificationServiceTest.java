package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.beans.WorkFileTypeBean;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaolisong on 16/3/23.
 */
@Transactional
public class NurseQualificationServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(NurseQualificationServiceTest.class.getName());

    @Autowired
    private NurseQualificationService qualService;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_qualification_data.xml")
    public void testNurseQualificationOK() {
        boolean isOk;
        isOk = true;
        List<NurseQualificationBean> qualifications = qualService.getAllNurseQualifications(3);
        for (NurseQualificationBean bean : qualifications) {
            if (!VetStatus.COMPLETED.equals(bean.getStatus())) {
                isOk = false;
                break;
            }
        }
        Assert.assertTrue(isOk);
        isOk = true;
        qualifications = qualService.getAllNurseQualifications(2);
        for (NurseQualificationBean bean : qualifications) {
            if (!VetStatus.COMPLETED.equals(bean.getStatus())) {
                isOk = false;
                break;
            }
        }
        Assert.assertFalse(isOk);
        isOk = true;
        qualifications = qualService.getAllNurseQualifications(1);
        for (NurseQualificationBean bean : qualifications) {
            if (!VetStatus.COMPLETED.equals(bean.getStatus())) {
                isOk = false;
                break;
            }
        }
        Assert.assertFalse(isOk);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_qualification_data.xml")
    public void testAddNurseQualification() {
        String name = "Identi";
        String content = "dsafdasfdasfdasflksajfdkla;jfdksaljfdksla";
        ByteArrayInputStream byteInput = new ByteArrayInputStream(content.getBytes());
        Date time = new Date();

        WorkFileTypeBean workFileType = qualService.getWorkFileTypeBean(WorkFileType.EMPLOYEES_CARD.name());

        NurseQualificationBean bean = null;
        bean = qualService.addWorkFile(4, name, WorkFileType.EMPLOYEES_CARD.name(), "aaa.png", byteInput);
        logger.info("add work file : " + bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(4, bean.getUserId());
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(workFileType.getId(), bean.getWorkFileType());
        Assert.assertTrue(bean.getWorkFileId() > 0);
        Assert.assertTrue(bean.getTimeCreated().getTime()>=time.getTime());

        workFileType = qualService.getWorkFileTypeBean(WorkFileType.IDENTIFICATION.name());
        name = "Identi2";
        bean = qualService.addWorkFile(4, name, workFileType.getName(), "aaa.png", byteInput);
        logger.info("add identification file : " + bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(4, bean.getUserId());
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(workFileType.getId(), bean.getWorkFileType());
        Assert.assertTrue(bean.getWorkFileId() > 0);
        Assert.assertTrue(bean.getTimeCreated().getTime()>=time.getTime());
    }


    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_qualification_data.xml")
    public void testUpdateNurseQualification() {
        List<NurseQualificationBean> qualifications = qualService.getAllNurseQualifications(1);
        boolean isOk = true;
        for (NurseQualificationBean bean : qualifications) {
            if (!VetStatus.COMPLETED.equals(bean.getStatus())) {
                isOk = false;
                break;
            }
        }
        Assert.assertFalse(isOk);

        for (NurseQualificationBean bean : qualifications) {
            logger.info(bean.toString());
            if (!VetStatus.COMPLETED.equals(bean.getStatus())) {
                qualService.updateWorkFile(bean.getId(), null, bean.getName(), null, null, VetStatus.COMPLETED, null, null);
            }
        }
        qualifications = qualService.getAllNurseQualifications(1);
        isOk = true;
        for (NurseQualificationBean bean : qualifications) {
            logger.info(bean.toString());
            if (!VetStatus.COMPLETED.equals(bean.getStatus())) {
                isOk = false;
                break;
            }
        }
        Assert.assertTrue(isOk);
    }


    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_qualification_data.xml")
    public void testDeleteNurseQualification() {
        List<NurseQualificationBean> qualifications = qualService.getAllNurseQualifications(1);
        for (NurseQualificationBean bean : qualifications) {
            logger.info(bean.toString());
            qualService.deleteNurseQualification(bean.getId());
        }
        qualifications = qualService.getAllNurseQualifications(1);
        Assert.assertTrue(qualifications.isEmpty());
    }
}
