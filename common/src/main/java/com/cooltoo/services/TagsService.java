package com.cooltoo.services;

import com.cooltoo.beans.TagsBean;
import com.cooltoo.beans.TagsCategoryBean;
import com.cooltoo.converter.TagsBeanConverter;
import com.cooltoo.converter.TagsCategoryBeanConverter;
import com.cooltoo.entities.TagsCategoryEntity;
import com.cooltoo.entities.TagsEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.TagsCategoryRepository;
import com.cooltoo.repository.TagsRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Service("TagsService")
public class TagsService {
    private static final Logger logger = LoggerFactory.getLogger(TagsService.class.getName());

    @Autowired
    private TagsRepository tagsRep;
    @Autowired
    private TagsCategoryRepository categoryRep;
    @Autowired
    private TagsBeanConverter tagsConverter;
    @Autowired
    private TagsCategoryBeanConverter categoryConverter;
    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    //=================================================================
    //         getter
    //=================================================================

    public long getTagCount() {
        long count = tagsRep.count();
        logger.info("tag count is {}", count);
        return count;
    }

    public long getCategoryCount() {
        long count = categoryRep.count();
        logger.info("tag category count is {}", count);
        return count;
    }

    public TagsBean getTag(long tagId) {
        logger.info("get tag by id {}", tagId);
        TagsEntity tag = tagsRep.findOne(tagId);
        if (null==tag) {
            logger.info("get tag by id, doesn't exist!");
            return null;
        }
        TagsBean tagB = tagsConverter.convert(tag);
        if (tagB.getImageId()>0) {
            String imageUrl = storageService.getFilePath(tagB.getImageId());
            tagB.setImageUrl(imageUrl);
        }

        return tagB;
    }

    public List<TagsBean> getTagByIds(String tagIds) {
        if (VerifyUtil.isIds(tagIds)) {
            List<TagsEntity> tagsE     = null;
            List<TagsBean>   tagsB     = new ArrayList<>();
            List<Long>       imgIds    = new ArrayList<>();
            Map<Long,String> imgId2Path= null;
            List<Long>       lTagIds   = new ArrayList<>();
            String[]         strTagIds = tagIds.split(",");
            for (String id : strTagIds) {
                lTagIds.add(Long.parseLong(id));
            }

            tagsE = tagsRep.findByIdIn(lTagIds);
            if (null!=tagsE) {
                TagsBean bean = null;
                String   path = null;
                for (TagsEntity tagE : tagsE) {
                    imgIds.add(tagE.getImageId());
                }
                imgId2Path = storageService.getFilePath(imgIds);
                for (TagsEntity tagE : tagsE) {
                    bean = tagsConverter.convert(tagE);
                    path = imgId2Path.get(bean.getImageId());
                    bean.setImageUrl(path);
                    tagsB.add(bean);
                }

                return tagsB;
            }
        }
        return new ArrayList<>();
    }

    public List<TagsBean> getTagsByCategoryId(long categoryId) {
        Sort             sort = new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
        List<TagsEntity> tagsE = tagsRep.findByCategoryId(categoryId, sort);

        if (null!=tagsE && !tagsE.isEmpty()) {
            List<TagsBean> tagsB    = new ArrayList<>();
            List<Long>     imageIds = new ArrayList<>();
            for (TagsEntity tagE : tagsE) {
                TagsBean tagB = tagsConverter.convert(tagE);
                tagsB.add(tagB);
                imageIds.add(tagB.getImageId());
            }

            Map<Long, String> imgId2Path = storageService.getFilePath(imageIds);
            for (TagsBean tagB : tagsB) {
                long   imgId   = tagB.getImageId();
                String imgPath = imgId2Path.get(imgId);
                tagB.setImageUrl(imgPath);
            }

            return tagsB;
        }

        return new ArrayList<>();
    }

    public List<TagsBean> getTagsWithoutCategoryId() {
        Sort             sort  = new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
        List<TagsEntity> tagsE = tagsRep.findByCategoryIdLessThanEqual(0, sort);

        if (null!=tagsE && !tagsE.isEmpty()) {
            List<TagsBean> tagsB    = new ArrayList<>();
            List<Long>     imageIds = new ArrayList<>();
            for (TagsEntity tagE : tagsE) {
                TagsBean tagB = tagsConverter.convert(tagE);
                tagsB.add(tagB);
                imageIds.add(tagB.getImageId());
            }

            Map<Long, String> imgId2Path = storageService.getFilePath(imageIds);
            for (TagsBean tagB : tagsB) {
                long   imgId   = tagB.getImageId();
                String imgPath = imgId2Path.get(imgId);
                tagB.setImageUrl(imgPath);
            }

            return tagsB;
        }

        return new ArrayList<>();
    }

