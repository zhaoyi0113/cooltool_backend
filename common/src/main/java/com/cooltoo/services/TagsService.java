package com.cooltoo.services;

import com.cooltoo.beans.TagsBean;
import com.cooltoo.converter.TagsBeanConverter;
import com.cooltoo.converter.TagsCategoryBeanConverter;
import com.cooltoo.entities.TagsEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.TagsCategoryRepository;
import com.cooltoo.repository.TagsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

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

//    public List<TagsBean> getTagsByCategoryId(long categoryId) {
//        List<TagsEntity> tags =
//    }


}
