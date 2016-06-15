package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.EmploymentInformationBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.EmploymentType;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.services.file.TemporaryFileStorageService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by hp on 2016/4/20.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/employment_information_data.xml")
})
public class EmploymentInformationServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(EmploymentInformationServiceTest.class.getName());

    @Autowired
    private EmploymentInformationService service;
    @Autowired
    private TemporaryFileStorageService tempStorage;
    @Autowired
    private OfficialFileStorageService officialStorage;


    private String statusAll = "ALL";
    private String statusEnb = CommonStatus.ENABLED.name();
    private String statusDis = CommonStatus.DISABLED.name();
    private String statusDel = CommonStatus.DELETED.name();

    private String employmentAll = "ALL";
    private String employmentFull = EmploymentType.FULL_TIME.name();
    private String employmentPart = EmploymentType.PART_TIME.name();


    @Test
    public void testCountEmploymentInfoByStatus() {
        long count = service.countEmploymentInfoByStatus(statusAll, employmentAll);
        Assert.assertEquals(9, count);
        count = service.countEmploymentInfoByStatus(statusDis, employmentAll);
        Assert.assertEquals(3, count);
        count = service.countEmploymentInfoByStatus(statusEnb, employmentAll);
        Assert.assertEquals(5, count);
        count = service.countEmploymentInfoByStatus(statusDel, employmentAll);
        Assert.assertEquals(1, count);

        count = service.countEmploymentInfoByStatus(statusAll, employmentAll);
        Assert.assertEquals(9, count);
        count = service.countEmploymentInfoByStatus(statusAll, employmentFull);
        Assert.assertEquals(6, count);
        count = service.countEmploymentInfoByStatus(statusAll, employmentPart);
        Assert.assertEquals(3, count);
    }

    @Test
    public void testGetEmploymentInfoByIds() {
        long id1 = 1;
        long id2 = 2;
        String ids = id1+","+id2;
        List<EmploymentInformationBean> beans = service.getEmploymentInfoByIds(ids);
        Assert.assertEquals(2, beans.size());
    }

    @Test
    public void testGetEmploymentInfoByStatus() {
        List<EmploymentInformationBean> page0 = service.getEmploymentInfoByStatus(statusAll, employmentAll, 0, 5);
        Assert.assertNotNull(page0);
        Assert.assertEquals(5, page0.size());
        page0 = service.getEmploymentInfoByStatus(statusAll, employmentAll, 1, 5);
        Assert.assertNotNull(page0);
        Assert.assertEquals(4, page0.size());

        page0 = service.getEmploymentInfoByStatus(statusEnb, employmentAll, 0, 3);
        Assert.assertNotNull(page0);
        Assert.assertEquals(3, page0.size());
        page0 = service.getEmploymentInfoByStatus(statusEnb, employmentAll, 1, 3);
        Assert.assertNotNull(page0);
        Assert.assertEquals(2, page0.size());

        page0 = service.getEmploymentInfoByStatus(statusDis, employmentAll, 0, 2);
        Assert.assertNotNull(page0);
        Assert.assertEquals(2, page0.size());
        page0 = service.getEmploymentInfoByStatus(statusDis, employmentAll, 1, 2);
        Assert.assertNotNull(page0);
        Assert.assertEquals(1, page0.size());

        page0 = service.getEmploymentInfoByStatus(statusDel, employmentAll, 0, 2);
        Assert.assertNotNull(page0);
        Assert.assertEquals(1, page0.size());


        page0 = service.getEmploymentInfoByStatus(statusAll, employmentAll);
        Assert.assertNotNull(page0);
        Assert.assertEquals(9, page0.size());

        page0 = service.getEmploymentInfoByStatus(statusDis, employmentAll);
        Assert.assertNotNull(page0);
        Assert.assertEquals(3, page0.size());

        page0 = service.getEmploymentInfoByStatus(statusEnb, employmentAll);
        Assert.assertNotNull(page0);
        Assert.assertEquals(5, page0.size());

        page0 = service.getEmploymentInfoByStatus(statusDel, employmentAll);
        Assert.assertNotNull(page0);
        Assert.assertEquals(1, page0.size());
    }

    @Test
    public void testCreateEmploymentInfo() {
        String title = "title test";
        String url = "http://afdsafds.com/fdsafd9ejkfdsjakfdjisoafkjfi";
        String partTime = EmploymentType.PART_TIME.name();
        EmploymentInformationBean bean = service.createEmploymentInfo(title, url, -1, partTime);
        Assert.assertNotNull(bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(title, bean.getTitle());
        Assert.assertEquals(url, bean.getUrl());
        Assert.assertEquals(partTime, bean.getType().name());
    }

    @Test
    public void testUpdateEmploymentInfoBasicInfo() {
        long   id  = 1L;
        String title = "title test";
        String url = "http://afdsafds.com/fdsafd9ejkfdsjakfdjisoafkjfi";
        int grade = 4;
        String status = CommonStatus.DELETED.name();
        String type = EmploymentType.PART_TIME.name();
        ByteArrayInputStream image = new ByteArrayInputStream(url.getBytes());

        EmploymentInformationBean bean1 = service.updateEmploymentInfo(id, title, url, grade, type, status, null, image);
        Assert.assertNotNull(bean1);
        Assert.assertEquals(id, bean1.getId());
        Assert.assertEquals(title, bean1.getTitle());
        Assert.assertTrue(bean1.getFrontCover()>0);
        Assert.assertEquals(url, bean1.getUrl());
        Assert.assertEquals(grade, bean1.getGrade());
        Assert.assertEquals(status, bean1.getStatus().name());
        Assert.assertEquals(type, bean1.getType().name());
        logger.info("Test case ====== image path --> {}", bean1.getFrontCoverUrl());
        Assert.assertTrue(officialStorage.fileExist(bean1.getFrontCoverUrl()));

        service.deleteByIds(""+bean1.getId());
        Assert.assertFalse(officialStorage.fileExist(bean1.getFrontCoverUrl()));

    }

    //======================================================
    //    the content with image src attribute replace
    //======================================================
    @Test
    public void testGetEmploymentInfoById() {
        String nginxBaseHttpUrl = "http://aaa.fdas.com/ddd/";
        EmploymentInformationBean page0;
        // enabled
        page0 = service.getEmploymentInfoById(1L);
        Assert.assertNotNull(page0);
        Assert.assertEquals(1, page0.getId());

        // editing
        page0 = service.getEmploymentInfoById(6L);
        Assert.assertNotNull(page0);
        Assert.assertEquals(6, page0.getId());
    }

    @Test
    public void testGetEmploymentInfoByName() {
        String title            = "title 2";
        EmploymentInformationBean page0;
        // enabled
        page0 = service.getEmploymentInfoByTitle(title);
        Assert.assertNotNull(page0);
        Assert.assertEquals(title, page0.getTitle());

        // editing
        title = "title 6";
        page0 = service.getEmploymentInfoByTitle(title);
        Assert.assertNotNull(page0);
        Assert.assertEquals(title, page0.getTitle());
    }
}
