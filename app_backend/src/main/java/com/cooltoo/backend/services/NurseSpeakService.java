package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.converter.NurseSpeakConverter;
import com.cooltoo.backend.converter.social_ability.SpeakAbilityTypeConverter;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.backend.repository.NurseSpeakRepository;
import com.cooltoo.beans.OfficialConfigBean;
import com.cooltoo.beans.SensitiveWordBean;
import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SensitiveWordType;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.constants.UserType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.OfficialConfigService;
import com.cooltoo.services.SensitiveWordService;
import com.cooltoo.util.NumberUtil;
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
 * Created by yzzhao on 3/15/16.
 */
@Service("NurseSpeakService")
public class NurseSpeakService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakService.class.getName());

    @Autowired private SensitiveWordService sensitiveWordService;
    @Autowired private NurseService nurseService;
    @Autowired private NurseRelationshipService nurseRelationshipService;
    @Autowired private NurseSpeakRepository speakRepository;
    @Autowired private NurseSpeakConverter speakConverter;
    @Autowired private NurseSpeakCommentService speakCommentService;
    @Autowired private NurseSpeakThumbsUpService thumbsUpService;
    @Autowired private SpeakTypeService speakTypeService;
    @Autowired private ImagesInSpeakService speakImageService;
    @Autowired private OfficialConfigService officialConfigService;
    @Autowired private SpeakAbilityTypeConverter speakAbilityTypeConverter;
    @Autowired private NurseSpeakTopicService topicService;
    @Autowired private VideoInSpeakService speakVideoService;

    //===============================================================
    //             get ----  admin using
    //===============================================================
    public long countByContentAndTime(String speakType, String strStatus, long userId, String content, String strStartTime, String strEndTime) {
        logger.info("count user={} speak by content={} startTime={} endTime={}", userId>0?userId:"ALL", content, strStartTime, strEndTime);
        content = VerifyUtil.reconstructSQLContentLike(content);
        long startTime = NumberUtil.getTime(strStartTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        long endTime   = NumberUtil.getTime(strEndTime,   NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        Date start     = startTime<0 ? new Date(0) : new Date(startTime);
        Date end       = endTime  <0 ? new Date()  : new Date(endTime);

        SpeakType speaktype = SpeakType.parseString(speakType);
        SpecificSocialAbility speakTypeAbility = speakAbilityTypeConverter.getItem(speaktype);
        int speakTypeId = null==speakTypeAbility ? 0 : speakTypeAbility.getAbilityId();

        CommonStatus status = CommonStatus.parseString(strStatus);

        long count;
        if (userId==0 || userId<-1) {
            count = speakRepository.countBySpeakTypeAndContentAndTime(speakTypeId, status, content, start, end);
        }
        else {
            count = speakRepository.countByUserIdContentAndTime(userId, content, start, end);
        }
        logger.info("count={}", count);
        return count;
    }

    public List<NurseSpeakBean> getSpeakByContentLikeAndTime(String speakType, String strStatus, long userId, String contentLike, String strStartTime, String strEndTime, int pageIndex, int sizePerPage) {
        logger.info("get user={} speak by speakType={} content={} startTime={} endTime={}", userId>0?userId:"ALL", contentLike, strStartTime, strEndTime);
        contentLike = VerifyUtil.reconstructSQLContentLike(contentLike);
        long startTime = NumberUtil.getTime(strStartTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        long endTime   = NumberUtil.getTime(strEndTime,   NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        Date start     = startTime<0 ? new Date(0) : new Date(startTime);
        Date end       = endTime  <0 ? new Date()  : new Date(endTime);

        SpeakType speaktype = SpeakType.parseString(speakType);
        SpecificSocialAbility speakTypeAbility = speakAbilityTypeConverter.getItem(speaktype);
        int speakTypeId = null==speakTypeAbility ? 0 : speakTypeAbility.getAbilityId();

        CommonStatus status = CommonStatus.parseString(strStatus);

        PageRequest page = new PageRequest(pageIndex, sizePerPage, Sort.Direction.DESC, "time");
        Page<NurseSpeakEntity> speaks;
        if (userId==0 || userId<-1) {
            speaks = speakRepository.findBySpeakTypeAndContentAndTime(speakTypeId, status, contentLike, start, end, page);
        }
        else {
            speaks = speakRepository.findByUserIdContentAndTime(userId, contentLike, start, end, page);
        }
        List<NurseSpeakBean> speakBeans = entitiesToBeans(speaks);
        fillOtherProperties(userId, speakBeans, true);
        logger.info("speak count is ={}", speakBeans.size());
        return speakBeans;
    }

    public List<NurseSpeakBean> getSpeakByIds(List<Long> speakIds, boolean fillTopics) {
        logger.info("get speak by speak ids={}", speakIds);
        List<NurseSpeakEntity> resultSet = speakRepository.findAll(speakIds);
        List<NurseSpeakBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(0, beans, fillTopics);
        logger.info("count is {}", beans.size());
        return beans;
    }

    //===============================================================
    //             get ----  nurse using
    //===============================================================
//    public long countSortSpeakBySpeakType(List<Long> speakIds, int speakTypeId) {
//        if (VerifyUtil.isListEmpty(speakIds)) {
//            return 0;
//        }
//        logger.info("sort speak(count={}) by speak type={}", speakIds.size(), speakTypeId);
//        long countSortedSpeakIds = speakRepository.countSortSpeakByTypeAndStatus(speakIds, speakTypeId, CommonStatus.ENABLED);
//        logger.info("sort speak count={}", countSortedSpeakIds);
//        return countSortedSpeakIds;
//    }
//
//    public Map<Long, Long> countByUserIds(String strUserIds){
//        logger.info("get nurse {} speak count", strUserIds);
//
//        if (VerifyUtil.isIds(strUserIds)) {
//            List<Long> userIds = VerifyUtil.parseLongIds(strUserIds);
//            return countByUserIds(userIds);
//        }
//        return new HashMap<>();
//    }

    public Map<Long, Long> countByUserIds(List<Long> userIds){
        if (null==userIds || userIds.isEmpty()) {
            return new HashMap<>();
        }
        logger.info("get nurse {} speak count", userIds);
        List<Object[]>  count  = speakRepository.countByUserIdInAndStatusNot(userIds, CommonStatus.DELETED);
        Map<Long, Long> id2num = new HashMap<>();
        for(int i=0; i<count.size(); i++) {
            Object[] tmp = count.get(i);
            logger.info("index array is {}--{}", i, tmp[0], tmp[1]);
            id2num.put((Long)tmp[0], (Long)tmp[1]);
        }
        logger.info("get nurse {} speak count {}", userIds, count);
        return id2num;
    }

    public long countSpeak(boolean useUserId, long userId, String strSpeakTypes) {
        logger.info("useUserId={} (user {}) get speak count by speak type {}", useUserId, userId, strSpeakTypes);
        List<SpeakType> speakTypes = VerifyUtil.parseSpeakTypes(strSpeakTypes);
        if (VerifyUtil.isListEmpty(speakTypes)) {
            logger.warn("speak type parsed is empty");
            return 0;
        }
        List<Integer> speakTypeIds = speakTypeService.getSpeakTypeIdByTypes(speakTypes);
        if (VerifyUtil.isListEmpty(speakTypeIds)) {
            logger.warn("speak type parsed ids is empty");
            return 0;
        }

        List<Long> denyUserIds = denyOrBlockSpeakUserIds(userId);
        long count;
        if (useUserId) {
            count = speakRepository.countSpecialTypeSpeakAndStatusNot(userId, speakTypeIds, CommonStatus.DELETED, denyUserIds);
        }
        else {
            count = speakRepository.countSpecialTypeSpeakAndStatusNot(speakTypeIds, CommonStatus.DELETED, denyUserIds);
        }
        logger.info("speak count is={}", count);
        return count;
    }

    public List<NurseSpeakBean> getSpeak(boolean useUserId, long userId, String strSpeakTypes, int pageIndex, int number) {
        logger.info("useUserId={} (user {}) get speak by speak type {} at page {}, {}/page",
                useUserId, userId, strSpeakTypes, pageIndex, number);
        List<SpeakType> speakTypes = VerifyUtil.parseSpeakTypes(strSpeakTypes);
        if (VerifyUtil.isListEmpty(speakTypes)) {
            logger.warn("speak type parsed is empty");
            return new ArrayList<>();
        }
        List<Integer> speakTypeIds = speakTypeService.getSpeakTypeIdByTypes(speakTypes);
        if (VerifyUtil.isListEmpty(speakTypeIds)) {
            logger.warn("speak type parsed ids is empty");
            return new ArrayList<>();
        }

        PageRequest request = new PageRequest(pageIndex, number, Sort.Direction.DESC, "time");
        Page<NurseSpeakEntity> resultSet;

        List<Long> denyUserId = denyOrBlockSpeakUserIds(userId);
        if (useUserId) {
            resultSet = speakRepository.findSpecialTypeSpeakAndStatusNot(userId, speakTypeIds, CommonStatus.DELETED, denyUserId, request);
        }
        else {
            resultSet = speakRepository.findSpecialTypeSpeakAndStatusNot(speakTypeIds, CommonStatus.DELETED, denyUserId, request);
        }

        // parse entities to bean
        List<NurseSpeakBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(userId, beans, true);
        logger.warn("speak count={}", beans.size());
        return beans;
    }

    public List<NurseSpeakBean> getNurseSpeak(long userId, long speakId) {
        logger.info("user {} get speak by id={}", userId, speakId);
        NurseSpeakEntity resultSet = speakRepository.findOne(speakId);
        if (null==resultSet) {
            logger.info("there is no record");
            return null;
        }
        List<NurseSpeakEntity> entities = new ArrayList<>();
        entities.add(resultSet);

        List<NurseSpeakBean> nurseSpeaks = entitiesToBeans(entities);
        fillOtherProperties(userId, nurseSpeaks, true);
        return nurseSpeaks;
    }

    public boolean existsSpeak(long speakId) {
        return speakRepository.exists(speakId);
    }

    public List<Long> denyOrBlockSpeakUserIds(long userId) {
        List<Long> denyUserIds = nurseService.getAllDenyNurseIds();
        List<Long> blockSpeakUserIds = nurseRelationshipService.getUserBlockSpeakUserIds(userId);
        if (!VerifyUtil.isListEmpty(blockSpeakUserIds)) {
            for (Long blockSpeakUserId : blockSpeakUserIds) {
                denyUserIds.add(blockSpeakUserId);
            }
        }
        if (VerifyUtil.isListEmpty(denyUserIds)) {
            denyUserIds.add(0L);
        }
        return denyUserIds;
    }

    private List<NurseSpeakBean> entitiesToBeans(Iterable<NurseSpeakEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<NurseSpeakBean> beans = new ArrayList<>();
        for(NurseSpeakEntity tmp : entities) {
            NurseSpeakBean bean = speakConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(long userId, List<NurseSpeakBean> beans, boolean fillTopic) {
        if (null==beans || beans.isEmpty()) {
            return;
        }
        List<Long>                speakIds         = new ArrayList<>();
        Map<Long, NurseSpeakBean> speakIdToBeanMap = new HashMap<>();
        for (NurseSpeakBean tmp : beans) {
            speakIdToBeanMap.put(tmp.getId(), tmp);
            speakIds.add(tmp.getId());
        }

        //
        // get username and profile photo path
        //
        // speak ids/file ids cache
        List<Long> userIds = new ArrayList<>();

        SpeakTypeBean        cathartSpeak  = speakTypeService.getSpeakTypeByType(SpeakType.CATHART);
        SpeakTypeBean        officialSpeak = speakTypeService.getSpeakTypeByType(SpeakType.OFFICIAL);
        Map<Long, NurseBean> userId2Name   = new HashMap<>();
        userIds.add(userId);
        for(NurseSpeakBean tmp : beans){
            if (userIds.contains(tmp.getUserId())) {
                continue;
            }
            userIds.add(tmp.getUserId());
        }
        List<NurseBean> users = nurseService.getNurse(userIds);
        for (NurseBean tmp : users) {
            userId2Name.put(tmp.getId(), tmp);
        }
        // set user name and profileUrl
        OfficialConfigBean officialSpeakProfile = officialConfigService.getConfig(OfficialConfigService.OFFICIAL_SPEAK_PROFILE);
        for (NurseSpeakBean tmp : beans) {
            NurseBean nurse = userId2Name.get(tmp.getUserId());
            if (null!=nurse) {
                tmp.setUserName(nurse.getName());
                tmp.setUserProfilePhotoUrl(nurse.getProfilePhotoUrl());
            }
            if (null!=officialSpeakProfile && tmp.getSpeakType()==officialSpeak.getId()) {
                String officialPhotoUrl = officialSpeakProfile.getImageUrl();
                tmp.setUserProfilePhotoUrl(officialPhotoUrl);
                tmp.setUserName(officialSpeakProfile.getValue());
            }
            if (tmp.getSpeakType()==cathartSpeak.getId()) {
                tmp.setUserName(tmp.getAnonymousName());
            }
        }

        NurseSpeakBean speakBean = null;

        //
        // get comment of speak
        //
        OfficialConfigBean forbiddenComment  = officialConfigService.getConfig(OfficialConfigService.SUBSTITUTION_OF_FORBIDDEN_COMMENT);
        List<NurseSpeakCommentBean> comments = speakCommentService.getSpeakCommentsByNurseSpeakIds(speakIds);
        for (NurseSpeakCommentBean tmp : comments) {
            long speakId = tmp.getNurseSpeakId();
            speakBean = speakIdToBeanMap.get(speakId);

            List<NurseSpeakCommentBean> mapValue = speakBean.getComments();
            if (null==mapValue) {
                mapValue = new ArrayList<>();
                speakBean.setComments(mapValue);
            }
            // is current user made
            tmp.setIsCurrentUserMade((userId>0 && tmp.getCommentMakerId()==userId));

            // comment is forbidden by admin
            if (null!=forbiddenComment && CommonStatus.DISABLED.equals(tmp.getStatus())) {
                tmp.setComment(forbiddenComment.getValue());
            }

            mapValue.add(tmp);
        }

        //
        // get thumbsUp of speak
        //
        List<NurseSpeakThumbsUpBean> thumbsUps = thumbsUpService.getSpeakThumbsUpByNurseSpeakIds(speakIds);
        for (NurseSpeakThumbsUpBean tmp : thumbsUps) {
            long speakId = tmp.getNurseSpeakId();
            speakBean = speakIdToBeanMap.get(speakId);

            List<NurseSpeakThumbsUpBean> mapValue = speakBean.getThumbsUps();
            if (null==mapValue) {
                mapValue = new ArrayList<>();
                speakBean.setThumbsUps(mapValue);
            }

            mapValue.add(tmp);
        }

        //
        // get image url of speak
        //
        OfficialConfigBean forbiddenSpeak = officialConfigService.getConfig(OfficialConfigService.SUBSTITUTION_OF_FORBIDDEN_SPEAK);
        List<ImagesInSpeakBean> forbiddenImages = new ArrayList<>();
        if (null!=forbiddenSpeak) {
            ImagesInSpeakBean forbiddenImage = new ImagesInSpeakBean();
            forbiddenImage.setId(0);
            forbiddenImage.setImageId(forbiddenSpeak.getImageId());
            forbiddenImage.setImageUrl(forbiddenSpeak.getImageUrl());
            forbiddenImage.setSpeakId(0);
            forbiddenImage.setTimeCreated(forbiddenSpeak.getCreateTime());
            forbiddenImages.add(forbiddenImage);
        }
        SpeakTypeBean                      cathartType = speakTypeService.getSpeakTypeByType(SpeakType.CATHART);
        Map<Long, List<ImagesInSpeakBean>> idToImage = speakImageService.getImagesInSpeak(speakIds);
        for (NurseSpeakBean tmp : beans) {
            long                    speakId = tmp.getId();
            List<ImagesInSpeakBean> images  = idToImage.get(speakId);
            tmp.setImages(images);
            if (tmp.getSpeakType()==cathartType.getId()) {
                if (!VerifyUtil.isListEmpty(images)) {
                    tmp.setUserProfilePhotoUrl(images.get(0).getImageUrl());
                }
            }
            if (null!=forbiddenSpeak && CommonStatus.DISABLED.equals(tmp.getStatus())) {
                tmp.setContent(forbiddenSpeak.getValue());
                tmp.setImages(forbiddenImages);
            }
        }

        //
        //   fill topics
        //
        if (fillTopic) {
            String topicStatus = CommonStatus.ENABLED.name();
            Map<Long, List<NurseSpeakTopicBean>> speakId2Topics = topicService.getTopicsBySpeakIds(speakIds, topicStatus);
            for (NurseSpeakBean tmp : beans) {
                if (!CommonStatus.ENABLED.equals(tmp.getStatus())) {
                    continue;
                }
                long speakId = tmp.getId();
                List<NurseSpeakTopicBean> topics  = speakId2Topics.get(speakId);
                if (!VerifyUtil.isListEmpty(topics)) {
                    tmp.setTopics(topics);
                }
            }
        }

        // fill video of speak
        Map<Long, List<VideoInSpeakBean>> speakId2Videos = speakVideoService.getVideoInSpeak(speakIds);
        if (!VerifyUtil.isMapEmpty(speakId2Videos)) {
            for (NurseSpeakBean tmp : beans) {
                List<VideoInSpeakBean> videos = speakId2Videos.get(tmp.getId());
                if (!VerifyUtil.isListEmpty(videos)) {
                    tmp.setVideos(videos);
                }
            }
        }

        // construct return values
        List<Object> countTarget = null;
        for (NurseSpeakBean tmp : beans) {
            long speakId = tmp.getId();
            speakBean    = speakIdToBeanMap.get(speakId);
            countTarget  = (List)speakBean.getComments();
            speakBean.setCommentsCount(null == countTarget ? 0 : countTarget.size());
            countTarget  = (List)speakBean.getThumbsUps();
            speakBean.setThumbsUpsCount(null == countTarget ? 0 : countTarget.size());
        }
        return;
    }


    //===============================================================
    //             add
    //===============================================================

    public NurseSpeakBean addSmug(long userId, String content, String fileName, InputStream file) {
        logger.info("add SMUG : userId=" + userId + " content="+content+" fileName="+fileName);
        return addNurseSpeak(userId, content, SpeakType.SMUG.name(), "", fileName, file);
    }

    public NurseSpeakBean addCathart(long userId, String content, String anonymousName, String fileName, InputStream file) {
        logger.info("add CATHART : userId=" + userId + " content="+content+" fileName="+fileName);
        return addNurseSpeak(userId, content, SpeakType.CATHART.name(), anonymousName, fileName, file);
    }

    public NurseSpeakBean addAskQuestion(long userId, String content, String fileName, InputStream file) {
        logger.info("add ASK_QUESTION : userId=" + userId + " content="+content+" fileName="+fileName);
        return addNurseSpeak(userId, content, SpeakType.ASK_QUESTION.name(), "", fileName, file);
    }

    public NurseSpeakBean addOfficial(long userId, String content, String fileName, InputStream file) {
        logger.info("add OFFICIAL : userId=" + userId + " content="+content+" fileName="+fileName);
        return addNurseSpeak(userId, content, SpeakType.OFFICIAL.name(), "", fileName, file);
    }

    public NurseSpeakBean addShortVideo(long userId, String content) {
        logger.info("add SHORT_VIDEO : userId=" + userId + " content="+content);
        return addNurseSpeak(userId, content, SpeakType.SHORT_VIDEO.name(), "", null, null);
    }

    @Transactional
    private NurseSpeakBean addNurseSpeak(long userId, String content, String strSpeakType, String anonymousName, String imageName, InputStream image) {
        logger.info("add nurse speak with userId={} speakType={} content={} imageName={} image={}", userId, strSpeakType, content, imageName, (null!=image));
        judgeSensitiveWord(content);

        // check speak type
        SpeakType     speakType     = SpeakType.parseString(strSpeakType);
        SpeakTypeBean speakTypeBean = speakTypeService.getSpeakTypeByType(speakType);
        if (null==speakTypeBean) {
            logger.error("speak type is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        NurseSpeakEntity entity = new NurseSpeakEntity();
        // check speak content
        if (!SpeakType.SMUG.equals(speakType) && !SpeakType.SHORT_VIDEO.equals(speakType)) {
            if (VerifyUtil.isStringEmpty(content)) {
                logger.error("speak content is empty");
                throw new BadRequestException(ErrorCode.SPEAK_CONTENT_IS_EMPTY);
            }
        }
        else {
            if (SpeakType.SMUG.equals(speakType) && VerifyUtil.isStringEmpty(content) && null==image){
                logger.error("smug's content and image is empty ");
                throw new BadRequestException(ErrorCode.SPEAK_CONTENT_IS_EMPTY);
            }
        }
        if (null==anonymousName) {
            anonymousName = "";
        }
        entity.setAnonymousName(anonymousName);
        entity.setUserId(userId);
        entity.setContent(content);
        entity.setSpeakType(speakTypeBean.getId());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = speakRepository.save(entity);

        ImagesInSpeakBean imageInSpeak = null;
        if (null==image) {
            logger.info("there is no image file need to insert!");
        }
        else {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = speakType.name();
            }
            imageInSpeak = addImage(userId, entity.getId(), imageName, image);
        }

        NurseSpeakBean bean = speakConverter.convert(entity);
        if (null!=imageInSpeak) {
            List<ImagesInSpeakBean> images = new ArrayList<>();
            images.add(imageInSpeak);
            bean.setImages(images);
        }
        List<NurseSpeakTopicBean> topics = topicService.addSpeakTopicsBySpeakContent(userId, UserType.NURSE.name(), bean.getId(), bean.getContent());
        bean.setTopics(topics);

        return bean;
    }

    public ImagesInSpeakBean addImage(long userId, long speakId, String imageName, InputStream image) {
        logger.info("user {} add image to speak={} image name={} image={}", userId, speakId, imageName, (null!=image));

        // check speak
        NurseSpeakEntity speakEntity   = speakRepository.findOne(speakId);
        if (null==speakEntity) {
            logger.error("nurse speak is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (userId!=speakEntity.getUserId()) {
            logger.error("user can not modify other's speak");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        // check speak type
        SpeakTypeBean    speakTypeBean = speakTypeService.getSpeakType(speakEntity.getSpeakType());
        if (null==speakTypeBean) {
            logger.error("speak type is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long count = speakImageService.countImagesInSpeak(speakId);
        if (count>=1 && SpeakType.CATHART.equals(speakTypeBean.getType())) {
            logger.warn("the cathart speak do not need a image");
            return new ImagesInSpeakBean();
        }
        else if (count>=1 && SpeakType.SMUG.equals(speakTypeBean.getType())) {
            logger.warn("the smug speak do not need image more than one");
            return new ImagesInSpeakBean();
        }
        else if (count>=9 && SpeakType.ASK_QUESTION.equals(speakTypeBean.getType())) {
            logger.warn("the ask_question speak do not need image more than nine");
            return new ImagesInSpeakBean();
        }
        else if (count>=1 && SpeakType.OFFICIAL.equals(speakTypeBean.getType())) {
            logger.warn("the official speak do not need image more than one");
            return new ImagesInSpeakBean();
        }

        ImagesInSpeakBean imageInSpeakBean = speakImageService.addImage(speakId, imageName, image);
        return imageInSpeakBean;
    }

    //===============================================================
    //             update
    //===============================================================
    public long updateSpeakStatus(String strSpeakIds, String strStatus) {
        logger.info("update speak={} with status={}", strSpeakIds, strStatus);
        if (!VerifyUtil.isIds(strSpeakIds)) {
            logger.error("invalid speak ids");
            return 0;
        }
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            logger.error("status not valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<Long> speakIds = VerifyUtil.parseLongIds(strSpeakIds);
        return updateSpeakStatus(speakIds, status);
    }

    public long updateSpeakStatus(List<Long> speakIds, CommonStatus status) {
        logger.info("update speak={} with status={}", speakIds, status);
        List<NurseSpeakEntity> entities = speakRepository.findAll(speakIds);
        if (VerifyUtil.isListEmpty(entities)) {
            logger.error("speak ids is empty");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (null==status) {
            logger.error("status not valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        for (NurseSpeakEntity entity : entities) {
            entity.setStatus(status);
            speakRepository.save(entity);
        }
        return entities.size();
    }

    //===============================================================
    //             delete
    //===============================================================

    @Transactional
    public List<NurseSpeakBean> deleteByIds(long speakMakerId, String strSpeakIds) {
        logger.info("delete nurse speak by speak ids {}.", strSpeakIds);
        if (!VerifyUtil.isIds(strSpeakIds)) {
            logger.warn("speak ids are invalid");
            return new ArrayList<>();
        }

        List<Long> ids = VerifyUtil.parseLongIds(strSpeakIds);
        return deleteByIds(speakMakerId, ids);
    }

    @Transactional
    public List<NurseSpeakBean> deleteByIds(long speakMakerId, List<Long> speakIds) {
        logger.info("delete nurse speak by speak ids {}.", speakIds);
        if (null==speakIds || speakIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseSpeakEntity> speaks = speakRepository.findAll(speakIds);
        if (null==speaks || speaks.isEmpty()) {
            logger.info("delete nothing");
        }

        for (NurseSpeakEntity speak : speaks) {
            if (speak.getUserId()==speakMakerId) {
                continue;
            }
            logger.warn("can not delete the comment {} not making by yourself {}", speak, speakMakerId);
            return new ArrayList<>();
        }
        for (NurseSpeakEntity speak : speaks) {
            speak.setStatus(CommonStatus.DELETED);
        }
        speakRepository.save(speaks);

        speakCommentService.deleteBySpeakIds(speakIds);
        thumbsUpService.deleteBySpeakIds(speakIds);
        speakImageService.deleteBySpeakIds(speakIds);

        List<NurseSpeakBean> retValue = new ArrayList<>();
        for (NurseSpeakEntity tmp : speaks) {
            NurseSpeakBean comment = speakConverter.convert(tmp);
            retValue.add(comment);
        }
        return retValue;
    }

    public ImagesInSpeakBean deleteImagesInSpeak(long imagesInSpeakId) {
        return speakImageService.deleteById(imagesInSpeakId);
    }

    //=======================================================
    //          Comment service
    //=======================================================

    public NurseSpeakCommentBean addSpeakComment(long speakId, long commentMakerId, long commentReceiverId, String comment) {
        judgeSensitiveWord(comment);
        NurseSpeakCommentBean commentBean = speakCommentService.addSpeakComment(speakId, commentMakerId, commentReceiverId, comment);
        return commentBean;
    }

    public NurseSpeakCommentBean updateSpeakComment(long commentId, String comment, String status) {
        judgeSensitiveWord(comment);
        NurseSpeakCommentBean commentBean = speakCommentService.updateSpeakComment(commentId, comment, status);
        return commentBean;
    }

    public List<NurseSpeakCommentBean> deleteSpeakComment(long userId, String strCommentIds) {
        List<NurseSpeakCommentBean> comments = speakCommentService.getCommentByIds(strCommentIds);

        if (null==comments || comments.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> speakIds = new ArrayList<>();
        List<Long> makerIds = new ArrayList<>();
        for (NurseSpeakCommentBean comment : comments) {
            long speakId = comment.getNurseSpeakId();
            long makerId = comment.getCommentMakerId();
            if (!speakIds.contains(speakId)) {
                speakIds.add(speakId);
            }
            if (!makerIds.contains(makerId)) {
                makerIds.add(makerId);
            }
        }

        // is comment maker
        if (makerIds.size()==1 && userId==makerIds.get(0)) {
            logger.warn("delete by comment maker");
            comments = speakCommentService.deleteByIds(strCommentIds);
            return comments;
        }

        // is speak not exist
        List<NurseSpeakEntity> speaks = speakRepository.findAll(speakIds);
        if (null==speaks || speakIds.isEmpty()) {
            logger.warn("delete as speak not exist");
            comments = speakCommentService.deleteByIds(strCommentIds);
            return comments;
        }

        // is speak maker
        boolean isSpeakMaker = true;
        for (NurseSpeakEntity tmp : speaks) {
            if (tmp.getUserId()==userId) {
                continue;
            }
            isSpeakMaker = false;
        }
        if (isSpeakMaker) {
            logger.warn("delete by speak maker");
            comments = speakCommentService.deleteByIds(strCommentIds);
            return comments;
        }
        logger.warn("can not delete comment");

        return new ArrayList<>();
    }


    //=======================================================
    //          Thumbs up service
    //=======================================================

    @Transactional
    public NurseSpeakThumbsUpBean setNurseSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        NurseSpeakEntity speakEntity = speakRepository.findOne(nurseSpeakId);
        if (null==speakEntity) {
            throw new BadRequestException(ErrorCode.SPEAK_CONTENT_NOT_EXIST);
        }

        // you could not add thumbs_up for yourself
        //if (thumbsUpUserId==speakEntity.getUserId()) {
        //    throw new BadRequestException(ErrorCode.SPEAK_THUMBS_UP_CAN_NOT_FOR_SELF);
        //}

        NurseSpeakThumbsUpBean thumbsUpBean = thumbsUpService.setSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
        return thumbsUpBean;
    }

    public List<NurseFriendsBean> getThumbsUpUsers(long currentUserId, long nurseSpeakId) {
        return thumbsUpService.getThumbsUpUsers(currentUserId, nurseSpeakId);
    }

    public NurseSpeakThumbsUpBean getNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(long nurseSpeakId, long thumbsUpUserId) {
        return thumbsUpService.findNurseSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
    }

    private void judgeSensitiveWord(String content) {
        if (!VerifyUtil.isStringEmpty(content)) {
            content = content.trim();
            List<SensitiveWordBean> sensitiveWords = sensitiveWordService.getWords(SensitiveWordType.ADD_CONTENT_FORBIDDEN.name(), CommonStatus.ENABLED.name());
            for (SensitiveWordBean word : sensitiveWords) {
                if (content.contains(word.getWord())) {
                    logger.info("contains sensitive word={}", word);
                    throw new BadRequestException(ErrorCode.CONTAINS_SENSITIVE_WORD);
                }
            }
        }
    }
}
