package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
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
import java.util.*;

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
    @Autowired private NurseSpeakTopicRelationService topicRelationService;
    @Autowired private NurseSpeakService speakService;
    @Autowired private NurseService nurseService;
    @Autowired private RegionRepository regionRepository;
    @Autowired private UserFileStorageService storageService;


    //=====================================================================
    //          get
    //=====================================================================
    public NurseSpeakTopicBean getTopicByTile(String title) {
        logger.info("get topic by title={}", title);
        List<String> titles = Arrays.asList(new String[]{title});
        List<NurseSpeakTopicBean> topics = getTopicByTitle(titles);
        logger.info("topic is {}", topics);
        int count = topics.size();
        if (count>0) {
            if (count>1) {
                logger.info("there is more than one topics for title");
            }
            return topics.get(0);
        }
        return null;
    }

    public long countTopic(String titleLike, String strStatus) {
        logger.info("count topic by title={} and status={}", titleLike, strStatus);
        if (VerifyUtil.isStringEmpty(titleLike)) {
            titleLike = null;
        }
        else {
            titleLike = VerifyUtil.reconstructSQLContentLike(titleLike.trim());
        }
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = repository.countByTitleAndStatus(titleLike, status);
        logger.info("count is {}", count);
        return count;
    }

    public List<NurseSpeakTopicBean> getTopic(String titleLike, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get topic by title={} and status={}", titleLike, strStatus);
        if (VerifyUtil.isStringEmpty(titleLike)) {
            titleLike = null;
        }
        else {
            titleLike = VerifyUtil.reconstructSQLContentLike(titleLike.trim());
        }
        CommonStatus status = CommonStatus.parseString(strStatus);

        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseSpeakTopicEntity> resultSet = repository.findByTitleAndStatus(titleLike, status, page);
        List<NurseSpeakTopicBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);

        logger.info("count is {}", beans.size());
        return beans;
    }

    public long countUsersInTopic(long topicId, String strUserAuthority) {
        logger.info("count users in topic={} relationStatus={}");
        long count = topicRelationService.countUserTakePartIn(topicId, strUserAuthority);
        logger.info("count is {}", count);
        return count;
    }

    public List<NurseBean> getUsersInTopic(long topicId, String strUserAuthority, int pageIndex, int sizePerPage) {
        logger.info("get user information in topic={} userAuthority={} at page={} sizePerPage={}",
                topicId, strUserAuthority, pageIndex, sizePerPage);
        List<Long> usersId = topicRelationService.getUserTakePartIn(topicId, strUserAuthority, pageIndex, sizePerPage);
        List<NurseBean> sortedUserInTopic = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(usersId)) {
            List<NurseBean> usersInTopic = nurseService.getNurse(usersId);
            for (Long userId : usersId) {
                for (NurseBean user : usersInTopic) {
                    if (user.getId()==userId) {
                        sortedUserInTopic.add(user);
                    }
                }
            }
        }
        logger.info("count is {}", sortedUserInTopic.size());
        return sortedUserInTopic;
    }

    public long countSpeaksInTopic(long topicId, String speakStatus) {
        logger.info("count speaks in topic={} speakStatus={}", topicId, speakStatus);
        long count = topicRelationService.countSpeaksInTopic(topicId, speakStatus);
        logger.info("count is {}", count);
        return count;
    }

    public List<NurseSpeakBean> getSpeaksInTopic(long topicId, String speakStatus, int page, int size) {
        logger.info("get speaks in topic={} speakStatus={} at page={} size={}", topicId, speakStatus, page, size);
        List<Long> speaksId = topicRelationService.getSpeaksIdInTopic(topicId, speakStatus, page, size);
        List<NurseSpeakBean> sortedSpeaksInTopic = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(speaksId)) {
            List<NurseSpeakBean> speaksInTopic = speakService.getSpeakByIds(speaksId, true);
            for (Long speakId : speaksId) {
                for (NurseSpeakBean speak : speaksInTopic) {
                    if (speak.getId()==speakId) {
                        sortedSpeaksInTopic.add(speak);
                    }
                }
            }
        }
        logger.info("count is {}", sortedSpeaksInTopic.size());
        return sortedSpeaksInTopic;
    }

    public Map<Long, List<NurseSpeakTopicBean>> getTopicsBySpeakIds(List<Long> speaksId, String topicStatus) {
        logger.info("get topic info by speaksId={} topicStatus={}", speaksId, topicStatus);
        Map<Long, List<Long>> speakId2TopicIds = topicRelationService.getTopicIdsBySpeakIds(speaksId, topicStatus);
        List<NurseSpeakTopicBean> allTopics = getTopics(speakId2TopicIds);

        Set<Long> keys = speakId2TopicIds.keySet();
        Map<Long, List<NurseSpeakTopicBean>> retVal = new HashMap<>();
        for (Long key : keys) {
            List<Long> topicIds = speakId2TopicIds.get(key);
            List<NurseSpeakTopicBean> topicInSpeak = retVal.get(key);
            if (null==topicInSpeak) {
                topicInSpeak = new ArrayList<>();
                retVal.put(key, topicInSpeak);
            }
            if (!VerifyUtil.isListEmpty(topicIds)) {
                for (Long topicId : topicIds) {
                    for (NurseSpeakTopicBean bean : allTopics) {
                        if (bean.getId()==topicId) {
                            topicInSpeak.add(bean);
                            break;
                        }
                    }
                }
            }
        }
        logger.info("count is {}", retVal.size());
        return retVal;
    }

    private List<NurseSpeakTopicBean> getTopics(Map<Long, List<Long>> speakId2TopicIds) {
        Collection<List<Long>> topicIdsCollection = speakId2TopicIds.values();
        List<Long> allTopicIds = new ArrayList<>();
        for (List<Long> topicIds : topicIdsCollection) {
            for (Long topicId : topicIds) {
                if (null!=topicId && !allTopicIds.contains(topicId)) {
                    allTopicIds.add(topicId);
                }
            }
        }
        List<NurseSpeakTopicEntity> entities = repository.findAll(allTopicIds);
        List<NurseSpeakTopicBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        return beans;
    }

    private List<NurseSpeakTopicBean> getTopicByTitle(List<String> titles) {
        logger.info("get topic by title={}", titles);
        if (VerifyUtil.isListEmpty(titles)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<String> titlesTrim = new ArrayList<>();
        for (String title : titles) {
            if (VerifyUtil.isStringEmpty(title)) {
                continue;
            }
            titlesTrim.add(title.trim());
        }

        List<NurseSpeakTopicEntity> topicEntities = repository.findByTitleIn(titlesTrim, sort);
        if (!VerifyUtil.isListEmpty(topicEntities)) {
            List<NurseSpeakTopicBean> beans = entitiesToBeans(topicEntities);
            fillOtherProperties(beans);
            return beans;
        }
        return new ArrayList<>();
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
            if (!imageIds.contains((bean.getBackgroundImageId()))) {
                imageIds.add(bean.getBackgroundImageId());
            }
        }

        Map<Long, String> imageId2Url = storageService.getFilePath(imageIds);
        List<NurseBean> users = nurseService.getNurse(userIds);
        boolean userNull = VerifyUtil.isListEmpty(users);
        for (NurseSpeakTopicBean bean : beans) {
            long imageId = bean.getProfileImageId();
            String imageUrl = imageId2Url.get(imageId);
            bean.setProfileImageUrl(imageUrl);

            imageId = bean.getBackgroundImageId();
            imageUrl = imageId2Url.get(imageId);
            bean.setBackgroundImageUrl(imageUrl);

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
    public List<NurseSpeakTopicBean> addSpeakTopicsBySpeakContent(long userId, String userType, long speakId, String speakContent) {
        logger.info("add speak topic by speak content={} speakId={} userId={} userType={}",
                speakContent, speakId, userId, userType);
        List<String> topicsTitle = VerifyUtil.parseSpeakTopic(speakContent);
        if (VerifyUtil.isListEmpty(topicsTitle)) {
            return new ArrayList<>();
        }
        List<NurseSpeakTopicBean> topics = getTopicByTitle(topicsTitle);

        List<NurseSpeakTopicBean> allTopics = new ArrayList<>();
        List<Long> allTopicsId = new ArrayList<>();
        for (String title : topicsTitle) {
            NurseSpeakTopicBean exist = null;
            for (NurseSpeakTopicBean topic : topics) {
                if (title.equals(topic.getTitle())) {
                    allTopics.add(topic);
                    exist = topic;
                    break;
                }
            }
            if (null==exist) {
                exist = addSpeakTopic(userId, userType, title, "", "", "", 0);
                allTopics.add(exist);
                logger.info("new topic {} with userId={} userType={} title={}", exist.getId(), userId, userType, title);
            }
            if (null!=exist && !allTopicsId.contains(exist.getId())) {
                allTopicsId.add(exist.getId());
            }
        }
        if (!VerifyUtil.isListEmpty(allTopicsId)) {
            logger.info("set topic relation userId={} speakId={} topicIds={}", userId, speakId, allTopicsId);
            topicRelationService.addTopicRelation(allTopicsId, speakId, userId);
        }
        logger.info("relation count is {}", allTopics.size());
        return allTopics;
    }

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
            if (creatorId==-1) {
                // admin user speak
            }
            else {
                creatorId = 0;
            }
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
    public NurseSpeakTopicBean updateTopicPhoto(long topicId, boolean checkCreator, long creatorId,
                                                String profileImageName, InputStream profileImage,
                                                String backgroundImageName, InputStream backgroundImage) {
        logger.info("update nurse speak topic {} by user={} profile photo name={} image={} and background photo name={} image",
                topicId, creatorId, profileImageName, profileImage!=null, backgroundImageName, null!=backgroundImage);
        NurseSpeakTopicEntity entity = repository.findOne(topicId);
        if (null!=entity ) {
            if (checkCreator && entity.getCreatorId()!=creatorId) {
                logger.info("user is not the creator");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
            String imageUrl = "";
            long imageId = 0;
            NurseSpeakTopicBean bean = beanConverter.convert(entity);

            if (null != profileImage) {
                if (VerifyUtil.isStringEmpty(profileImageName)) {
                    profileImageName = "topic_" + System.currentTimeMillis();
                }
                imageId = storageService.addFile(bean.getProfileImageId(), profileImageName, profileImage);

                if (imageId > 0) {
                    imageUrl = storageService.getFilePath(imageId);
                    entity.setProfileImageId(imageId);
                    entity = repository.save(entity);
                }
                bean.setProfileImageId(imageId);
                bean.setProfileImageUrl(imageUrl);
            }

            imageUrl = "";
            imageId = 0;
            if (null != backgroundImage) {
                if (VerifyUtil.isStringEmpty(backgroundImageName)) {
                    backgroundImageName = "topic_" + System.currentTimeMillis();
                }
                imageId = storageService.addFile(bean.getBackgroundImageId(), backgroundImageName, backgroundImage);

                if (imageId > 0) {
                    imageUrl = storageService.getFilePath(imageId);
                    entity.setBackgroundImageId(imageId);
                    entity = repository.save(entity);
                }
                bean.setBackgroundImageId(imageId);
                bean.setBackgroundImageUrl(imageUrl);
            }
            logger.info("update speak topic profile photo is {}", bean);
        }
        logger.info("update speak topic profile photo is not exist");
        return null;
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

        if (nurseService.existNurse(creatorId) || creatorId==-1/*official speaker*/) {
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
