package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.service.CourseService;
import com.cooltoo.go2nurse.service.file.TemporaryGo2NurseFileStorageService;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.HtmlParser;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hp on 2016/6/8.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml")
})
public class CourseServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceTest.class.getName());

    @Autowired private CourseService service;
    @Autowired private TemporaryGo2NurseFileStorageService tempStorage;
    @Autowired private UserGo2NurseFileStorageService officailStorage;


    private String statusAll = "ALL";
    private String statusDis = CourseStatus.DISABLE.name();
    private String statusEnb = CourseStatus.ENABLE.name();
    private String statusEdt = CourseStatus.EDITING.name();

    @Test
    public void testExistsCourse() {
        long courseId = 1L;
        boolean exists = service.existCourse(courseId);
        Assert.assertTrue(exists);

        courseId = 10L;
        exists = service.existCourse(courseId);
        Assert.assertFalse(exists);
    }

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

    @Test
    public void testGetCourseByStatus() {
        List<CourseBean> courses = service.getCourseByStatus(statusAll, 1, 4);
        Assert.assertEquals(4, courses.size());
        Assert.assertEquals(5L, courses.get(0).getId());
        Assert.assertEquals(4L, courses.get(1).getId());
        Assert.assertEquals(3L, courses.get(2).getId());
        Assert.assertEquals(2L, courses.get(3).getId());

        courses = service.getCourseByStatus(statusDis, 0, 4);
        Assert.assertEquals(3, courses.size());
        Assert.assertEquals(9L, courses.get(0).getId());
        Assert.assertEquals(8L, courses.get(1).getId());
        Assert.assertEquals(7L, courses.get(2).getId());

        courses = service.getCourseByStatus(statusEnb, 1, 4);
        Assert.assertEquals(1, courses.size());
        Assert.assertEquals(1L, courses.get(0).getId());


        courses = service.getCourseByStatus(statusEdt, 0, 4);
        Assert.assertEquals(1, courses.size());
        Assert.assertEquals(6L, courses.get(0).getId());
    }

    @Test
    public void testCountCourseByStatusAndIds() {
        List<Long> ids = Arrays.asList(new Long[]{4L, 5L, 6L, 7L});
        List<Long> courseIdFound = service.getCourseIdByStatusAndIds(CourseStatus.ENABLE.name(), ids);
        Assert.assertEquals(2, courseIdFound.size());
        Assert.assertEquals(5L, courseIdFound.get(0).longValue());
        Assert.assertEquals(4L, courseIdFound.get(1).longValue());

        courseIdFound = service.getCourseIdByStatusAndIds(CourseStatus.EDITING.name(), ids);
        Assert.assertEquals(1, courseIdFound.size());
        Assert.assertEquals(6L, courseIdFound.get(0).longValue());

        courseIdFound = service.getCourseIdByStatusAndIds(CourseStatus.DISABLE.name(), ids);
        Assert.assertEquals(1, courseIdFound.size());
        Assert.assertEquals(7L, courseIdFound.get(0).longValue());
    }

    @Test
    public void testGetCourseByStatusAndIds() {
        List<Long> ids = Arrays.asList(new Long[]{4L, 5L, 6L, 7L});
        List<CourseBean> beans = service.getCourseByStatusAndIds(CourseStatus.ENABLE.name(), ids);
        Assert.assertEquals(2, beans.size());
        Assert.assertEquals(5L, beans.get(0).getId());
        Assert.assertEquals(4L, beans.get(1).getId());

        beans = service.getCourseByStatusAndIds(CourseStatus.EDITING.name(), ids);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals(6L, beans.get(0).getId());

        beans = service.getCourseByStatusAndIds(CourseStatus.DISABLE.name(), ids);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals(7L, beans.get(0).getId());
    }

    @Test
    public void testCreateCourse() {
        String name = "title test";
        String introduction = "描述 0003";
        String link = "http://afdsafds.com/fdsafd9ejkfdsjakfdjisoafkjfi";
        int hospitalId = 11;
        CourseBean bean = service.createCourse(name, introduction, link, hospitalId);
        Assert.assertNotNull(bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(introduction, bean.getIntroduction());
        Assert.assertEquals(link, bean.getLink());
    }

    @Test
    public void testUpdateActivityBasicInfo() {
        long id  = 1L;
        String name = "title test";
        String introduction = "描述 0003";
        String imageName = "image.png";
        ByteArrayInputStream image = new ByteArrayInputStream(name.getBytes());
        String link = "http://afdsafds.com/fdsafd9ejkfdsjakfdjisoafkjfi";

        CourseBean bean1 = service.updateCourseBasicInfo(id, name, introduction, imageName, image, link);
        Assert.assertNotNull(bean1);
        Assert.assertEquals(id, bean1.getId());
        Assert.assertEquals(name, bean1.getName());
        Assert.assertEquals(introduction, bean1.getIntroduction());
        Assert.assertTrue(bean1.getFrontCover()>0);
        Assert.assertEquals(link, bean1.getLink());
        logger.info("Test case ====== image path --> {}", bean1.getFrontCoverUrl());
        Assert.assertTrue(officailStorage.fileExist(bean1.getFrontCoverUrl()));

        service.deleteByIds(""+bean1.getId());
        Assert.assertFalse(officailStorage.fileExist(bean1.getFrontCoverUrl()));

    }

    @Test
    public void testUpdateActivityStatus() {
        long activityId = 6;
        Throwable thr = null;

        // test editing activity update
        try { service.updateCourseStatus(activityId, statusDis); } catch (Exception ex) { thr = ex; }
        Assert.assertNotNull(thr); thr = null;
        try { service.updateCourseStatus(activityId, statusEdt); } catch (Exception ex) { thr = ex; }
        Assert.assertNotNull(thr); thr = null;
        try { service.updateCourseStatus(activityId, statusEnb); } catch (Exception ex) { thr = ex; }
        Assert.assertNotNull(thr); thr = null;

        // test set activity to editing status
        activityId = 1;
        try { service.updateCourseStatus(activityId, statusEdt); } catch (Exception ex) { thr = ex; }
        Assert.assertNotNull(thr); thr = null;

        // test activity is not exist
        activityId = 1000;
        try { service.updateCourseStatus(activityId, statusEdt); } catch (Exception ex) { thr = ex; }
        Assert.assertNotNull(thr); thr = null;

        // test normal status setting
        activityId = 1;
        CourseBean bean = service.updateCourseStatus(activityId, statusDis);
        Assert.assertEquals(CourseStatus.DISABLE, bean.getStatus());
        bean = service.updateCourseStatus(activityId, statusEnb);
        Assert.assertEquals(CourseStatus.ENABLE, bean.getStatus());
    }

    @Test
    public void testDeleteByIds() {
        String storagePath1 = officailStorage.getStoragePath();
        String storagePath2 = tempStorage.getStoragePath();
        long id1 = 1;
        long id2 = 2;
        long id3 = 6;
        List<Long> ids = Arrays.asList(new Long[]{id1, id2, id3});

        // make storage file
        makeFileForCourse(storagePath1, "11", new String[]{"111111", "222222", "333333"});
        makeFileForCourse(storagePath1, "22", new String[]{"444444", "555555"});
        makeFileForCourse(storagePath2, "66", new String[]{"121212", "131313"});
        String[] fileRelativePath = new String[]{"11/111111", "11/222222", "11/333333", "22/444444", "22/555555", "66/121212", "66/131313"};
        for (String filePath : fileRelativePath) {
            if (!filePath.startsWith("66")) {
                Assert.assertTrue(officailStorage.fileExist(filePath));
            }
            else {
                Assert.assertTrue(tempStorage.fileExist(filePath));
            }
        }

        List<CourseBean> beans = service.getCourseByIds(ids);
        Assert.assertEquals(3, beans.size());

        service.deleteByIds(ids);

        ids = Arrays.asList(new Long[]{id1});
        beans = service.getCourseByIds(ids);
        Assert.assertTrue(beans.isEmpty());

        ids = Arrays.asList(new Long[]{id2});
        beans = service.getCourseByIds(ids);
        Assert.assertTrue(beans.isEmpty());

        ids = Arrays.asList(new Long[]{id3});
        beans = service.getCourseByIds(ids);
        Assert.assertTrue(beans.isEmpty());

        for (String filePath : fileRelativePath) {
            if (!filePath.startsWith("66")) {
                Assert.assertFalse(officailStorage.fileExist(filePath));
            }
            else {
                Assert.assertFalse(tempStorage.fileExist(filePath));
            }
        }
    }




    //======================================================
    //    the content with image src attribute replace
    //======================================================
    @Test
    public void testGetActivityById() {
        String nginxBaseHttpUrl = "http://aaa.fdas.com/ddd/";
        CourseBean page0;
        String content;
        HtmlParser htmlParser;
        Map<String, String> imgTag2SrcUrl;
        Set<String> imgTags;
        // enabled
        page0 = service.getCourseById(1L, nginxBaseHttpUrl);
        Assert.assertNotNull(page0);
        Assert.assertEquals(1, page0.getId());
        content = page0.getContent();
        htmlParser = HtmlParser.newInstance();
        imgTag2SrcUrl = htmlParser.getImgTag2SrcUrlMap(content);
        imgTags = imgTag2SrcUrl.keySet();
        for (String tag : imgTags) {
            String  srcUrl   = imgTag2SrcUrl.get(tag);
            boolean nginxUrl = srcUrl.startsWith(nginxBaseHttpUrl+officailStorage.getNginxRelativePath());
            Assert.assertTrue(nginxUrl);
        }

        // editing
        page0 = service.getCourseById(6L, nginxBaseHttpUrl);
        Assert.assertNotNull(page0);
        Assert.assertEquals(6, page0.getId());
        content = page0.getContent();
        htmlParser = HtmlParser.newInstance();
        imgTag2SrcUrl = htmlParser.getImgTag2SrcUrlMap(content);
        imgTags = imgTag2SrcUrl.keySet();
        for (String tag : imgTags) {
            String  srcUrl   = imgTag2SrcUrl.get(tag);
            boolean nginxUrl = srcUrl.startsWith(nginxBaseHttpUrl+tempStorage.getNginxRelativePath());
            Assert.assertTrue(nginxUrl);
        }
    }

    @Test
    public void testGetActivityByName() {
        String name = "title 2";
        String nginxBaseHttpUrl = "http://aaa.fdas.com/ddd/";
        CourseBean page0;
        String content;
        HtmlParser htmlParser;
        Map<String, String> imgTag2SrcUrl;
        Set<String> imgTags;
        // enabled
        page0 = service.getCourseByName(name, nginxBaseHttpUrl);
        Assert.assertNotNull(page0);
        Assert.assertEquals(name, page0.getName());
        content = page0.getContent();
        htmlParser = HtmlParser.newInstance();
        imgTag2SrcUrl = htmlParser.getImgTag2SrcUrlMap(content);
        imgTags = imgTag2SrcUrl.keySet();
        for (String tag : imgTags) {
            String srcUrl = imgTag2SrcUrl.get(tag);
            boolean nginxUrl = srcUrl.startsWith(nginxBaseHttpUrl+officailStorage.getNginxRelativePath());
            Assert.assertTrue(nginxUrl);
        }

        // editing
        name = "title 6";
        page0 = service.getCourseByName(name, nginxBaseHttpUrl);
        Assert.assertNotNull(page0);
        Assert.assertEquals(name, page0.getName());
        content       = page0.getContent();
        htmlParser    = HtmlParser.newInstance();
        imgTag2SrcUrl = htmlParser.getImgTag2SrcUrlMap(content);
        imgTags       = imgTag2SrcUrl.keySet();
        for (String tag : imgTags) {
            String  srcUrl   = imgTag2SrcUrl.get(tag);
            boolean nginxUrl = srcUrl.startsWith(nginxBaseHttpUrl+tempStorage.getNginxRelativePath());
            Assert.assertTrue(nginxUrl);
        }
    }

    @Test
    public void testCreateTemporaryFile() {
        long contentId= 6;
        String imageName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.png";
        ByteArrayInputStream image = new ByteArrayInputStream(imageName.getBytes());
        String cachePath = null;

        cachePath = service.createTemporaryFile(contentId, imageName, image);
        boolean exist = tempStorage.fileExist(cachePath);
        Assert.assertTrue(exist);

        tempStorage.deleteFile(cachePath);
        exist = tempStorage.fileExist(cachePath);
        Assert.assertFalse(exist);
    }

    @Test
    public void testMoveActivity2Temporary() {
        String storagePath = officailStorage.getStoragePath();
        long courseId = 1;
        CourseBean course = null;

        // make storage file
        makeFileForCourse(storagePath, "11", new String[]{"111111", "222222", "333333"});
        String[] fileRelativePath = new String[]{"11/111111", "11/222222", "11/333333"};
        for (String filePath : fileRelativePath) {
            Assert.assertTrue(officailStorage.fileExist(filePath));
        }

        // move to temporary path
        service.moveCourse2Temporary(courseId);
        for (String filePath : fileRelativePath) {
            Assert.assertFalse(officailStorage.fileExist(filePath));
            Assert.assertTrue(tempStorage.fileExist(filePath));
        }

        course = service.getCourseById(courseId, "");
        Assert.assertNotNull(course);
        Assert.assertEquals(CourseStatus.EDITING, course.getStatus());


        tempStorage.deleteFileByPaths(Arrays.asList(fileRelativePath));
        for (String filePath : fileRelativePath) {
            Assert.assertFalse(tempStorage.fileExist(filePath));
        }
    }

    @Test
    public void testUpdateActivityContent() {
        String storagePath  = tempStorage.getStoragePath();
        String relativeFile = "11/BBBCCC";
        long courseId = 6;
        CourseBean course;

        // make storage file
        makeFileForCourse(storagePath, "11", new String[]{ "BBBCCC" });
        String content = "<html><head><title>test 04</title></head><body><img src='11/BBBCCC'></body></html>";
        Assert.assertTrue(tempStorage.fileExist(relativeFile));

        course = service.updateCourseContent(courseId, content);
        Assert.assertEquals(courseId, course.getId());
        Assert.assertEquals(CourseStatus.ENABLE, course.getStatus());
        course = service.getCourseById(courseId, "");
        Assert.assertEquals(content, course.getContent().replace('\\', '/'));

        Assert.assertFalse(tempStorage.fileExist(relativeFile));
        Assert.assertTrue(officailStorage.fileExist(relativeFile));
        officailStorage.deleteFile(relativeFile);
        Assert.assertFalse(officailStorage.fileExist(relativeFile));
    }

    private void makeFileForCourse(String basePath, String course_dir, String[] fileName) {
        course_dir   = basePath + File.separator + course_dir;
        File[] files   = new File[fileName.length];
        for (int i=0; i<files.length; i++) {
            files[i] = new File(course_dir + File.separator + fileName[i]);
        }

        File dir = new File(course_dir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        ByteArrayInputStream input = new ByteArrayInputStream(course_dir.getBytes());
        try {
            for (int i=0; i<files.length; i++) {
                FileUtil.writeFile(input, files[i]);
            }
        } catch (Exception ex) {}
    }
}