    public List<TagsBean> getTagsByPage(int pageIndex, int sizeOfPage) {
        Sort             sort  = new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
        PageRequest      page  = new PageRequest(pageIndex, sizeOfPage, sort);
        Page<TagsEntity> tagsE = tagsRep.findAll(page);

        if (null!=tagsE && tagsE.getSize()>0) {
            List<TagsBean> tagsB    = new ArrayList<>();
            List<Long>     imageIds = new ArrayList<>();
            for (TagsEntity tagE : tagsE) {
                TagsBean tagB = tagsConverter.convert(tagE);
                tagsB.add(tagB);
                imageIds.add(tagB.getImageId());
            }

            Map<Long, String> imgId2Path = storageService.getFilePath(imageIds);
            for (TagsBean tagB : tagsB) {
                long   imgId   = tagB.getImageId();
                String imgPath = imgId2Path.get(imgId);
                tagB.setImageUrl(imgPath);
            }

            return tagsB;
        }

        return new ArrayList<>();
    }

    public List<TagsCategoryBean> getCategoryByPage(int pageIndex, int sizeOfPage) {
        // get all category
        Sort                     sortCategory = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        PageRequest              pageCategory = new PageRequest(pageIndex, sizeOfPage, sortCategory);
        Page<TagsCategoryEntity> allCategory  = categoryRep.findAll(pageCategory);

        // has category
        if (null!=allCategory && allCategory.getSize()>0) {

            // cache image path
            List<Long> imageIds = new ArrayList<>();
            for (TagsCategoryEntity category : allCategory) {
                imageIds.add(category.getImageId());
            }
            Map<Long, String> imgId2Path = storageService.getFilePath(imageIds);

            // converter entity to bean
            List<TagsCategoryBean> allCategoryB = new ArrayList<>();
            for (TagsCategoryEntity category : allCategory) {
                long imageId = category.getImageId();
                String imgPath = imgId2Path.get(imageId);
                TagsCategoryBean bean = categoryConverter.convert(category);
                bean.setImageUrl(imgPath);
                allCategoryB.add(bean);
            }

            return allCategoryB;
        }

        return new ArrayList<>();
    }

    public TagsCategoryBean getCategoryWithTags(long categoryId) {
        TagsCategoryEntity categoryE = categoryRep.findOne(categoryId);
        if (null==categoryE) {
            return null;
        }

        TagsCategoryBean   categoryB = categoryConverter.convert(categoryE);
        long   imageId = categoryB.getImageId();
        String imgPath = storageService.getFilePath(imageId);
        categoryB.setImageUrl(imgPath);

        List<TagsBean> tagsB = getTagsByCategoryId(categoryId);
        categoryB.setTags(tagsB);
        return categoryB;
    }

    public TagsCategoryBean getCategory(long categoryId) {
        TagsCategoryEntity categoryE = categoryRep.findOne(categoryId);
        if (null==categoryE) {
            return null;
        }

        TagsCategoryBean   categoryB = categoryConverter.convert(categoryE);
        long   imageId = categoryB.getImageId();
        String imgPath = storageService.getFilePath(imageId);
        categoryB.setImageUrl(imgPath);

        return categoryB;
    }

    public List<TagsBean> getAllTag() {
        Sort             sortTags = new Sort(new Sort.Order(Sort.Direction.ASC, "categoryId")
                                           , new Sort.Order(Sort.Direction.ASC, "name"));
        List<TagsEntity> allTags  = tagsRep.findAll(sortTags);

        if (null!=allTags && !allTags.isEmpty()) {
            List<Long> imageIds = new ArrayList<>();
            for (TagsEntity tagE : allTags) {
                imageIds.add(tagE.getImageId());
            }

            Map<Long, String> imgId2Path = storageService.getFilePath(imageIds);

            TagsBean        tagB     = null;
            List<TagsBean>  allTagsB = new ArrayList<>();
            for (TagsEntity tagE : allTags) {
                long   imageId = tagE.getImageId();
                String imgPath = imgId2Path.get(imageId);
                tagB = tagsConverter.convert(tagE);
                tagB.setImageUrl(imgPath);
                allTagsB.add(tagB);
            }

            return allTagsB;
        }

        return new ArrayList<>();
    }

