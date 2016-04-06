package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.beans.WorkFileTypeBean;
import com.cooltoo.backend.services.NurseQualificationService;
import com.cooltoo.constants.VetStatus;
import com.cooltoo.constants.WorkFileType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaolisong on 16/3/23.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/work_file_type_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_qualification_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_qualification_file_data.xml")
})
public class NurseQualificationServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(NurseQualificationServiceTest.class.getName());

    @Autowired
    private NurseQualificationService qualService;

    @Test
    public void testNurseQualificationOK() {
        boolean isOk;
        long nurseId = 3;
        isOk = true;
        List<NurseQualificationBean> qualifications = qualService.getAllNurseQualifications(nurseId);
        for (NurseQualificationBean bean : qualifications) {
            if (!VetStatus.COMPLETED.equals(bean.getStatus())) {
                isOk = false;
                break;
            }
        }
        Assert.assertTrue(isOk);

        nurseId = 2;
        isOk = true;
        qualifications = qualService.getAllNurseQualifications(nurseId);
        for (NurseQualificationBean bean : qualifications) {
            if (!VetStatus.COMPLETED.equals(bean.getStatus())) {
                isOk = false;
                break;
            }
        }
        Assert.assertFalse(isOk);

        nurseId = 1;
        isOk = true;
        qualifications = qualService.getAllNurseQualifications(nurseId);
        for (NurseQualificationBean bean : qualifications) {
            if (!VetStatus.COMPLETED.equals(bean.getStatus())) {
                isOk = false;
                break;
            }
        }
        Assert.assertFalse(isOk);
    }

    @Test
    public void testAddNurseQualification() {
        String name = "Identi";
        String content = "dsafdasfdasfdasflksajfdkla;jfdksaljfdksla";
        ByteArrayInputStream byteInput = new ByteArrayInputStream(content.getBytes());
        Date time = new Date();

        WorkFileTypeBean workFileType = qualService.getWorkFileTypeBean(WorkFileType.EMPLOYEES_CARD.name());

        NurseQualificationBean bean = null;
        qualService.addWorkFile(4, name, WorkFileType.EMPLOYEES_CARD.name(), "aaa.png", byteInput);
        bean = qualService.getAllNurseQualifications(4).get(0);
        logger.info("add work file : " + bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(4, bean.getUserId());
        Assert.assertEquals(name, bean.getName());

        workFileType = qualService.getWorkFileTypeBean(WorkFileType.IDENTIFICATION.name());
        name = "Identi2";
        qualService.addWorkFile(4, name, workFileType.getName(), "aaa.png", byteInput);
        bean = qualService.getAllNurseQualifications(4).get(0);
        logger.info("add identification file : " + bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(4, bean.getUserId());
        Assert.assertNotEquals(name, bean.getName());
    }


    @Test
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
                qualService.updateQualification(bean.getId(), bean.getName(), VetStatus.COMPLETED, null);
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
    public void testDeleteNurseQualification() {
        List<NurseQualificationBean> qualifications = qualService.getAllNurseQualifications(1);
        for (NurseQualificationBean bean : qualifications) {
            logger.info(bean.toString());
            qualService.deleteNurseQualification(bean.getId());
        }
        Throwable ex = null;
        try {
            qualifications = qualService.getAllNurseQualifications(1);
        }
        catch (Exception e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
    }
}
