package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.converter.NurseSpeakConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.backend.repository.NurseSpeakRepository;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.services.file.UserFileStorageService;
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
 * Created by yzzhao on 3/15/16.
 */
@Service("NurseSpeakService")
public class NurseSpeakService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakService.class.getName());

    @Autowired private NurseService nurseService;
    @Autowired private NurseSpeakRepository speakRepository;
    @Autowired private NurseSpeakConverter speakConverter;
    @Autowired private NurseSpeakCommentService speakCommentService;
    @Autowired private NurseSpeakThumbsUpService thumbsUpService;
    @Autowired private SpeakTypeService speakTypeService;
    @Autowired private ImagesInSpeakService speakImageService;
    @Autowired
    @Qualifier("OfficialFileStorageService")
    private OfficialFileStorageService officialStorage;

    //===============================================================
    //             get
    //===============================================================

    public Map<Long, Long> countByUserIds(String strUserIds){
        logger.info("get nurse {} speak count", strUserIds);

        if (VerifyUtil.isIds(strUserIds)) {
            List<Long> userIds = VerifyUtil.parseLongIds(strUserIds);
            return countByUserIds(userIds);
        }
        return new HashMap<>();
    }

    public Map<Long, Long> countByUserIds(List<Long> userIds){
        if (null==userIds || userIds.isEmpty()) {
            return new HashMap<>();
        }
        logger.info("get nurse {} speak count", userIds);
        List<Object[]>  count  = speakRepository.countByUserIdIn(userIds);
        Map<Long, Long> id2num = new HashMap<>();
        for(int i=0; i<count.size(); i++) {
            Object[] tmp = count.get(i);
            logger.info("index {} array is {}--{}", i, tmp[0], tmp[1]);
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
        long count = 0;
        if (useUserId) {
            count = speakRepository.countSpecialTypeSpeak(userId, speakTypeIds);
        }
        else {
            count = speakRepository.countSpecialTypeSpeak(speakTypeIds);
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
        if (useUserId) {
            resultSet = speakRepository.findSpecialTypeSpeak(userId, speakTypeIds, request);
        }
        else {
            resultSet = speakRepository.findSpecialTypeSpeak(speakTypeIds, request);
        }

        // parse entities to bean
        List<NurseSpeakBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(userId, beans);
        logger.warn("speak count={}", beans.size());
        return beans;
    }

    public NurseSpeakBean getNurseSpeak(long userId, long speakId) {
        logger.info("user {} get speak by id={}", userId, speakId);
        NurseSpeakEntity resultSet = speakRepository.findOne(speakId);
        if (null==resultSet) {
            logger.info("there is no record");
            return null;
        }
        List<NurseSpeakEntity> entities = new ArrayList<>();
        entities.add(resultSet);

        List<NurseSpeakBean> nurseSpeaks = entitiesToBeans(entities);
        fillOtherProperties(userId, nurseSpeaks);
        return nurseSpeaks.get(0);
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

    private void fillOtherProperties(long userId, List<NurseSpeakBean> beans) {
        if (null==beans || beans.isEmpty()) {
            return;
        }
        List<Long>                speakIds         = new ArrayList<Long>();
        Map<Long, NurseSpeakBean> speakIdToBeanMap = new HashMap<>();
        for (NurseSpeakBean tmp : beans) {
            speakIdToBeanMap.put(tmp.getId(), tmp);
            speakIds.add(tmp.getId());
        }

        //
        // get username and profile photo path
        //
        // speak ids/file ids cache
        List<Long> userIds = new ArrayList<Long>();
        List<Long> fileIds = new ArrayList<Long>();

        SpeakTypeBean        officialSpeak = speakTypeService.getSpeakTypeByType(SpeakType.OFFICIAL);
        Map<Long, NurseBean> userId2Name   = new HashMap<>();
        Map<Long, Long>      userId2FileId = new HashMap<Long, Long>();
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
        for (NurseSpeakBean tmp : beans) {
            NurseBean nurse = userId2Name.get(tmp.getUserId());
            if (null!=nurse) {
                tmp.setUserName(nurse.getName());
                tmp.setUserProfilePhotoUrl(nurse.getProfilePhotoUrl());
            }
            if (tmp.getSpeakType()==officialSpeak.getId()) {
                String officialPhotoUrl = officialStorage.getOfficalSpeakProfilePhotoNginxRelativePath();
                tmp.setUserProfilePhotoUrl(officialPhotoUrl);
            }
        }

        NurseSpeakBean speakBean = null;

        //
        // get comment of speak
        //
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
        SpeakTypeBean                      cathartType = speakTypeService.getSpeakTypeByType(SpeakType.CATHART);
        Map<Long, List<ImagesInSpeakBean>> idToImage = speakImageService.getImagesInSpeak(speakIds);
        for (NurseSpeakBean tmp : beans) {
            long                    speakId = tmp.getId();
            List<ImagesInSpeakBean> images  = idToImage.get(speakId);
            speakBean = speakIdToBeanMap.get(speakId);
            speakBean.setImages(images);
            if (speakBean.getSpeakType()==cathartType.getId()) {
                if (!VerifyUtil.isListEmpty(images)) {
                    speakBean.setUserProfilePhotoUrl(images.get(0).getImageUrl());
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
        return addNurseSpeak(userId, content, SpeakType.SMUG.name(), fileName, file);
    }

    public NurseSpeakBean addCathart(long userId, String content, String fileName, InputStream file) {
        logger.info("add CATHART : userId=" + userId + " content="+content+" fileName="+fileName);
        return addNurseSpeak(userId, content, SpeakType.CATHART.name(), fileName, file);
    }

    public NurseSpeakBean addAskQuestion(long userId, String content, String fileName, InputStream file) {
        logger.info("add ASK_QUESTION : userId=" + userId + " content="+content+" fileName="+fileName);
        return addNurseSpeak(userId, content, SpeakType.ASK_QUESTION.name(), fileName, file);
    }

    public NurseSpeakBean addOfficial(long userId, String content, String fileName, InputStream file) {
        logger.info("add SMUG : userId=" + userId + " content="+content+" fileName="+fileName);
        return addNurseSpeak(userId, content, SpeakType.OFFICIAL.name(), fileName, file);
    }

    @Transactional
    private NurseSpeakBean addNurseSpeak(long userId, String content, String strSpeakType, String imageName, InputStream image) {
        logger.info("add nurse speak with userId={} speakType={} content={} imageName={} image={}", userId, strSpeakType, content, imageName, (null!=image));

        // check speak type
        SpeakType     speakType     = SpeakType.parseString(strSpeakType);
        SpeakTypeBean speakTypeBean = speakTypeService.getSpeakTypeByType(speakType);
        if (null==speakTypeBean) {
            logger.error("speak type is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        NurseSpeakEntity entity = new NurseSpeakEntity();
        // check speak content
        if (!SpeakType.SMUG.equals(speakType)) {
            if (VerifyUtil.isStringEmpty(content)) {
                logger.error("speak content is empty");
                throw new BadRequestException(ErrorCode.SPEAK_CONTENT_IS_EMPTY);
            }
        }
        else {
            if (VerifyUtil.isStringEmpty(content) && null==image) {
                logger.error("smug's content and image is empty ");
                throw new BadRequestException(ErrorCode.SPEAK_CONTENT_IS_EMPTY);
            }
        }

        entity.setUserId(userId);
        entity.setContent(content);
        entity.setSpeakType(speakTypeBean.getId());
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

        List<NurseSpeakEntity> speaks = speakRepository.findByIdIn(speakIds);
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

        speakRepository.delete(speaks);
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
        NurseSpeakCommentBean commentBean = speakCommentService.addSpeakComment(speakId, commentMakerId, commentReceiverId, comment);
        //TODO need to transfer the comment to the relative person
        //... ...
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
        List<NurseSpeakEntity> speaks = speakRepository.findByIdIn(speakIds);
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
        if (thumbsUpUserId==speakEntity.getUserId()) {
            throw new BadRequestException(ErrorCode.SPEAK_THUMBS_UP_CAN_NOT_FOR_SELF);
        }

        NurseSpeakThumbsUpBean thumbsUpBean = thumbsUpService.setSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
        //TODO need to transfer this thumbs_up to the relative person

        return thumbsUpBean;
    }

    public List<NurseFriendsBean> getThumbsUpUsers(long currentUserId, long nurseSpeakId) {
        return thumbsUpService.getThumbsUpUsers(currentUserId, nurseSpeakId);
    }

    public NurseSpeakThumbsUpBean getNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(long nurseSpeakId, long thumbsUpUserId) {
        return thumbsUpService.findNurseSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
    }
}
