package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.TagsBean;
import com.cooltoo.beans.TagsCategoryBean;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.util.FileUtil;
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
import java.io.InputStream;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/8.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/tags_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/tag_category_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml")
})
public class TagsServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(TagsServiceTest.class.getName());

    @Autowired
    private TagsService tagService;
    @Autowired
    private OfficialFileStorageService officialStorage;


    //==========================================
    //  get
    //==========================================
    @Test
    public void testGetTagById() {
        long tagId = 100;
        TagsBean bean = tagService.getTag(tagId);
        Assert.assertNull(bean);

        tagId = 12;
        bean = tagService.getTag(tagId);
        Assert.assertNotNull(bean);
        Assert.assertEquals(tagId, bean.getId());
        logger.info("tag is : {}", bean);
    }

    @Test
    public void testGetTagByCategoryId() {
        long categoryId = 100;
        List<TagsBean> beans = tagService.getTagsByCategoryId(categoryId);
        Assert.assertTrue(beans.isEmpty());

        categoryId = 3;
        beans = tagService.getTagsByCategoryId(categoryId);
        Assert.assertNotNull(beans);
        for (TagsBean bean : beans) {
            Assert.assertEquals(categoryId, bean.getCategoryId());
        }
        logger.info("tags are : {}", beans);
    }

    @Test
    public void testGetAllTag() {
        List<TagsBean> beans = tagService.getAllTag();
        Assert.assertFalse(beans.isEmpty());
        Assert.assertEquals(34, beans.size());
        logger.info("tags are : {}", beans);
    }

    @Test
    public void testGetCategoryById() {
        long id = 15;
        TagsCategoryBean bean = tagService.getCategory(id);
        Assert.assertNull(bean);

        id = 2;
        bean = tagService.getCategory(id);
        Assert.assertEquals(id, bean.getId());
        logger.info("category is : {}", bean);
    }

    @Test
    public void testGetCategoryByIdWithTags() {
        long id = 15;
        TagsCategoryBean bean = tagService.getCategoryWithTags(id);
        Assert.assertNull(bean);

        id = 2;
        bean = tagService.getCategoryWithTags(id);
        Assert.assertEquals(id, bean.getId());
        Assert.assertFalse(bean.getTags().isEmpty());
        logger.info("category is : {}", bean);
    }

    @Test
    public void testGetAllCategory() {
        long id = 15;
        List<TagsCategoryBean> beans = tagService.getAllCategory();
        Assert.assertNotNull(beans);
        Assert.assertEquals(9, beans.size());
        logger.info("categories are : {}", beans);
    }

    @Test
    public void testGetAllCategoryWithTags() {
        long id = 15;
        List<TagsCategoryBean> beans = tagService.getAllCategoryWithTags();
        Assert.assertNotNull(beans);
        Assert.assertEquals(9, beans.size());
        logger.info("categories are : {}", beans);
    }

    //==========================================
    //  update
    //==========================================

    @Test
    public void testUpdateTag() {
        long   tagId      = 100;
        String name       = "tag_update_test";
        long   categoryId = 100;
        String imageName  = "aaaa.png";
        InputStream image = new ByteArrayInputStream(name.getBytes());

        Throwable excp = null;
        TagsBean  bean = null;
        try { bean = tagService.updateTag(tagId, categoryId, name, imageName, image); }
        catch (Exception e) { excp = e; }
        Assert.assertNotNull(excp);


        tagId = 3;
        excp  = null;
        bean  = null;
        try { bean = tagService.updateTag(tagId, categoryId, name, imageName, image); }
        catch (Exception e) { excp = e; }
        Assert.assertNull(excp);
        Assert.assertNotNull(bean);
        Assert.assertNotEquals(categoryId, bean.getCategoryId());
        Assert.assertEquals(name, bean.getName());
        Assert.assertTrue(officialStorage.fileExist(bean.getImageId()));
        officialStorage.deleteFile(bean.getImageUrl());
        Assert.assertFalse(officialStorage.fileExist(bean.getImageId()));

        categoryId = 9;
        excp       = null;
        bean       = null;
        try { bean = tagService.updateTag(tagId, categoryId, name, imageName, image); }
        catch (Exception e) { excp = e; }
        Assert.assertNull(excp);
        Assert.assertNotNull(bean);
        Assert.assertEquals(categoryId, bean.getCategoryId());
        Assert.assertEquals(name, bean.getName());
        Assert.assertTrue(officialStorage.fileExist(bean.getImageId()));
        officialStorage.deleteFile(bean.getImageUrl());
        Assert.assertFalse(officialStorage.fileExist(bean.getImageId()));
    }

    @Test
    public void testUpdateCategory() {
        long   categoryId = 100;
        String name       = "tag_update_test";
        String imageName  = "aaaa.png";
        InputStream image = new ByteArrayInputStream(name.getBytes());

        Throwable         excp = null;
        TagsCategoryBean  bean = null;
        try {
            tagService.updateCategory(categoryId, name, imageName, image); }
        catch (Exception e) { excp = e; }
        Assert.assertNotNull(excp);

        categoryId = 9;
        excp       = null;
        bean       = null;
        try { bean = tagService.updateCategory(categoryId, name, imageName, image); }
        catch (Exception e) { excp = e; }
        Assert.assertNull(excp);
        Assert.assertNotNull(bean);
        Assert.assertEquals(name, bean.getName());
        Assert.assertTrue(officialStorage.fileExist(bean.getImageId()));
        officialStorage.deleteFile(bean.getImageUrl());
        Assert.assertFalse(officialStorage.fileExist(bean.getImageId()));
    }

    //=================================================================
    //         delete
    //=================================================================

    @Test
    public void testDeleteTag() {
        long tagId  = 1;
        String storagePath = officialStorage.getStoragePath();
        String imagePath   = "aa/c7c653fdc989b9fa7463717fcd29c0ea157710";
        try {
            FileUtil.getInstance().writeFile(new ByteArrayInputStream(imagePath.getBytes()), new File(storagePath + imagePath));
        } catch (Exception ex) {}

        TagsBean bean = tagService.getTag(tagId);
        Assert.assertNotNull(bean);
        Assert.assertTrue(officialStorage.fileExist(bean.getImageId()));

        long tagId2 = tagService.deleteTag(tagId);
        Assert.assertEquals(tagId2, tagId);

        bean = tagService.getTag(tagId);
        Assert.assertNull(bean);

        Assert.assertFalse(officialStorage.fileExist(imagePath));
    }

    @Test
    public void testDeleteTagByIds() {
        String ids = "1,2,3,4,5";
        List<TagsBean> beans = tagService.getTagByIds(ids);
        Assert.assertEquals(5, beans.size());

        String tmpIds = tagService.deleteTagByIds(ids);
        Assert.assertEquals(ids, tmpIds);

        beans = tagService.getTagByIds(ids);
        Assert.assertEquals(0, beans.size());
    }

    @Test
    public void testDeleteCategory() {
        long tagId  = 8;

        TagsCategoryBean bean = tagService.getCategory(tagId);
        Assert.assertNotNull(bean);

        long tagId2 = tagService.deleteCategory(tagId);
        Assert.assertEquals(tagId2, tagId);

        bean = tagService.getCategory(tagId);
        Assert.assertNull(bean);
    }

    @Test
    public void testDeleteCategoryIds() {
        String ids = "1,2,3,4";
        List<TagsCategoryBean> beans = tagService.getCategoryByIds(ids);
        Assert.assertEquals(4, beans.size());

        String tmpIds = tagService.deleteCategoryByIds(ids);
        Assert.assertEquals(ids, tmpIds);

        beans = tagService.getCategoryByIds(ids);
        Assert.assertEquals(0, beans.size());

    }

    //=================================================================
    //         add
    //=================================================================

    @Test
    public void testAddTag() {
        long   id         = 1;
        long   categoryId = 100;
        String name       = "testeaaa";
        String imageName  = "image.png";
        InputStream image = new ByteArrayInputStream(name.getBytes());

        TagsBean bean = null;
        Throwable        excp = null;
        bean = tagService.getTag(id);

        try { bean = tagService.addTags(bean.getName(), categoryId, imageName, null); }
        catch (Exception ex) { excp = ex; }
        Assert.assertNotNull(excp);

        bean = tagService.addTags(name, categoryId, imageName, image);
        Assert.assertNotNull(bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(name, bean.getName());
        Assert.assertNotEquals(categoryId, bean.getCategoryId());
        Assert.assertTrue(officialStorage.fileExist(bean.getImageId()));
        officialStorage.deleteFile(bean.getImageUrl());
        Assert.assertFalse(officialStorage.fileExist(bean.getImageId()));

        categoryId = 3;
        name = "aaaaaaaa";
        bean = tagService.addTags(name, categoryId, imageName, image);
        Assert.assertNotNull(bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(categoryId, bean.getCategoryId());
        Assert.assertTrue(officialStorage.fileExist(bean.getImageId()));
        officialStorage.deleteFile(bean.getImageUrl());
        Assert.assertFalse(officialStorage.fileExist(bean.getImageId()));
    }

    @Test
    public void testAddCategory() {
        long   id         = 1;
        String name       = "testeaaa";
        String imageName  = "image.png";
        InputStream image = new ByteArrayInputStream(name.getBytes());

        TagsCategoryBean bean = null;
        Throwable        excp = null;
        bean = tagService.getCategory(id);

        try { bean = tagService.addTagCategory(bean.getName(), imageName, image); }
        catch (Exception ex) { excp = ex; }
        Assert.assertNotNull(excp);

        bean = tagService.addTagCategory(name, imageName, image);
        Assert.assertNotNull(bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertTrue(officialStorage.fileExist(bean.getImageId()));
        officialStorage.deleteFile(bean.getImageUrl());
        Assert.assertFalse(officialStorage.fileExist(bean.getImageId()));
    }
}
