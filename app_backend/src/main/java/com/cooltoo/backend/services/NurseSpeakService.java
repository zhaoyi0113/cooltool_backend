package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.converter.NurseSpeakConverter;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.backend.repository.NurseSpeakRepository;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 3/15/16.
 */
@Service("NurseSpeakService")
public class NurseSpeakService {

    private static final Logger logger = Logger.getLogger(NurseSpeakService.class.getName());

    @Autowired
    private NurseSpeakRepository speakRepository;

    @Autowired
    private NurseSpeakConverter speakConverter;

    @Autowired
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
        if (null==content || "".equals(content)) {
            throw new BadRequestException(ErrorCode.SPEAK_CONTENT_IS_EMPTY);
        }
        if (null==speakType || "".equals(speakType)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (speakType.equalsIgnoreCase(SpeakType.SMUG.toString())) {
            entity.setSpeakType(SpeakType.SMUG);
        }
        else if (speakType.equalsIgnoreCase(SpeakType.CATHART.toString())) {
            entity.setSpeakType(SpeakType.CATHART);
        }
        else if (speakType.equalsIgnoreCase(SpeakType.ASK_QUESTION.toString())) {
            entity.setSpeakType(SpeakType.ASK_QUESTION);
        }
        else {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setUserId(userId);
        entity.setContent(content);
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

    public void deleteNurseSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        thumbsUpService.deleteNurseSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
    }

    public NurseSpeakThumbsUpBean getNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(long nurseSpeakId, long thumbsUpUserId) {
        return thumbsUpService.findNurseSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
    }
}
