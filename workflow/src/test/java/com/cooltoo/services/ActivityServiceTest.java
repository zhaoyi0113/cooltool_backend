package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.ActivityBean;
import com.cooltoo.constants.ActivityStatus;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.HtmlParser;
import com.cooltoo.util.NumberUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Exchanger;

/**
 * Created by hp on 2016/4/20.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/platform_activities_data.xml")
})
public class ActivityServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceTest.class.getName());

    @Autowired
    private ActivityService service;
    @Autowired
    private TemporaryFileStorageService tempStorageService;
    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;


    private String statusAll = "ALL";
    private String statusDis = ActivityStatus.DISABLE.name();
    private String statusEnb = ActivityStatus.ENABLE.name();
    private String statusEdt = ActivityStatus.EDITING.name();

    @Test
    public void testCountActivityByStatus() {
        long count = service.countActivityByStatus(statusAll);
        Assert.assertEquals(9, count);
        count = service.countActivityByStatus(statusDis);
        Assert.assertEquals(3, count);
        count = service.countActivityByStatus(statusEnb);
        Assert.assertEquals(5, count);
        count = service.countActivityByStatus(statusEdt);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testGetActivityByIds() {
        long id1 = 1;
        long id2 = 2;
        String ids = id1+","+id2;
        List<ActivityBean> beans = service.getActivityByIds(ids);
        Assert.assertEquals(2, beans.size());
    }

    @Test
    public void testGetActivityByStatus() {
        List<ActivityBean> page0 = service.getActivityByStatus(statusAll, 0, 5);
        Assert.assertNotNull(page0);
        Assert.assertEquals(5, page0.size());
        page0 = service.getActivityByStatus(statusAll, 1, 5);
        Assert.assertNotNull(page0);
        Assert.assertEquals(4, page0.size());

        page0 = service.getActivityByStatus(statusEnb, 0, 3);
        Assert.assertNotNull(page0);
        Assert.assertEquals(3, page0.size());
        page0 = service.getActivityByStatus(statusEnb, 1, 3);
        Assert.assertNotNull(page0);
        Assert.assertEquals(2, page0.size());

        page0 = service.getActivityByStatus(statusDis, 0, 2);
        Assert.assertNotNull(page0);
        Assert.assertEquals(2, page0.size());
        page0 = service.getActivityByStatus(statusDis, 1, 2);
        Assert.assertNotNull(page0);
        Assert.assertEquals(1, page0.size());

        page0 = service.getActivityByStatus(statusEdt, 0, 2);
        Assert.assertNotNull(page0);
        Assert.assertEquals(1, page0.size());


        page0 = service.getActivityByStatus(statusAll);
        Assert.assertNotNull(page0);
        Assert.assertEquals(9, page0.size());

        page0 = service.getActivityByStatus(statusDis);
        Assert.assertNotNull(page0);
        Assert.assertEquals(3, page0.size());

        page0 = service.getActivityByStatus(statusEnb);
        Assert.assertNotNull(page0);
        Assert.assertEquals(5, page0.size());

        page0 = service.getActivityByStatus(statusEdt);
        Assert.assertNotNull(page0);
        Assert.assertEquals(1, page0.size());
    }

    @Test
    public void testCreateActivity() {
        String title = "title test";
        String subtitle = "子标题 001";
        String descript = "描述 0003";
        String time     = "2014-12-22 00:00:00";
        String place    = "fdafaf";
        String price    = "323.324";
        BigDecimal bdPrice = new BigDecimal("323.32");
        ActivityBean bean = service.createActivity(title, subtitle, descript, time, place, price);
        Assert.assertNotNull(bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(title, bean.getTitle());
        Assert.assertEquals(subtitle, bean.getSubtitle());
        Assert.assertEquals(descript, bean.getDescription());
        Assert.assertEquals(new Date(NumberUtil.getTime(time, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS)), bean.getTime());
        Assert.assertEquals(place, bean.getPlace());
        Assert.assertEquals(bdPrice, bean.getPrice());
    }

    @Test
    public void testUpdateActivityBasicInfo() {
        long   id  = 1L;
        String title = "title test";
        String subtitle = "子标题 001";
        String descript = "描述 0003";
        String time     = "2014-12-22 00:00:00";
        String place    = "fdafaf";
        String price    = "323.324";
        BigDecimal bdPrice = new BigDecimal("323.32");
        ByteArrayInputStream image = new ByteArrayInputStream(title.getBytes());

        ActivityBean bean1 = service.updateActivityBasicInfo(id, title, subtitle, descript, time, place, price, null, image);
        Assert.assertNotNull(bean1);
        Assert.assertEquals(id, bean1.getId());
        Assert.assertEquals(title, bean1.getTitle());
        Assert.assertEquals(subtitle, bean1.getSubtitle());
        Assert.assertEquals(descript, bean1.getDescription());
        Assert.assertEquals(new Date(NumberUtil.getTime(time, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS)), bean1.getTime());
        Assert.assertEquals(place, bean1.getPlace());
        Assert.assertEquals(bdPrice, bean1.getPrice());
        Assert.assertTrue(bean1.getFrontCover()>0);
        logger.info("Test case ====== image path --> {}", bean1.getFrontCoverUrl());
    }

    @Test
    public void testUpdateActivityStatus() {
        long activityId = 6;
        Throwable thr = null;

        // test editing activity update
        try { service.updateActivityStatus(activityId, statusDis); } catch (Exception ex) { thr = ex; }
        Assert.assertNotNull(thr); thr = null;
        try { service.updateActivityStatus(activityId, statusEdt); } catch (Exception ex) { thr = ex; }
        Assert.assertNotNull(thr); thr = null;
        try { service.updateActivityStatus(activityId, statusEnb); } catch (Exception ex) { thr = ex; }
        Assert.assertNotNull(thr); thr = null;

        // test set activity to editing status
        activityId = 1;
        try { service.updateActivityStatus(activityId, statusEdt); } catch (Exception ex) { thr = ex; }
        Assert.assertNotNull(thr); thr = null;

        // test activity is not exist
        activityId = 1000;
        try { service.updateActivityStatus(activityId, statusEdt); } catch (Exception ex) { thr = ex; }
        Assert.assertNotNull(thr); thr = null;

        // test normal status setting
        activityId = 1;
        ActivityBean bean = service.updateActivityStatus(activityId, statusDis);
        Assert.assertEquals(ActivityStatus.DISABLE, bean.getStatus());
        bean = service.updateActivityStatus(activityId, statusEnb);
        Assert.assertEquals(ActivityStatus.ENABLE, bean.getStatus());
    }

    @Test
    public void testDeleteByIds() {
        long id1 = 1;
        long id2 = 2;
        String ids = id1+","+id2;

        List<ActivityBean> beans = service.getActivityByIds(ids);
        Assert.assertEquals(2, beans.size());

        service.deleteByIds(ids);

        beans = service.getActivityByIds(id1+"");
        Assert.assertTrue(beans.isEmpty());

        beans = service.getActivityByIds(id2+"");
        Assert.assertTrue(beans.isEmpty());
    }




    //======================================================
    //    the content with image src attribute replace
    //======================================================
    @Test
    public void testGetActivityById() {
        String nginxBaseHttpUrl = "http://aaa.fdas.com/ddd";
        ActivityBean page0 = service.getActivityById(1L, nginxBaseHttpUrl);
        Assert.assertNotNull(page0);
        Assert.assertEquals(1, page0.getId());

        String              content       = page0.getContent();
        HtmlParser          htmlParser    = HtmlParser.newInstance();
        Map<String, String> imgTag2SrcUrl = htmlParser.getImgTag2SrcUrlMap(content);

        Set<String>         imgTags       = imgTag2SrcUrl.keySet();
        for (String tag : imgTags) {
            String  srcUrl   = imgTag2SrcUrl.get(tag);
            boolean nginxUrl = srcUrl.startsWith(nginxBaseHttpUrl);
            Assert.assertTrue(nginxUrl);
        }
    }

    @Test
    public void testGetActivityByName() {
        String title            = "title 2";
        String nginxBaseHttpUrl = "http://aaa.fdas.com/ddd";
        ActivityBean page0 = service.getActivityByTitle(title, nginxBaseHttpUrl);
        Assert.assertNotNull(page0);
        Assert.assertEquals(title, page0.getTitle());

        String              content       = page0.getContent();
        HtmlParser          htmlParser    = HtmlParser.newInstance();
        Map<String, String> imgTag2SrcUrl = htmlParser.getImgTag2SrcUrlMap(content);

        Set<String>         imgTags       = imgTag2SrcUrl.keySet();
        for (String tag : imgTags) {
            String  srcUrl   = imgTag2SrcUrl.get(tag);
            boolean nginxUrl = srcUrl.startsWith(nginxBaseHttpUrl);
            Assert.assertTrue(nginxUrl);
        }
    }

    @Test
    public void testCreateTemporaryFile() {
        String               token     = "1234567890";
        String               imageName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.png";
        ByteArrayInputStream image     = new ByteArrayInputStream(imageName.getBytes());
        String               cachePath = null;

        cachePath = service.createTemporaryFile(token, imageName, image);
        boolean exist = tempStorageService.existTmpFile(cachePath);
        Assert.assertTrue(exist);

        tempStorageService.cleanTokenCachedFile(token);
    }

    @Test
    public void testMoveActivity2Temporary() {
        String               storagePath = storageService.getStoragePath();
        String               token       = "1234567890";
        long                 activityId  = 1;
        ActivityBean         activity    = null;

        // make storage file
        makeFileForActivity(storagePath, "11", new String[]{"111111", "222222", "333333"});

        activity = service.moveActivity2Temporary(token, activityId);
        Assert.assertNotNull(activity);
        Assert.assertEquals(ActivityStatus.EDITING, activity.getStatus());

        tempStorageService.cleanTokenCachedFile(token);
    }

    @Test
    public void test() {
        String               storagePath  = storageService.getStoragePath();
        String               relativeFile = null;
        String               token        = "1234567890";
        long                 activityId   = 6;
        ActivityBean         activity     = null;

        relativeFile = tempStorageService.cacheTemporaryFile(token, token, new ByteArrayInputStream(token.getBytes()));
        String content = "<html><head><title>test 04</title></head><body><img src='"+relativeFile+"'></body></html>";

        activity = service.updateActivityContent(token, activityId, content);
        Assert.assertEquals(activityId, activity.getId());
        Assert.assertEquals(ActivityStatus.DISABLE, activity.getStatus());
        activity = service.getActivityById(activityId, "");
        Assert.assertEquals(content, activity.getContent());

        List<String> delFile = new ArrayList<>();
        delFile.add(relativeFile);
        tempStorageService.deleteStorageFile(delFile);
    }

    private void makeFileForActivity(String basePath, String activity_dir, String[] fileName) {
        String storagePath    = storageService.getStoragePath();

        activity_dir   = storagePath + File.separator + activity_dir;
        File[] files   = new File[fileName.length];
        for (int i=0; i<files.length; i++) {
            files[i] = new File(activity_dir + File.separator + fileName[i]);
        }

        File dir = new File(activity_dir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        ByteArrayInputStream input = new ByteArrayInputStream(activity_dir.getBytes());
        try {
            for (int i=0; i<files.length; i++) {
                FileUtil.writeFile(input, files[i]);
            }
        } catch (Exception ex) {}
    }
}
