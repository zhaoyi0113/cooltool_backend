package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CourseCategoryBean;
import com.cooltoo.go2nurse.beans.CourseCategoryRelationBean;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.service.CourseCategoryRelationService;
import com.cooltoo.go2nurse.service.CourseCategoryService;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhaolisong on 16/4/8.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_category_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_category_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/course_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml")
})
public class CourseCategoryServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(CourseCategoryServiceTest.class.getName());

    @Autowired private CourseCategoryService service;
    @Autowired private CourseCategoryRelationService relationService;
    @Autowired private UserGo2NurseFileStorageService userStorage;

    private static final String All = "ALL";
    private static final String CategoryDisabled = CommonStatus.DISABLED.name();
    private static final String CategoryEnabled = CommonStatus.ENABLED.name();
    private static final String CategoryDeleted = CommonStatus.DELETED.name();
    private static final String CourseDisable = CourseStatus.DISABLE.name();
    private static final String CourseEnable = CourseStatus.ENABLE.name();
    private static final String CourseEditing = CourseStatus.EDITING.name();

    //==========================================
    //   course category relation getter
    //==========================================
    @Test
    public void testGetCategoryByCourseId() {
        long courseId = 1L;
        List<CourseCategoryBean> categories = relationService.getCategoryByCourseId(All, courseId);
        Assert.assertEquals(2, categories.size());
        Assert.assertEquals(1L, categories.get(0).getId());
        Assert.assertEquals(2L, categories.get(1).getId());

        courseId = 2L;
        categories = relationService.getCategoryByCourseId(All, courseId);
        Assert.assertEquals(4, categories.size());
        Assert.assertEquals(1L, categories.get(0).getId());
        Assert.assertEquals(2L, categories.get(1).getId());
        Assert.assertEquals(6L, categories.get(2).getId());
        Assert.assertEquals(8L, categories.get(3).getId());
        categories = relationService.getCategoryByCourseId(CategoryEnabled, courseId);
        Assert.assertEquals(2, categories.size());
        Assert.assertEquals(1L, categories.get(0).getId());
        Assert.assertEquals(2L, categories.get(1).getId());
        categories = relationService.getCategoryByCourseId(CategoryDisabled, courseId);
        Assert.assertEquals(1, categories.size());
        Assert.assertEquals(6L, categories.get(0).getId());
        categories = relationService.getCategoryByCourseId(CategoryDeleted, courseId);
        Assert.assertEquals(1, categories.size());
        Assert.assertEquals(8L, categories.get(0).getId());

    }

    @Test
    public void testGetCategoryByCourseIds() {
        List<Long> courseIds = Arrays.asList(new Long[]{1L, 2L});
        List<CourseCategoryBean> categories = relationService.getCategoryByCourseId(All, courseIds);
        Assert.assertEquals(4, categories.size());
        Assert.assertEquals(1L, categories.get(0).getId());
        Assert.assertEquals(2L, categories.get(1).getId());
        Assert.assertEquals(6L, categories.get(2).getId());
        Assert.assertEquals(8L, categories.get(3).getId());
        categories = relationService.getCategoryByCourseId(CategoryEnabled, courseIds);
        Assert.assertEquals(2, categories.size());
        Assert.assertEquals(1L, categories.get(0).getId());
        Assert.assertEquals(2L, categories.get(1).getId());
        categories = relationService.getCategoryByCourseId(CategoryDisabled, courseIds);
        Assert.assertEquals(1, categories.size());
        Assert.assertEquals(6L, categories.get(0).getId());
        categories = relationService.getCategoryByCourseId(CategoryDeleted, courseIds);
        Assert.assertEquals(1, categories.size());
        Assert.assertEquals(8L, categories.get(0).getId());
    }

    @Test
    public void testGetCourseByCategoryId() {
        long categoryId = 1L;
        List<CourseBean> courses = relationService.getCourseByCategoryId(All, categoryId);
        Assert.assertEquals(5, courses.size());
        Assert.assertEquals(8L, courses.get(0).getId());
        Assert.assertEquals(7L, courses.get(1).getId());
        Assert.assertEquals(6L, courses.get(2).getId());
        Assert.assertEquals(2L, courses.get(3).getId());
        Assert.assertEquals(1L, courses.get(4).getId());
        courses = relationService.getCourseByCategoryId(CourseEnable, categoryId);
        Assert.assertEquals(2, courses.size());
        Assert.assertEquals(2L, courses.get(0).getId());
        Assert.assertEquals(1L, courses.get(1).getId());
        courses = relationService.getCourseByCategoryId(CourseDisable, categoryId);
        Assert.assertEquals(2, courses.size());
        Assert.assertEquals(8L, courses.get(0).getId());
        Assert.assertEquals(7L, courses.get(1).getId());
        courses = relationService.getCourseByCategoryId(CourseEditing, categoryId);
        Assert.assertEquals(1, courses.size());
        Assert.assertEquals(6L, courses.get(0).getId());
    }

    //==========================================
    //   category getter
    //==========================================

    @Test
    public void testExistsCategory() {
        long categoryId = 1;
        boolean exists = service.existsCategory(categoryId);
        Assert.assertTrue(exists);

        categoryId = 100L;
        exists = service.existsCategory(categoryId);
        Assert.assertFalse(exists);
    }

    @Test
    public void testCountByStatus() {
        String status = "ALL";
        long count = service.countByStatus(status);
        Assert.assertEquals(9, count);

        status = "ENABLED";
        count = service.countByStatus(status);
        Assert.assertEquals(6, count);

        status = "DISABLED";
        count = service.countByStatus(status);
        Assert.assertEquals(2, count);

        status = "DELETED";
        count = service.countByStatus(status);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testGetCategoryByStatus() {
        String status = "ALL";
        List<CourseCategoryBean> beans = service.getCategoryByStatus(status, 0, 3);
        Assert.assertEquals(3, beans.size());
        beans = service.getCategoryByStatus(status, 0, 10);
        Assert.assertEquals(9, beans.size());

        status = "ENABLED";
        beans = service.getCategoryByStatus(status, 0, 10);
        Assert.assertEquals(6, beans.size());
        Assert.assertEquals(1, beans.get(0).getId());
        Assert.assertEquals(9, beans.get(beans.size()-1).getId());

        status = "DISABLED";
        beans = service.getCategoryByStatus(status, 0, 10);
        Assert.assertEquals(2, beans.size());
        Assert.assertEquals(6, beans.get(0).getId());
        Assert.assertEquals(7, beans.get(1).getId());

        status = "DELETED";
        beans = service.getCategoryByStatus(status, 0, 10);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals(8, beans.get(0).getId());
    }

    @Test
    public void testGetCategoryByStatusAndIds() {
        List<Long> ids = Arrays.asList(new Long[]{5L, 6L, 7L, 8L, 9L});
        String status = "";
        List<CourseCategoryBean> beans = service.getCategoryByStatusAndIds(status, ids);
        Assert.assertEquals(0, beans.size());

        status = "ENABLED";
        beans = service.getCategoryByStatusAndIds(status, ids);
        Assert.assertEquals(2, beans.size());
        Assert.assertEquals(5, beans.get(0).getId());
        Assert.assertEquals(9, beans.get(1).getId());

        status = "DISABLED";
        beans = service.getCategoryByStatusAndIds(status, ids);
        Assert.assertEquals(2, beans.size());
        Assert.assertEquals(6, beans.get(0).getId());
        Assert.assertEquals(7, beans.get(1).getId());

        status = "DELETED";
        beans = service.getCategoryByStatusAndIds(status, ids);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals(8, beans.get(0).getId());
    }

    //==========================================
    //   category update
    //==========================================
    @Test
    public void testUpdateCategory() {
        int  categoryId = 100;
        String name = "course_category_update_test";
        String introduction = "test introduction";
        String status = "DISABLED";
        String imageName = "aaaa.png";
        InputStream image = new ByteArrayInputStream(name.getBytes());

        Throwable         excp = null;
        CourseCategoryBean  bean = null;
        try {
            service.updateCategory(categoryId, name, introduction, status, imageName, image); }
        catch (Exception e) { excp = e; }
        Assert.assertNotNull(excp);

        categoryId = 9;
        excp       = null;
        bean       = null;
        try { bean = service.updateCategory(categoryId, name, introduction, status, imageName, image); }
        catch (Exception e) { excp = e; }
        Assert.assertNull(excp);
        Assert.assertNotNull(bean);
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(introduction, bean.getIntroduction());
        Assert.assertEquals(CommonStatus.DISABLED, bean.getStatus());
        Assert.assertTrue(userStorage.fileExist(bean.getImageId()));
        userStorage.deleteFile(bean.getImageUrl());
        Assert.assertFalse(userStorage.fileExist(bean.getImageId()));
    }

    //=================================================================
    //         relation add
    //=================================================================
    @Test
    public void testAddRelation() {
        long courseId = 1;
        long categoryId = 3;
        CourseCategoryRelationBean relation = relationService.getRelation(courseId, categoryId);
        Assert.assertNotNull(relation);
        Assert.assertEquals(CommonStatus.DISABLED, relation.getStatus());
        relationService.setCourseRelation(courseId, categoryId);
        relation = relationService.getRelation(courseId, categoryId);
        Assert.assertNotNull(relation);
        Assert.assertEquals(CommonStatus.ENABLED, relation.getStatus());

        courseId = 9;
        relation = relationService.getRelation(courseId, categoryId);
        Assert.assertNull(relation);
        relationService.setCourseRelation(courseId, categoryId);
        relation = relationService.getRelation(courseId, categoryId);
        Assert.assertNotNull(relation);
        Assert.assertEquals(CommonStatus.ENABLED, relation.getStatus());

    }

    //=================================================================
    //         add
    //=================================================================
    @Test
    public void testAddCategory() {
        long id = 1L;
        List<Long> ids = Arrays.asList(new Long[]{id});
        String name = "testeaaa";
        String introduction = "test introduction";
        String status = "ENABLED";
        String imageName = "image.png";
        InputStream image = new ByteArrayInputStream(name.getBytes());

        CourseCategoryBean bean = null;
        Throwable        excp = null;
        bean = service.getCategoryByStatusAndIds(status, ids).get(0);

        try { service.addCategory(bean.getName(), introduction, imageName, image); }
        catch (Exception ex) { excp = ex; }
        Assert.assertNotNull(excp);

        bean = service.addCategory(name, introduction, imageName, image);
        Assert.assertNotNull(bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(introduction, bean.getIntroduction());
        Assert.assertTrue(userStorage.fileExist(bean.getImageId()));
        userStorage.deleteFile(bean.getImageUrl());
        Assert.assertFalse(userStorage.fileExist(bean.getImageId()));
    }
}