    public List<TagsCategoryBean> getAllCategoryWithTags() {
        // get all tags
        List<TagsBean>           allTags      = getAllTag();

        // get all category
        Sort                     sortCategory = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<TagsCategoryEntity> allCategory  = categoryRep.findAll(sortCategory);

        // has category
        if (null!=allCategory && !allCategory.isEmpty()) {

            // cache image path
            List<Long> imageIds = new ArrayList<>();
            for (TagsCategoryEntity category : allCategory) {
                imageIds.add(category.getImageId());
            }
            Map<Long, String> imgId2Path = storageService.getFilePath(imageIds);

            // converter entity to bean
            List<TagsCategoryBean> allCategoryB = new ArrayList<>();
            for (TagsCategoryEntity category : allCategory) {
                long imageId = category.getImageId();
                String imgPath = imgId2Path.get(imageId);
                TagsCategoryBean bean = categoryConverter.convert(category);
                bean.setImageUrl(imgPath);
                allCategoryB.add(bean);

                // add tag to category
                List<TagsBean> tagsB = new ArrayList<>();
                for (int i = 0; i < allTags.size(); i++) {
                    TagsBean tagB = allTags.get(i);
                    if (bean.getId() != tagB.getCategoryId()) {
                        continue;
                    }
                    tagsB.add(tagB);
                }
                bean.setTags(tagsB);
            }

            return allCategoryB;
        }

        return new ArrayList<>();
    }

    public List<TagsCategoryBean> getAllCategory() {
        // get all category
        Sort                     sortCategory = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<TagsCategoryEntity> allCategory  = categoryRep.findAll(sortCategory);

        // has category
        if (null!=allCategory && !allCategory.isEmpty()) {

            // cache image path
            List<Long> imageIds = new ArrayList<>();
            for (TagsCategoryEntity category : allCategory) {
                imageIds.add(category.getImageId());
            }
            Map<Long, String> imgId2Path = storageService.getFilePath(imageIds);

            // converter entity to bean
            List<TagsCategoryBean> allCategoryB = new ArrayList<>();
            for (TagsCategoryEntity category : allCategory) {
                long imageId = category.getImageId();
                String imgPath = imgId2Path.get(imageId);
                TagsCategoryBean bean = categoryConverter.convert(category);
                bean.setImageUrl(imgPath);
                allCategoryB.add(bean);
            }

            return allCategoryB;
        }

        return new ArrayList<>();
    }


    public List<TagsCategoryBean> getCategoryByIds(String categoryIds) {
        if (VerifyUtil.isIds(categoryIds)) {
            List<TagsCategoryBean> categoriesB = new ArrayList<>();
            List<Long>             lTagIds     = new ArrayList<>();
            String[]               strTagIds   = categoryIds.split(",");
            for (String id : strTagIds) {
                lTagIds.add(Long.parseLong(id));
            }

            List<TagsCategoryBean> all = getAllCategory();
            for (TagsCategoryBean categoryB : all) {
                if(!lTagIds.contains(categoryB.getId())) {
                    continue;
                }
                categoriesB.add(categoryB);
            }

            return categoriesB;
        }
        return new ArrayList<>();
    }


    public List<TagsCategoryBean> getCategoryWithTagsByIds(String categoryIds) {
        if (VerifyUtil.isIds(categoryIds)) {
            List<TagsCategoryBean> categoriesB = new ArrayList<>();
            List<Long>             lTagIds     = new ArrayList<>();
            String[]               strTagIds   = categoryIds.split(",");
            for (String id : strTagIds) {
                lTagIds.add(Long.parseLong(id));
            }

            List<TagsCategoryBean> all = getAllCategoryWithTags();
            for (TagsCategoryBean categoryB : all) {
                if(!lTagIds.contains(categoryB.getId())) {
                    continue;
                }
                categoriesB.add(categoryB);
            }

            return categoriesB;
        }
        return new ArrayList<>();
    }

    //=================================================================
    //         update
    //=================================================================

