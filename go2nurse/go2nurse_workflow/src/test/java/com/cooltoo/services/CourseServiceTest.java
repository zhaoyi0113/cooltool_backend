package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.ActivityStatus;
import com.cooltoo.go2nurse.service.CourseService;
import com.cooltoo.go2nurse.service.file.TemporaryGo2NurseFileStorageService;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hp on 2016/6/8.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml")
})
public class CourseServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceTest.class.getName());

    @Autowired private CourseService service;
    @Autowired private TemporaryGo2NurseFileStorageService tempStorage;
    @Autowired private UserGo2NurseFileStorageService officailStorage;


    private String statusAll = "ALL";
    private String statusDis = ActivityStatus.DISABLE.name();
    private String statusEnb = ActivityStatus.ENABLE.name();
    private String statusEdt = ActivityStatus.EDITING.name();

    @Test
    public void testCountByStatus() {
        long count = service.countByStatus(statusAll);
        Assert.assertEquals(9, count);
        count = service.countByStatus(statusDis);
        Assert.assertEquals(3, count);
        count = service.countByStatus(statusEnb);
        Assert.assertEquals(5, count);
        count = service.countByStatus(statusEdt);
        Assert.assertEquals(1, count);
    }
//
//    @Test
//    public void testGetActivityByIds() {
//        int id1 = 1;
//        int id2 = 2;
//        List<Integer> ids = id1+","+id2;
//        List<ActivityBean> beans = service.getCourseByStatusAndIds(ActivityStatus.ENABLE.name(), ids);
//        Assert.assertEquals(2, beans.size());
//    }
//
//    @Test
//    public void testGetActivityByStatus() {
//        List<ActivityBean> page0 = service.getActivityByStatus(statusAll, 0, 5);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(5, page0.size());
//        page0 = service.getActivityByStatus(statusAll, 1, 5);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(4, page0.size());
//
//        page0 = service.getActivityByStatus(statusEnb, 0, 3);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(3, page0.size());
//        page0 = service.getActivityByStatus(statusEnb, 1, 3);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(2, page0.size());
//
//        page0 = service.getActivityByStatus(statusDis, 0, 2);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(2, page0.size());
//        page0 = service.getActivityByStatus(statusDis, 1, 2);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(1, page0.size());
//
//        page0 = service.getActivityByStatus(statusEdt, 0, 2);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(1, page0.size());
//
//
//        page0 = service.getActivityByStatus(statusAll);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(9, page0.size());
//
//        page0 = service.getActivityByStatus(statusDis);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(3, page0.size());
//
//        page0 = service.getActivityByStatus(statusEnb);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(5, page0.size());
//
//        page0 = service.getActivityByStatus(statusEdt);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(1, page0.size());
//    }
//
//    @Test
//    public void testCreateActivity() {
//        String title = "title test";
//        String subtitle = "子标题 001";
//        String descript = "描述 0003";
//        String time     = "2014-12-22 00:00:00";
//        String place    = "fdafaf";
//        String price    = "323.324";
//        BigDecimal bdPrice = new BigDecimal("323.32");
//        String enrollUrl = "http://afdsafds.com/fdsafd9ejkfdsjakfdjisoafkjfi";
//        ActivityBean bean = service.createActivity(title, subtitle, descript, time, place, price, enrollUrl, -1);
//        Assert.assertNotNull(bean);
//        Assert.assertTrue(bean.getId()>0);
//        Assert.assertEquals(title, bean.getTitle());
//        Assert.assertEquals(subtitle, bean.getSubtitle());
//        Assert.assertEquals(descript, bean.getDescription());
//        Assert.assertEquals(new Date(NumberUtil.getTime(time, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS)), bean.getTime());
//        Assert.assertEquals(place, bean.getPlace());
//        Assert.assertEquals(bdPrice, bean.getPrice());
//        Assert.assertEquals(enrollUrl, bean.getEnrollUrl());
//    }
//
//    @Test
//    public void testUpdateActivityBasicInfo() {
//        long   id  = 1L;
//        String title = "title test";
//        String subtitle = "子标题 001";
//        String descript = "描述 0003";
//        String time     = "2014-12-22 00:00:00";
//        String place    = "fdafaf";
//        String price    = "323.324";
//        BigDecimal bdPrice = new BigDecimal("323.32");
//        ByteArrayInputStream image = new ByteArrayInputStream(title.getBytes());
//        String enrollUrl = "http://afdsafds.com/fdsafd9ejkfdsjakfdjisoafkjfi";
//
//        ActivityBean bean1 = service.updateActivityBasicInfo(id, title, subtitle, descript, time, place, price, null, image, enrollUrl, -1);
//        Assert.assertNotNull(bean1);
//        Assert.assertEquals(id, bean1.getId());
//        Assert.assertEquals(title, bean1.getTitle());
//        Assert.assertEquals(subtitle, bean1.getSubtitle());
//        Assert.assertEquals(descript, bean1.getDescription());
//        Assert.assertEquals(new Date(NumberUtil.getTime(time, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS)), bean1.getTime());
//        Assert.assertEquals(place, bean1.getPlace());
//        Assert.assertEquals(bdPrice, bean1.getPrice());
//        Assert.assertTrue(bean1.getFrontCover()>0);
//        Assert.assertEquals(enrollUrl, bean1.getEnrollUrl());
//        logger.info("Test case ====== image path --> {}", bean1.getFrontCoverUrl());
//        Assert.assertTrue(officailStorage.fileExist(bean1.getFrontCoverUrl()));
//
//        service.deleteByIds(""+bean1.getId());
//        Assert.assertFalse(officailStorage.fileExist(bean1.getFrontCoverUrl()));
//
//    }
//
//    @Test
//    public void testUpdateActivityStatus() {
//        long activityId = 6;
//        Throwable thr = null;
//
//        // test editing activity update
//        try { service.updateActivityStatus(activityId, statusDis); } catch (Exception ex) { thr = ex; }
//        Assert.assertNotNull(thr); thr = null;
//        try { service.updateActivityStatus(activityId, statusEdt); } catch (Exception ex) { thr = ex; }
//        Assert.assertNotNull(thr); thr = null;
//        try { service.updateActivityStatus(activityId, statusEnb); } catch (Exception ex) { thr = ex; }
//        Assert.assertNotNull(thr); thr = null;
//
//        // test set activity to editing status
//        activityId = 1;
//        try { service.updateActivityStatus(activityId, statusEdt); } catch (Exception ex) { thr = ex; }
//        Assert.assertNotNull(thr); thr = null;
//
//        // test activity is not exist
//        activityId = 1000;
//        try { service.updateActivityStatus(activityId, statusEdt); } catch (Exception ex) { thr = ex; }
//        Assert.assertNotNull(thr); thr = null;
//
//        // test normal status setting
//        activityId = 1;
//        ActivityBean bean = service.updateActivityStatus(activityId, statusDis);
//        Assert.assertEquals(ActivityStatus.DISABLE, bean.getStatus());
//        bean = service.updateActivityStatus(activityId, statusEnb);
//        Assert.assertEquals(ActivityStatus.ENABLE, bean.getStatus());
//    }
//
//    @Test
//    public void testUpdateActivityGrade() {
//        long activityId;
//
//        List<ActivityBean> activities = service.getActivityByStatus(ActivityStatus.ENABLE.name());
//        Assert.assertEquals(5, activities.get(0).getId());
//        Assert.assertEquals(4, activities.get(1).getId());
//
//        activityId = 4;
//        service.updateActivityBasicInfo(activityId, null, null, null, null, null, null, null, null, null, 1);
//        activities = service.getActivityByStatus(ActivityStatus.ENABLE.name());
//        Assert.assertEquals(4, activities.get(0).getId());
//        Assert.assertEquals(5, activities.get(1).getId());
//
//        activityId = 3;
//        service.updateActivityBasicInfo(activityId, null, null, null, null, null, null, null, null, null, 2);
//        activities = service.getActivityByStatus(ActivityStatus.ENABLE.name());
//        Assert.assertEquals(3, activities.get(0).getId());
//        Assert.assertEquals(4, activities.get(1).getId());
//    }
//
//    @Test
//    public void testDeleteByIds() {
//        String storagePath1 = officailStorage.getStoragePath();
//        String storagePath2 = tempStorage.getStoragePath();
//        long id1 = 1;
//        long id2 = 2;
//        long id3 = 6;
//        String ids = id1+","+id2+","+id3;
//
//        // make storage file
//        makeFileForActivity(storagePath1, "11", new String[]{"111111", "222222", "333333"});
//        makeFileForActivity(storagePath1, "22", new String[]{"444444", "555555"});
//        makeFileForActivity(storagePath2, "66", new String[]{"121212", "131313"});
//        String[] fileRelativePath = new String[]{"11/111111", "11/222222", "11/333333", "22/444444", "22/555555", "66/121212", "66/131313"};
//        for (String filePath : fileRelativePath) {
//            if (!filePath.startsWith("66")) {
//                Assert.assertTrue(officailStorage.fileExist(filePath));
//            }
//            else {
//                Assert.assertTrue(tempStorage.fileExist(filePath));
//            }
//        }
//
//        List<ActivityBean> beans = service.getActivityByIds(ids);
//        Assert.assertEquals(3, beans.size());
//
//        service.deleteByIds(ids);
//
//        beans = service.getActivityByIds(id1+"");
//        Assert.assertTrue(beans.isEmpty());
//
//        beans = service.getActivityByIds(id2+"");
//        Assert.assertTrue(beans.isEmpty());
//
//        beans = service.getActivityByIds(id3+"");
//        Assert.assertTrue(beans.isEmpty());
//
//        for (String filePath : fileRelativePath) {
//            if (!filePath.startsWith("66")) {
//                Assert.assertFalse(officailStorage.fileExist(filePath));
//            }
//            else {
//                Assert.assertFalse(tempStorage.fileExist(filePath));
//            }
//        }
//    }
//
//
//
//
//    //======================================================
//    //    the content with image src attribute replace
//    //======================================================
//    @Test
//    public void testGetActivityById() {
//        String nginxBaseHttpUrl = "http://aaa.fdas.com/ddd/";
//        ActivityBean page0;
//        String              content;
//        HtmlParser          htmlParser;
//        Map<String, String> imgTag2SrcUrl;
//        Set<String>         imgTags;
//        // enabled
//        page0 = service.getActivityById(1L, nginxBaseHttpUrl);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(1, page0.getId());
//        content       = page0.getContent();
//        htmlParser    = HtmlParser.newInstance();
//        imgTag2SrcUrl = htmlParser.getImgTag2SrcUrlMap(content);
//        imgTags       = imgTag2SrcUrl.keySet();
//        for (String tag : imgTags) {
//            String  srcUrl   = imgTag2SrcUrl.get(tag);
//            boolean nginxUrl = srcUrl.startsWith(nginxBaseHttpUrl+officailStorage.getNginxRelativePath());
//            Assert.assertTrue(nginxUrl);
//        }
//
//        // editing
//        page0 = service.getActivityById(6L, nginxBaseHttpUrl);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(6, page0.getId());
//        content       = page0.getContent();
//        htmlParser    = HtmlParser.newInstance();
//        imgTag2SrcUrl = htmlParser.getImgTag2SrcUrlMap(content);
//        imgTags       = imgTag2SrcUrl.keySet();
//        for (String tag : imgTags) {
//            String  srcUrl   = imgTag2SrcUrl.get(tag);
//            boolean nginxUrl = srcUrl.startsWith(nginxBaseHttpUrl+tempStorage.getNginxRelativePath());
//            Assert.assertTrue(nginxUrl);
//        }
//    }
//
//    @Test
//    public void testGetActivityByName() {
//        String title            = "title 2";
//        String nginxBaseHttpUrl = "http://aaa.fdas.com/ddd/";
//        ActivityBean page0;
//        String              content;
//        HtmlParser          htmlParser;
//        Map<String, String> imgTag2SrcUrl;
//        Set<String>         imgTags;
//        // enabled
//        page0 = service.getActivityByTitle(title, nginxBaseHttpUrl);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(title, page0.getTitle());
//        content       = page0.getContent();
//        htmlParser    = HtmlParser.newInstance();
//        imgTag2SrcUrl = htmlParser.getImgTag2SrcUrlMap(content);
//        imgTags       = imgTag2SrcUrl.keySet();
//        for (String tag : imgTags) {
//            String  srcUrl   = imgTag2SrcUrl.get(tag);
//            boolean nginxUrl = srcUrl.startsWith(nginxBaseHttpUrl+officailStorage.getNginxRelativePath());
//            Assert.assertTrue(nginxUrl);
//        }
//
//        // editing
//        title = "title 6";
//        page0 = service.getActivityByTitle(title, nginxBaseHttpUrl);
//        Assert.assertNotNull(page0);
//        Assert.assertEquals(title, page0.getTitle());
//        content       = page0.getContent();
//        htmlParser    = HtmlParser.newInstance();
//        imgTag2SrcUrl = htmlParser.getImgTag2SrcUrlMap(content);
//        imgTags       = imgTag2SrcUrl.keySet();
//        for (String tag : imgTags) {
//            String  srcUrl   = imgTag2SrcUrl.get(tag);
//            boolean nginxUrl = srcUrl.startsWith(nginxBaseHttpUrl+tempStorage.getNginxRelativePath());
//            Assert.assertTrue(nginxUrl);
//        }
//    }
//
//    @Test
//    public void testCreateTemporaryFile() {
//        long                 activityId= 6;
//        String               imageName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.png";
//        ByteArrayInputStream image     = new ByteArrayInputStream(imageName.getBytes());
//        String               cachePath = null;
//
//        cachePath = service.createTemporaryFile(activityId, imageName, image);
//        boolean exist = tempStorage.fileExist(cachePath);
//        Assert.assertTrue(exist);
//
//        tempStorage.deleteFile(cachePath);
//        exist = tempStorage.fileExist(cachePath);
//        Assert.assertFalse(exist);
//    }
//
//    @Test
//    public void testMoveActivity2Temporary() {
//        String               storagePath = officailStorage.getStoragePath();
//        long                 activityId  = 1;
//        ActivityBean         activity    = null;
//
//        // make storage file
//        makeFileForActivity(storagePath, "11", new String[]{"111111", "222222", "333333"});
//        String[] fileRelativePath = new String[]{"11/111111", "11/222222", "11/333333"};
//        for (String filePath : fileRelativePath) {
//            Assert.assertTrue(officailStorage.fileExist(filePath));
//        }
//
//        // move to temporary path
//        service.moveActivity2Temporary(activityId);
//        for (String filePath : fileRelativePath) {
//            Assert.assertFalse(officailStorage.fileExist(filePath));
//            Assert.assertTrue(tempStorage.fileExist(filePath));
//        }
//
//        activity = service.getActivityById(activityId, "");
//        Assert.assertNotNull(activity);
//        Assert.assertEquals(ActivityStatus.EDITING, activity.getStatus());
//
//
//        tempStorage.deleteFileByPaths(Arrays.asList(fileRelativePath));
//        for (String filePath : fileRelativePath) {
//            Assert.assertFalse(tempStorage.fileExist(filePath));
//        }
//    }
//
//    @Test
//    public void testUpdateActivityContent() {
//        String               storagePath  = tempStorage.getStoragePath();
//        String               relativeFile = "11/BBBCCC";
//        long                 activityId   = 6;
//        ActivityBean         activity;
//
//        // make storage file
//        makeFileForActivity(storagePath, "11", new String[]{ "BBBCCC" });
//        String content = "<html><head><title>test 04</title></head><body><img src='11/BBBCCC'></body></html>";
//        Assert.assertTrue(tempStorage.fileExist(relativeFile));
//
//        activity = service.updateActivityContent(activityId, content);
//        Assert.assertEquals(activityId, activity.getId());
//        Assert.assertEquals(ActivityStatus.ENABLE, activity.getStatus());
//        activity = service.getActivityById(activityId, "");
//        Assert.assertEquals(content, activity.getContent().replace('\\', '/'));
//
//        Assert.assertFalse(tempStorage.fileExist(relativeFile));
//        Assert.assertTrue(officailStorage.fileExist(relativeFile));
//        officailStorage.deleteFile(relativeFile);
//        Assert.assertFalse(officailStorage.fileExist(relativeFile));
//    }
//
//    private void makeFileForActivity(String basePath, String activity_dir, String[] fileName) {
//        activity_dir   = basePath + File.separator + activity_dir;
//        File[] files   = new File[fileName.length];
//        for (int i=0; i<files.length; i++) {
//            files[i] = new File(activity_dir + File.separator + fileName[i]);
//        }
//
//        File dir = new File(activity_dir);
//        if (!dir.exists()) {
//            dir.mkdir();
//        }
//
//        ByteArrayInputStream input = new ByteArrayInputStream(activity_dir.getBytes());
//        try {
//            for (int i=0; i<files.length; i++) {
//                FileUtil.writeFile(input, files[i]);
//            }
//        } catch (Exception ex) {}
//    }
}
