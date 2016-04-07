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
import org.apache.catalina.util.StringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

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

    public TagsBean getTag(long tagId) {
        logger.info("get tag by id {}" + tagId);
        TagsEntity tag = tagsRep.findOne(tagId);
        if (null==tag) {
            logger.info("get tag by id, doesn't exist!");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        TagsBean tagB = tagsConverter.convert(tag);
        if (tagB.getImageId()>0) {
            String imageUrl = storageService.getFilePath(tagB.getImageId());
            tagB.setImageUrl(imageUrl);
        }

        return tagB;
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

    public TagsCategoryBean getCategoryById(long categoryId) {
        TagsCategoryEntity categoryE = categoryRep.findOne(categoryId);
        if (null==categoryE) {
            return new TagsCategoryBean();
        }

        TagsCategoryBean   categoryB = categoryConverter.convert(categoryE);
        long   imageId = categoryB.getImageId();
        String imgPath = storageService.getFilePath(imageId);
        categoryB.setImageUrl(imgPath);

        List<TagsBean> tagsB = getTagsByCategoryId(categoryId);
        categoryB.setTags(tagsB);
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

    public List<TagsCategoryBean> getAllCategory() {
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

    //=================================================================
    //         update
    //=================================================================

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
                tagE.setName(name);
                changed = true;
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
        }
        return tagsConverter.convert(tagE);
    }

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
        }
        return categoryConverter.convert(categoryE);
    }

    //=================================================================
    //         delete
    //=================================================================

    public long deleteTag(long tagId) {
        TagsEntity tagsE = tagsRep.findOne(tagId);
        if (null!=tagsE) {
            long       imageId = tagsE.getImageId();
            List<Long> imgIds  = new ArrayList<>();
            imgIds.add(imageId);
            storageService.deleteFiles(imgIds);
        }
        throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
    }
}
