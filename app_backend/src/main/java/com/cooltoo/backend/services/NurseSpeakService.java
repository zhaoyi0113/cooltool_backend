package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.converter.NurseSpeakConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseSpeakRepository;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 3/15/16.
 */
@Service("NurseSpeakService")
public class NurseSpeakService {

    private static final Logger logger = Logger.getLogger(NurseSpeakService.class.getName());

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

    public List<NurseSpeakBean> getNurseSpeak(long userId, int index, int number) {
        logger.info("get nurse speak at "+index+" number="+number);
        PageRequest request = new PageRequest(index, number, Sort.Direction.DESC, "time");
        Page<NurseSpeakEntity> entities = speakRepository.findNurseSpeakByUserId(userId, request);
        List<NurseSpeakBean> speaks = new ArrayList<NurseSpeakBean>();
        for (NurseSpeakEntity entity : entities) {
            NurseSpeakBean speak = speakConverter.convert(entity);
            String fileUrl = storageService.getFilePath(entity.getId());
            speak.setImageUrl(fileUrl);
            List<NurseSpeakCommentBean> comments = speakCommentService.getSpeakCommentsByNurseSpeakId(speak.getId());
            speak.setComments(comments);
            List<NurseSpeakThumbsUpBean> thumbsUps = thumbsUpService.getSpeakThumbsUpByNurseSpeakId(speak.getId());
            speak.setThumbsUps(thumbsUps);
            speaks.add(speak);
        }
        return speaks;
    }

    @Transactional
    public List<NurseSpeakBean> getNurseSpeakByType(long userId, String speakType, int index, int number) {
        logger.info("get nurse speak ("+speakType+") at "+index+" number="+number);
        SpeakType speaktype = SpeakType.parseString(speakType);
        if (null==speaktype) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        // get speaks
        PageRequest pagable = new PageRequest(index, number, Sort.Direction.DESC, "time");
        Page<NurseSpeakEntity> speaks = speakRepository.findNurseSpeakByUserIdAndSpeakType(userId, speaktype, pagable);

        // get username
        String nurseName = null;
        NurseEntity nurse = nurseRepository.findOne(userId);
        if (null!=nurse) {
            nurseName = nurse.getName();
        }

        // speak ids/file ids cache
        List<Long> speakIds = new ArrayList<Long>();
        List<Long> fileIds = new ArrayList<Long>();

        // convert to bean
        Map<Long, NurseSpeakBean> speakIdToBeanMap = new Hashtable<Long, NurseSpeakBean>();
        for (NurseSpeakEntity entity : speaks) {
            NurseSpeakBean speak = speakConverter.convert(entity);
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
        for (NurseSpeakEntity entity : speaks) {
            long speakId = entity.getId();
            long imageId = entity.getImageId();
            String imageUrl = idToPath.get(imageId);
            if (VerifyUtil.isStringEmpty(imageUrl)) {
                continue;
            }
            NurseSpeakBean speakBean = speakIdToBeanMap.get(speakId);
            speakBean.setImageUrl(imageUrl);
        }

        // construct return values
        List<NurseSpeakBean> speakBeans = new ArrayList<NurseSpeakBean>();
        for (NurseSpeakEntity entity : speaks) {
            long speakId = entity.getId();
            bean = speakIdToBeanMap.get(speakId);
            List<Object> countTarget = (List)bean.getComments();
            bean.setCommentsCount(null==countTarget ? 0 : countTarget.size());
            countTarget = (List)bean.getThumbsUps();
            bean.setThumbsUpsCount(null==countTarget ? 0 : countTarget.size());
            speakBeans.add(speakIdToBeanMap.get(speakId));
        }
        return speakBeans;
    }

    public long countNurseSpeakByType(long userId, String speakType) {
        logger.info("get nurse speak ("+speakType+") count");
        SpeakType speaktype = SpeakType.parseString(speakType);
        if (null==speaktype) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        long count = speakRepository.countNurseSpeakByUserIdAndSpeakType(userId, speaktype);
        return count;
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

    @Transactional
    public NurseSpeakBean addNurseSpeak(long userId, String content, String speakType, String fileName, InputStream fileInputStream) {
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

        // check speak type
        SpeakType speaktype = SpeakType.parseString(speakType);
        if (null==speaktype) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setUserId(userId);
        entity.setContent(content);
        entity.setSpeakType(speaktype);
        entity.setTime(new Date());
        entity = speakRepository.save(entity);

        NurseSpeakBean bean = speakConverter.convert(entity);
        if (hasImage) {
            bean.setImageUrl(storageService.getFilePath(entity.getImageId()));
        }
        return bean;
    }

    public NurseSpeakCommentBean addSpeakComment(long speakId, long commentMakerId, long commentReceiverId, String comment) {
        NurseSpeakCommentBean commentBean = speakCommentService.addSpeakComment(speakId, commentMakerId, commentReceiverId, comment);
        //TODO need to transfer the comment to the relative person
        //... ...
        return commentBean;
    }

    public long getNurseSpeakCount(long userId){
        return speakRepository.countNurseSpeakByUserId(userId);
    }

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