    @Transactional
    public TagsBean updateTag(long tagId, long categoryId, String name, String imageName, InputStream image) {
        boolean    changed = false;
        String     imgPath = "";
        TagsEntity tagE    = tagsRep.findOne(tagId);

        if (null==tagE) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (categoryRep.exists(categoryId)) {
            tagE.setCategoryId(categoryId);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(name)) {
            if (!name.equals(tagE.getName())) {
                long count = tagsRep.countByCategoryIdAndName(tagE.getCategoryId(), name);
                if (count<=0) {
                    tagE.setName(name);
                    changed = true;
                }
            }
        }
        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "tag_tmp_" + System.nanoTime();
            }
            long imgId = storageService.saveFile(tagE.getImageId(), imageName, image);
            if (imgId>0) {
                imgPath = storageService.getFilePath(imgId);
                tagE.setImageId(imgId);
                changed = true;
            }
        }

        if (changed) {
            tagE = tagsRep.save(tagE);
            TagsBean bean = tagsConverter.convert(tagE);
            bean.setImageUrl(imgPath);
            return bean;
        }
        return tagsConverter.convert(tagE);
    }

    @Transactional
    public TagsCategoryBean updateCategory(long categoryId, String name, String imageName, InputStream image) {
        boolean    changed = false;
        String     imgPath = "";

        TagsCategoryEntity categoryE    = categoryRep.findOne(categoryId);
        if (null==categoryE) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (!VerifyUtil.isStringEmpty(name)) {
            if (!name.equals(categoryE.getName())) {
                long count = categoryRep.countByName(name);
                if (count<=0) {
                    categoryE.setName(name);
                    changed = true;
                }
            }
        }
        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "tag_tmp_" + System.nanoTime();
            }
            long imgId = storageService.saveFile(categoryE.getImageId(), imageName, image);
            if (imgId>0) {
                imgPath = storageService.getFilePath(imgId);
                categoryE.setImageId(imgId);
                changed = true;
            }
        }

        if (changed) {
            categoryE = categoryRep.save(categoryE);
            TagsCategoryBean bean = categoryConverter.convert(categoryE);
            bean.setImageUrl(imgPath);
            return bean;
        }
        return categoryConverter.convert(categoryE);
    }

    //=================================================================
    //         delete
    //=================================================================

    @Transactional
    public long deleteTag(long tagId) {
        TagsEntity tagsE = tagsRep.findOne(tagId);
        if (null!=tagsE) {
            long       imageId = tagsE.getImageId();
            List<Long> imgIds  = new ArrayList<>();
            imgIds.add(imageId);
            storageService.deleteFiles(imgIds);
            tagsRep.delete(tagId);

            return tagId;
        }
        throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
    }

    @Transactional
    public String deleteTagByIds(String tagIds) {
        if (VerifyUtil.isIds(tagIds)) {
            List<TagsEntity> tagsE     = null;
            List<Long>       imgIds    = new ArrayList<>();
            List<Long>       lTagIds   = new ArrayList<>();
            String[]         strTagIds = tagIds.split(",");
            for (String id : strTagIds) {
                lTagIds.add(Long.parseLong(id));
            }

            tagsE = tagsRep.findByIdIn(lTagIds);
            if (null!=tagsE) {
                for (TagsEntity tagE : tagsE) {
                    imgIds.add(tagE.getImageId());
                }
            }
            if (!imgIds.isEmpty()) {
                storageService.deleteFiles(imgIds);
            }
            tagsRep.delete(tagsE);

            return tagIds;
        }
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }

    @Transactional
    public long deleteCategory(long categoryId) {
        TagsCategoryBean category = getCategory(categoryId);
        if (null!=category) {
            List<Long> imgIds   = new ArrayList<>();
            imgIds.add(category.getImageId());

            List<TagsBean> tags = category.getTags();
            if (null!=tags && !tags.isEmpty()) {
                for (TagsBean tag : tags) {
                    imgIds.add(tag.getImageId());
                }
            }

            storageService.deleteFiles(imgIds);
            tagsRep.deleteByCategoryId(categoryId);
            categoryRep.delete(categoryId);

            return categoryId;
        }
        throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
    }

    @Transactional
    public String deleteCategoryByIds(String categoryIds) {
        if (VerifyUtil.isIds(categoryIds)) {

            List<Long> imgIds         = new ArrayList<>();
            List<Long> lCategoryIds   = new ArrayList<>();
            String[]   strCategoryIds = categoryIds.split(",");
            for (String id : strCategoryIds) {
                lCategoryIds.add(Long.parseLong(id));
            }

            List<TagsCategoryEntity> categoriesE = categoryRep.findByIdIn(lCategoryIds);
            List<TagsEntity>         tagsE       = tagsRep.findByCategoryIdIn(lCategoryIds);

            if (null!=categoriesE) {
                for (TagsCategoryEntity categoryE : categoriesE) {
                    imgIds.add(categoryE.getImageId());
                }
            }
            if (!imgIds.isEmpty()) {
                storageService.deleteFiles(imgIds);
            }
            // set category id = 0
            if (null!=tagsE) {
                for (TagsEntity tagE : tagsE) {
                    tagE.setCategoryId(0);
                }
                tagsRep.save(tagsE);
            }
            // delete category
            categoryRep.delete(categoriesE);

            return categoryIds;
        }
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }

    @Transactional
    public String deleteCategoryWithTagsByIds(String categoryIds) {
        if (VerifyUtil.isIds(categoryIds)) {

            List<Long> imgIds         = new ArrayList<>();
            List<Long> lCategoryIds   = new ArrayList<>();
            String[]   strCategoryIds = categoryIds.split(",");
            for (String id : strCategoryIds) {
                lCategoryIds.add(Long.parseLong(id));
            }

            List<TagsCategoryEntity> categoriesE = categoryRep.findByIdIn(lCategoryIds);
            List<TagsEntity>         tagsE       = tagsRep.findByCategoryIdIn(lCategoryIds);

            if (null!=categoriesE) {
                for (TagsCategoryEntity categoryE : categoriesE) {
                    imgIds.add(categoryE.getImageId());
                }
            }
            if (null!=tagsE) {
                for (TagsEntity tagE : tagsE) {
                    imgIds.add(tagE.getImageId());
                }
            }
            if (!imgIds.isEmpty()) {
                storageService.deleteFiles(imgIds);
            }
            tagsRep.deleteByCategoryIdIn(lCategoryIds);
            categoryRep.delete(categoriesE);

            return categoryIds;
        }
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }


    //=================================================================
    //         add tag and category
    //=================================================================

    public TagsBean addTags(String name, long categoryId, String imageName, InputStream image) {
        logger.info("add tag : name={} categoryId={} imageName={} image={}", name, categoryId, imageName, (null!=image));

        String imagePath = null;
        TagsEntity entity = new TagsEntity();
        if (VerifyUtil.isStringEmpty(name)) {
//            logger.error("add tag : name is empty");
//            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
//        else if (tagsRep.countByName(name)>0) {
//            logger.error("add tag : name is exist");
//            throw new BadRequestException(ErrorCode.DATA_ERROR);
//        }
        else {
            entity.setName(name);
        }

        if (categoryId>0 && categoryRep.exists(categoryId)) {
            entity.setCategoryId(categoryId);
        }

        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "tag_tmp_" + System.nanoTime();
            }
            long fileId = storageService.saveFile(-1, imageName, image);
            if (fileId>0) {
                entity.setImageId(fileId);
                imagePath = storageService.getFilePath(fileId);
            }
        }
        else {
            logger.error("add tag : image is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        entity.setTimeCreated(new Date());
        entity = tagsRep.save(entity);

        TagsBean bean = tagsConverter.convert(entity);
        bean.setImageUrl(imagePath);

        return bean;
    }

    public TagsCategoryBean addTagCategory(String name, String imageName, InputStream image) {
        logger.info("add tag category : name={} imageName={} image={}", name, imageName, (null!=image));

        String imagePath = null;
        TagsCategoryEntity entity = new TagsCategoryEntity();
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("add tag : name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else if (categoryRep.countByName(name)>0) {
            logger.error("add tag : name is exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else {
            entity.setName(name);
        }

        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "category_tmp_" + System.nanoTime();
            }
            long fileId = storageService.saveFile(-1, imageName, image);
            if (fileId>0) {
                entity.setImageId(fileId);
                imagePath = storageService.getFilePath(fileId);
            }
        }

        entity.setTimeCreated(new Date());
        entity = categoryRep.save(entity);

        TagsCategoryBean bean = categoryConverter.convert(entity);
        bean.setImageUrl(imagePath);

        return bean;
    }

}
