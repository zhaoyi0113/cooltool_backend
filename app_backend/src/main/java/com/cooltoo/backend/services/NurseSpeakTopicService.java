package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseSpeakTopicBean;
import com.cooltoo.backend.converter.NurseSpeakTopicBeanConverter;
import com.cooltoo.backend.entities.NurseSpeakTopicEntity;
import com.cooltoo.backend.repository.NurseSpeakTopicRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.RegionRepository;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/3.
 */
@Service("NurseSpeakTopicService")
public class NurseSpeakTopicService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakTopicService.class);
    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private NurseSpeakTopicRepository repository;
    @Autowired private NurseSpeakTopicBeanConverter beanConverter;
    @Autowired private NurseSpeakService speakService;
    @Autowired private NurseService nurseService;
    @Autowired private RegionRepository regionRepository;
    @Autowired private UserFileStorageService storageService;


    //=====================================================================
    //          get
    //=====================================================================
    public long countTopic(String title, String strStatus) {
        logger.info("count topic by title={} and status={}", title, strStatus);
        if (VerifyUtil.isStringEmpty(title)) {
            title = null;
        }
        else {
            title = VerifyUtil.reconstructSQLContentLike(title.trim());
        }
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = repository.countByTitleAndStatus(title, status);
        logger.info("count is {}", count);
        return count;
    }

    public List<NurseSpeakTopicBean> getTopic(String title, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get topic by title={} and status={}", title, strStatus);
        if (VerifyUtil.isStringEmpty(title)) {
            title = null;
        }
        else {
            title = VerifyUtil.reconstructSQLContentLike(title.trim());
        }
        CommonStatus status = CommonStatus.parseString(strStatus);

        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseSpeakTopicEntity> resultSet = repository.findByTitleAndStatus(title, status, page);
        List<NurseSpeakTopicBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);

        logger.info("count is {}", beans.size());
        return beans;
    }

    private List<NurseSpeakTopicBean> entitiesToBeans(Iterable<NurseSpeakTopicEntity> entities) {
        List<NurseSpeakTopicBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }

        NurseSpeakTopicBean bean;
        for (NurseSpeakTopicEntity entity : entities) {
            bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<NurseSpeakTopicBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> userIds = new ArrayList<>();
        List<Long> imageIds = new ArrayList<>();
        for (NurseSpeakTopicBean bean : beans) {
            if (!userIds.contains(bean.getCreatorId())) {
                userIds.add(bean.getCreatorId());
            }
            if (!imageIds.contains(bean.getProfileImageId())) {
                imageIds.add(bean.getProfileImageId());
            }
        }

        Map<Long, String> imageId2Url = storageService.getFilePath(imageIds);
        List<NurseBean> users = nurseService.getNurse(userIds);
        boolean userNull = VerifyUtil.isListEmpty(users);
        for (NurseSpeakTopicBean bean : beans) {
            long imageId = bean.getProfileImageId();
            String imageUrl = imageId2Url.get(imageId);
            bean.setProfileImageUrl(imageUrl);

            if (userNull) { continue; }
            long userId = bean.getCreatorId();
            for (NurseBean user : users) {
                if (user.getId() == userId) {
                    bean.setCreator(user);
                    break;
                }
            }
        }
    }
    //=====================================================================
    //          add
    //=====================================================================
    @Transactional
    public NurseSpeakTopicBean addSpeakTopic(long creatorId, String creatorType,
                                             String title, String label, String taxonomy, String description, int province
    ) {
        logger.info("add speak topic title={} label={} taxonomy={} description={} province={} by creatorId={} creatorType={}",
                title, label, taxonomy, description, province, creatorId, creatorType);

        if (VerifyUtil.isStringEmpty(title)) {
            logger.info("title is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        title = title.trim();
        if (repository.countByTitle(title)>0) {
            throw new BadRequestException(ErrorCode.RECORD_ALREADY_EXIST);
        }

        if (!nurseService.existNurse(creatorId)) {
            creatorId = 0;
        }
        UserType userType = UserType.parseString(creatorType);

        if (!regionRepository.exists(province)) {
            province = 0;
        }

        label = label.trim();
        taxonomy = taxonomy.trim();
        description = description.trim();

        NurseSpeakTopicEntity entity = new NurseSpeakTopicEntity();
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setCreatorId(creatorId);
        entity.setCreatorType(userType);
        entity.setTitle(title);
        entity.setProfileImageId(0);
        entity.setLabel(label);
        entity.setTaxonomy(taxonomy);
        entity.setDescription(description);
        entity.setProvince(province);
        entity.setClickNumber(0);
        entity = repository.save(entity);

        NurseSpeakTopicBean bean = beanConverter.convert(entity);
        logger.info("nurse speak topic is {}", bean);
        return bean;
    }

    //=====================================================================
    //          update
    //=====================================================================
    @Transactional
    public NurseSpeakTopicBean updateTopicProfilePhoto(long topicId, String imageName, InputStream image) {
        logger.info("update nurse speak topic {} profile photo name={} image={}", topicId, imageName, image!=null);
        NurseSpeakTopicEntity entity = repository.findOne(topicId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (null==image) {
            logger.info("image is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        String imageUrl = "";
        long imageId = 0;
        if (VerifyUtil.isStringEmpty(imageName)) {
            imageName = "topic_"+System.currentTimeMillis();
        }
        imageId = storageService.addFile(entity.getProfileImageId(), imageName, image);

        if (imageId>0) {
            imageUrl = storageService.getFilePath(imageId);
            entity.setProfileImageId(imageId);
            entity = repository.save(entity);
        }

        NurseSpeakTopicBean bean = beanConverter.convert(entity);
        bean.setProfileImageUrl(imageUrl);
        logger.info("update speak topic profile photo is {}", bean);
        return bean;
    }

    @Transactional
    public NurseSpeakTopicBean updateTopic(long topicId,
                                           long creatorId, String creatorType,
                                           String label, String taxonomy, String description, int province,
                                           String strStatus,
                                           long clickNumberIncrement
    ) {
        logger.info("update nurse speak topic {} with creatorId={} creatorType={} label={} taxonomy={} descr={} province={} status={} clickIncrement={}",
                topicId, creatorId, creatorType, label, taxonomy, description, province, strStatus, clickNumberIncrement);
        NurseSpeakTopicEntity entity = repository.findOne(topicId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;

        if (nurseService.existNurse(creatorId)) {
            if (creatorId!=entity.getCreatorId()) {
                entity.setCreatorId(creatorId);
                changed = true;
            }
        }
        UserType userType = UserType.parseString(creatorType);
        if (null!=userType) {
            if (!userType.equals(entity.getCreatorType())) {
                entity.setCreatorType(userType);
                changed = true;
            }
        }

        if (!VerifyUtil.isStringEmpty(label)) {
            label = label.trim();
            if (!label.equals(entity.getLabel())) {
                entity.setLabel(label);
                changed = true;
            }
        }

        if (!VerifyUtil.isStringEmpty(taxonomy)) {
            taxonomy = taxonomy.trim();
            if (!taxonomy.equals(entity.getTaxonomy())) {
                entity.setTaxonomy(taxonomy);
                changed = true;
            }
        }

        if (!VerifyUtil.isStringEmpty(description)) {
            description = description.trim();
            if (!description.equals(entity.getDescription())) {
                entity.setDescription(description);
                changed = true;
            }
        }

        if (regionRepository.exists(province)) {
            if (province!=entity.getProvince()) {
                entity.setProvince(province);
                changed = true;
            }
        }

        CommonStatus status  = CommonStatus.parseString(strStatus);
        if (null!=status) {
            if (!status.equals(entity.getStatus())) {
                entity.setStatus(status);
                changed = true;
            }
        }

        if (clickNumberIncrement>0) {
            long clickNum = clickNumberIncrement + entity.getClickNumber();
            entity.setClickNumber(clickNum);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        NurseSpeakTopicBean bean = beanConverter.convert(entity);
        logger.info("update nurse speak topic is {}", bean);
        return bean;
    }
}
