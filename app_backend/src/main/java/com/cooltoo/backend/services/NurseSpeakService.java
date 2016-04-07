package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.beans.SpeakTypeBean;
import com.cooltoo.backend.converter.NurseSpeakConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseSpeakRepository;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
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

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private NurseSpeakRepository speakRepository;

    @Autowired
    private NurseSpeakConverter speakConverter;

    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    @Autowired
    private NurseSpeakCommentService speakCommentService;

    @Autowired
    private NurseSpeakThumbsUpService thumbsUpService;

    @Autowired
    private SpeakTypeService speakTypeService;

    public long countNurseSpeakByType(long userId, String speakType) {
        logger.info("get nurse speak ("+speakType+") count");
        SpeakType speaktype = SpeakType.parseString(speakType);
        if (null==speaktype) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        SpeakTypeBean speakTypeBean = speakTypeService.getSpeakTypeByType(speaktype);
        if (null==speakTypeBean) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long count = speakRepository.countNurseSpeakByUserIdAndSpeakType(userId, speakTypeBean.getId());
        return count;
    }

    public List<NurseSpeakBean> getNurseSpeak(long userId, int index, int number) {
        logger.info("get nurse speak at "+index+" number="+number);

        // get speaks
        PageRequest request = new PageRequest(index, number, Sort.Direction.DESC, "time");
        Page<NurseSpeakEntity> speaks = speakRepository.findNurseSpeakByUserId(userId, request);

        // move to cache
        List<NurseSpeakEntity> speakEntities = new ArrayList<NurseSpeakEntity>();
        for (NurseSpeakEntity entity : speaks) {
            speakEntities.add(entity);
        }

        // parse entities to bean
        return parseEntities(userId, speakEntities);
    }

    public List<NurseSpeakBean> getSpeakByType(String speakType, int index, int number) {
        logger.info("get all speak ("+speakType+") at "+index+" number="+number);
        SpeakTypeBean speakTypeBean = null;
        SpeakType speaktype = SpeakType.parseString(speakType);
        if (null!=speaktype) {
            speakTypeBean = speakTypeService.getSpeakTypeByType(speaktype);
        }

        // get speaks
        PageRequest pagable = new PageRequest(index, number, Sort.Direction.DESC, "time");
        Page<NurseSpeakEntity> speaks = null;
        if (null!=speakTypeBean) {
            logger.info("get speak type's speaks. type is {}", speakTypeBean);
            speaks = speakRepository.findNurseSpeakBySpeakType(speakTypeBean.getId(), pagable);
        }
        else {
            logger.info("get all speak type's speaks");
            speaks = speakRepository.findAll(pagable);
        }

        // move to cache
        List<NurseSpeakEntity> speakEntities = new ArrayList<NurseSpeakEntity>();
        for (NurseSpeakEntity entity : speaks) {
            speakEntities.add(entity);
        }

        // parse entities to bean
        return parseEntities(-1, speakEntities);
    }

    @Transactional
    public List<NurseSpeakBean> getSpeakByUserIdAndType(long userId, String speakType, int index, int number) {
        logger.info("get nurse speak ("+speakType+") at "+index+" number="+number);
        SpeakType speaktype = SpeakType.parseString(speakType);
        if (null==speaktype) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        SpeakTypeBean speakTypeBean = speakTypeService.getSpeakTypeByType(speaktype);
        if (null==speakTypeBean) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // get speaks
        PageRequest pagable = new PageRequest(index, number, Sort.Direction.DESC, "time");
        Page<NurseSpeakEntity> speaks = speakRepository.findNurseSpeakByUserIdAndSpeakType(userId, speakTypeBean.getId(), pagable);

        // move to cache
        List<NurseSpeakEntity> speakEntities = new ArrayList<NurseSpeakEntity>();
        for (NurseSpeakEntity entity : speaks) {
            speakEntities.add(entity);
        }

        // parse entities to bean
        return parseEntities(userId, speakEntities);
    }

    private List<NurseSpeakBean> parseEntities(long userId, List<NurseSpeakEntity> entities) {
        // speak ids/file ids cache
        List<Long> speakIds = new ArrayList<Long>();
        List<Long> fileIds = new ArrayList<Long>();

        // get username
        Map<Long, String> userId2Name   = new HashMap<Long, String>();
        Map<Long, Long>   userId2FileId = new HashMap<Long, Long>();
        if (userId<0) {
            Iterable<NurseEntity> all = nurseRepository.findAll();
            for (NurseEntity user : all) {
                userId2Name.put(user.getId(), user.getName());
                userId2FileId.put(user.getId(), user.getProfilePhotoId());
                fileIds.add(user.getProfilePhotoId());
            }
        }
        else {
            NurseEntity user = nurseRepository.findOne(userId);
            if (null != user) {
                userId2Name.put(userId, user.getName());
                userId2FileId.put(userId, user.getProfilePhotoId());
                fileIds.add(user.getProfilePhotoId());
            }
        }

        // convert to bean
        Map<Long, NurseSpeakBean> speakIdToBeanMap = new HashMap<Long, NurseSpeakBean>();
        for (NurseSpeakEntity entity : entities) {
            NurseSpeakBean speak = speakConverter.convert(entity);
            String nurseName = userId2Name.get(speak.getUserId());
            speak.setUserName(nurseName);
            speakIdToBeanMap.put(speak.getId(), speak);
            speakIds.add(speak.getId());
            fileIds.add(speak.getImageId());
        }

        NurseSpeakBean bean = null;

        // get comment of speak
        List<NurseSpeakCommentBean> comments = speakCommentService.getSpeakCommentsByNurseSpeakIds(speakIds);
        for (NurseSpeakCommentBean comment : comments) {
            bean = speakIdToBeanMap.get(comment.getNurseSpeakId());
            List<NurseSpeakCommentBean> nurseComment = bean.getComments();
            if (null==nurseComment) {
                nurseComment = new ArrayList<NurseSpeakCommentBean>();
                bean.setComments(nurseComment);
            }
            nurseComment.add(comment);
        }

        // get thumbsUp of speak
        List<NurseSpeakThumbsUpBean> thumbsUps = thumbsUpService.getSpeakThumbsUpByNurseSpeakIds(speakIds);
        for (NurseSpeakThumbsUpBean thumbsUp : thumbsUps) {
            bean = speakIdToBeanMap.get(thumbsUp.getNurseSpeakId());
            List<NurseSpeakThumbsUpBean> speakThumbsUp = bean.getThumbsUps();
            if (null==speakThumbsUp) {
                speakThumbsUp = new ArrayList<NurseSpeakThumbsUpBean>();
                bean.setThumbsUps(speakThumbsUp);
            }
            speakThumbsUp.add(thumbsUp);
        }

        // get image url of speak
        Map<Long, String> idToPath = storageService.getFilePath(fileIds);
        for (NurseSpeakEntity entity : entities) {
            long   speakId    = entity.getId();
            long   imageId    = entity.getImageId();
            long   tmpUserId  = entity.getUserId();
            long   userProfId = userId2FileId.get(tmpUserId);
            String imageUrl   = idToPath.get(imageId);
            String userProUrl = idToPath.get(userProfId);
            NurseSpeakBean speakBean = speakIdToBeanMap.get(speakId);
            speakBean.setImageUrl(imageUrl);
            speakBean.setUserProfilePhotoUrl(userProUrl);
        }

        // construct return values
        List<Object>         countTarget = null;
        List<NurseSpeakBean> speakBeans  = new ArrayList<NurseSpeakBean>();
        for (NurseSpeakEntity entity : entities) {
            long speakId = entity.getId();
            bean         = speakIdToBeanMap.get(speakId);
            countTarget  = (List)bean.getComments();
            bean.setCommentsCount(null==countTarget ? 0 : countTarget.size());
            countTarget  = (List)bean.getThumbsUps();
            bean.setThumbsUpsCount(null==countTarget ? 0 : countTarget.size());
            speakBeans.add(speakIdToBeanMap.get(speakId));
        }
        return speakBeans;
    }

    public NurseSpeakBean getNurseSpeak(long id) {
        NurseSpeakEntity entity = speakRepository.findOne(id);
        NurseSpeakBean speakBean = speakConverter.convert(entity);
        speakBean.setImageUrl(storageService.getFilePath(entity.getImageId()));
        List<NurseSpeakCommentBean> comments = speakCommentService.getSpeakCommentsByNurseSpeakId(speakBean.getId());
        speakBean.setComments(comments);
        speakBean.setCommentsCount(comments.size());
        List<NurseSpeakThumbsUpBean> thumbsUps = thumbsUpService.getSpeakThumbsUpByNurseSpeakId(id);
        speakBean.setThumbsUps(thumbsUps);
        speakBean.setThumbsUpsCount(thumbsUps.size());
        return speakBean;
    }

    public NurseSpeakBean addSmug(long userId, String content, String fileName, InputStream fileInputStream) {
        logger.info("add SMUG : userId=" + userId + " content="+content+" fileName="+fileName);
        // check speak type
        SpeakTypeBean speakType = speakTypeService.getSpeakTypeByType(SpeakType.SMUG);
        if (null==speakType) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        return addNurseSpeak(userId, content, speakType.getId(), fileName, fileInputStream);
    }

    public NurseSpeakBean addCathart(long userId, String content, String fileName, InputStream fileInputStream) {
        logger.info("add CATHART : userId=" + userId + " content="+content+" fileName="+fileName);
        // check speak type
        SpeakTypeBean speakType = speakTypeService.getSpeakTypeByType(SpeakType.CATHART);
        if (null==speakType) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        return addNurseSpeak(userId, content, speakType.getId(), fileName, fileInputStream);
    }

    public NurseSpeakBean addAskQuestion(long userId, String content, String fileName, InputStream fileInputStream) {
        logger.info("add ASK_QUESTION : userId=" + userId + " content="+content+" fileName="+fileName);
        // check speak type
        SpeakTypeBean speakType = speakTypeService.getSpeakTypeByType(SpeakType.ASK_QUESTION);
        if (null==speakType) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        return addNurseSpeak(userId, content, speakType.getId(), fileName, fileInputStream);
    }

    @Transactional
    private NurseSpeakBean addNurseSpeak(long userId, String content, int speakTypeId, String fileName, InputStream fileInputStream) {
        boolean hasImage = false;

        NurseSpeakEntity entity = new NurseSpeakEntity();
        if (null==fileName||"".equals(fileName) || null==fileInputStream) {
            logger.info("there is no image file need to insert!");
        }
        else {
            try {
                long fileID = storageService.saveFile(entity.getImageId(), fileName, fileInputStream);
                entity.setImageId(fileID);
                hasImage = true;
            }
            catch (BadRequestException ex) {
                // do nothing
            }
        }
        // check speak content
        if (null==content || "".equals(content)) {
            throw new BadRequestException(ErrorCode.SPEAK_CONTENT_IS_EMPTY);
        }

        entity.setUserId(userId);
        entity.setContent(content);
        entity.setSpeakType(speakTypeId);
        entity.setTime(new Date());
        entity = speakRepository.save(entity);

        NurseSpeakBean bean = speakConverter.convert(entity);
        if (hasImage) {
            bean.setImageUrl(storageService.getFilePath(entity.getImageId()));
        }

        return bean;
    }

    public long getNurseSpeakCount(long userId){
        return speakRepository.countNurseSpeakByUserId(userId);
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


    //=======================================================
    //          Thumbs up service
    //=======================================================

    @Transactional
    public NurseSpeakThumbsUpBean addNurseSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        NurseSpeakEntity speakEntity = speakRepository.findOne(nurseSpeakId);
        if (null==speakEntity) {
            throw new BadRequestException(ErrorCode.SPEAK_CONTENT_NOT_EXIST);
        }

        // you could not add thumbs_up for yourself
        if (thumbsUpUserId==speakEntity.getUserId()) {
            throw new BadRequestException(ErrorCode.SPEAK_THUMBS_UP_CAN_NOT_FOR_SELF);
        }

        NurseSpeakThumbsUpBean thumbsUpBean = thumbsUpService.addSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
        //TODO need to transfer this thumbs_up to the relative person

        return thumbsUpBean;
    }

    @Transactional
    public void deleteNurseSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        thumbsUpService.deleteNurseSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
    }

    public NurseSpeakThumbsUpBean getNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(long nurseSpeakId, long thumbsUpUserId) {
        return thumbsUpService.findNurseSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
    }
}
